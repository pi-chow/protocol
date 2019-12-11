package com.cetiti.iotp.protocol.transport.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonResponsePayload;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import com.cetiti.iotp.protocol.transport.mqtt.model.SessionEventEnum;
import com.cetiti.iotp.protocol.transport.mqtt.service.SessionMsgListener;
import com.cetiti.iotp.protocol.transport.mqtt.service.TransportService;
import com.cetiti.iotp.protocol.transport.mqtt.session.MqttDeviceSessionContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED;
import static io.netty.handler.codec.mqtt.MqttMessageType.*;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.FAILURE;

/**
 *
 * MQTT Handler
 * @author zhouliyu
 * @since 2019-12-05 19:03:04
 */
@Slf4j
public class MqttTransportHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<? super Void>>, SessionMsgListener {

    private static final MqttQoS MAX_SUPPORTED_QOS_LVL = AT_LEAST_ONCE;

    private final UUID sessionId;

    private final ConcurrentMap<MqttTopicMatcher, Integer> mqttQoSMap;

    private volatile MqttDeviceSessionContext sessionContext;

    private volatile DeviceInfo deviceInfo;

    private volatile InetSocketAddress address;

    private TransportService transportService;

    public MqttTransportHandler(MqttTransportContext context) {
        this.sessionId = UUID.randomUUID();
        this.mqttQoSMap = new ConcurrentHashMap<>();
        this.sessionContext = new MqttDeviceSessionContext(sessionId, mqttQoSMap);
        this.transportService = context.getTransportService();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.trace("[{}] Processing msg: {}", msg);
        if (msg instanceof MqttMessage) {
            processMqttMsg(ctx, (MqttMessage) msg);
        }else {
            ctx.close();

        }
    }


    private void processMqttMsg(ChannelHandlerContext ctx, MqttMessage msg){

        address = (InetSocketAddress) ctx.channel().remoteAddress();
        if (msg.fixedHeader() == null) {
            log.info("[{}:{}] Invalid message received", address.getAddress(), address.getPort());
            processDisconnect(ctx);
            return;
        }

        sessionContext.setChannel(ctx);
        switch (msg.fixedHeader().messageType()) {

            case CONNECT:
                processConnect(ctx, (MqttConnectMessage) msg);
                break;
            case PUBLISH:
                processPublish(ctx, (MqttPublishMessage)msg);
                break;
            case SUBSCRIBE:
                processSubscribe(ctx, (MqttSubscribeMessage) msg);
                break;
            case UNSUBSCRIBE:
                processUnsubscribe(ctx, (MqttUnsubscribeMessage) msg);
                break;
            case PINGREQ:
                if (checkConnected(ctx, msg)) {
                    ctx.writeAndFlush(new MqttMessage(new MqttFixedHeader(PINGRESP, false, AT_MOST_ONCE, false, 0)));
                    transportService.reportActivity(deviceInfo);
                }
                break;
            case DISCONNECT:
                if(checkConnected(ctx, msg)) {
                    processDisconnect(ctx);
                }
                break;

            default:
                break;
        }



    }


    private void processConnect(ChannelHandlerContext ctx, MqttConnectMessage msg){
        log.info("[{}] processing connect msg for client: {}!", sessionId, msg.payload().clientIdentifier());

        //TODO SSL handler

        processAuthTokenConnect(ctx, msg);

    }

    /**
     * 设备权限处理
     * @param ctx
     * @param msg
     * */
    private void processAuthTokenConnect(ChannelHandlerContext ctx, MqttConnectMessage msg){

        String userName = msg.payload().userName();
        log.info("[{}] processing connect msg for client with user name: {}!", sessionId, userName);
        if (StringUtils.isEmpty(userName)){
            ctx.writeAndFlush(createMqttConnAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            ctx.close();
        }else {

            transportService.process(msg.payload().userName(), new TransportServiceCallback<DeviceInfo>() {
                @Override
                public void onSuccess(DeviceInfo deviceInfo) {
                    onValidateDeviceResponse(deviceInfo, ctx);
                }

                @Override
                public void onError(Throwable throwable) {
                    log.info("[{}] Failed to process credentials: {}", address, userName, throwable);
                    ctx.writeAndFlush(createMqttConnAckMsg(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE));
                    ctx.close();
                }
            });


        }

    }

    /**
     *设备权限处理响应
     * @param deviceInfo
     * @param ctx
     * */
    private void onValidateDeviceResponse(DeviceInfo deviceInfo, ChannelHandlerContext ctx){

        if (deviceInfo == null) {
            ctx.writeAndFlush(createMqttConnAckMsg(CONNECTION_REFUSED_NOT_AUTHORIZED));
            ctx.close();
        }else {
            deviceInfo.setSessionId(sessionId);
            sessionContext.setDeviceInfo(deviceInfo);
            this.deviceInfo = deviceInfo;
            transportService.process(deviceInfo, SessionEventEnum.OPEN, null);
            transportService.registerAsyncSession(deviceInfo, this);
            ctx.writeAndFlush(createMqttConnAckMsg(MqttConnectReturnCode.CONNECTION_ACCEPTED));
            log.info("[{}=>{}] Client connected!", sessionId, deviceInfo);
        }

    }

    private void processDisconnect(ChannelHandlerContext ctx) {
        log.info("[{}:{}] processing disconnect for client", address.getAddress(), address.getPort());
        ctx.close();
        if (sessionContext.isConnected()) {
            transportService.process(deviceInfo, SessionEventEnum.CLOSED, null);
            transportService.deregisterSession(deviceInfo);
        }
        log.info("[{}] Client disconnected!", sessionId);
    }


    public void processPublish(ChannelHandlerContext ctx, MqttPublishMessage msg){

        if (!checkConnected(ctx, msg)) {
            return;
        }

        String topicName = msg.variableHeader().topicName();
        int msgId = msg.variableHeader().packetId();
        log.info("[{}] processing publish msg [{}][{}]!", sessionId, topicName, msgId);

        processDevicePublish(ctx, msg, topicName, msgId);


    }


    /**
     *设备发布信息处理
     * @param ctx
     * @param msg
     * @param msgId
     * @param topicName
     * */
    public void processDevicePublish(ChannelHandlerContext ctx, MqttPublishMessage msg, String topicName, int msgId){

        try {
            log.info("[{}] publish device msg [{}][{}][{}]", sessionId, topicName, msgId, msg.payload().toString(CharsetUtil.UTF_8));
            transportService.process(msg, getPublishCallback(ctx, topicName, msgId));

        }catch (Exception e) {

            log.warn("[{}] Failed to process publish msg [{}][{}]", sessionId, topicName, msgId, e);
            log.info("[{}] Closing current session due to invalid publish msg [{}][{}]", sessionId, topicName, msgId);
            ctx.close();

        }

    }

    private TransportServiceCallback<CommonRequestPayload> getPublishCallback(ChannelHandlerContext ctx, String topicName, int msgId){

        String replyTopic = topicName.concat("_reply");

        return new TransportServiceCallback<CommonRequestPayload>() {
            @Override
            public void onSuccess(CommonRequestPayload commonRequestPayload) {

                CommonResponsePayload<JSONObject> response = new CommonResponsePayload<>();
                response.setId(commonRequestPayload.getId());
                response.setCode(200);
                response.setMsg("success");
                response.setData(new JSONObject());

                ctx.writeAndFlush(createPublishMsg(replyTopic, msgId, response));

            }

            @Override
            public void onError(Throwable throwable) {

                CommonResponsePayload<JSONObject> error = new CommonResponsePayload<>();
                error.setCode(400);
                error.setMsg("failure");
                error.setData(new JSONObject());

                ctx.writeAndFlush(createPublishMsg(replyTopic, msgId, error));

            }
        };

    }

    public void processSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage msg) {
        if(!checkConnected(ctx, msg)){
            return;
        }

        log.info("[{}] processing subscription [{}]", sessionId, msg.variableHeader().messageId());

        List<Integer> grantedQoSList = new ArrayList<>();

        for (MqttTopicSubscription subscription : msg.payload().topicSubscriptions()) {

            String topic = subscription.topicName();
            MqttQoS reqQoS = subscription.qualityOfService();

            try {
                registerSubQoS(topic, grantedQoSList, reqQoS);
            }catch (Exception e) {

                log.warn("[{}] Failed to subscribe to [{}][{}]", sessionId, topic, reqQoS);
                grantedQoSList.add(FAILURE.value());

            }

        }
        ctx.writeAndFlush(createMqttSubAckMsg(msg.variableHeader().messageId(), grantedQoSList));
    }

    private void processUnsubscribe(ChannelHandlerContext ctx, MqttUnsubscribeMessage msg){
        if(!checkConnected(ctx, msg)){
            return;
        }

        log.info("[{}] processing unsubscribe [{}]", sessionId, msg.variableHeader().messageId());

        for (String topicName : msg.payload().topics()) {

            mqttQoSMap.remove(new MqttTopicMatcher(topicName));

            //TODO 业务逻辑
        }
        ctx.writeAndFlush(createUnSubAckMessage(msg.variableHeader().messageId()));
    }



    private void registerSubQoS(String topic, List<Integer> grantedQoSList, MqttQoS reqQoS) {
        grantedQoSList.add(getMinSupportedQos(reqQoS));
        mqttQoSMap.put(new MqttTopicMatcher(topic), getMinSupportedQos(reqQoS));
    }

    private int getMinSupportedQos(MqttQoS reqQos) {
        return Math.min(reqQos.value(), MAX_SUPPORTED_QOS_LVL.value());
    }

    private MqttConnAckMessage createMqttConnAckMsg(MqttConnectReturnCode returnCode) {
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(CONNACK, false, AT_MOST_ONCE, false, 0);
        MqttConnAckVariableHeader mqttConnAckVariableHeader =
                new MqttConnAckVariableHeader(returnCode, true);
        return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
    }

    private MqttSubAckMessage createMqttSubAckMsg(Integer msgId, List<Integer> grantedQosList){
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(SUBACK, false, AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(msgId);
        MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(grantedQosList);
        return new MqttSubAckMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubAckPayload);

    }

    private MqttMessage createUnSubAckMessage(int msgId) {
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(UNSUBACK, false, AT_LEAST_ONCE, false, 0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(msgId);
        return new MqttMessage(mqttFixedHeader, mqttMessageIdVariableHeader);
    }

    private MqttPublishMessage createPublishMsg(String topic, int msgId, CommonResponsePayload payload){

        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(PUBLISH, false, AT_MOST_ONCE, false, 0);
        MqttPublishVariableHeader mqttPublishVariableHeader =
                new MqttPublishVariableHeader(topic, msgId);

        ByteBuf buf = Unpooled.copiedBuffer(JSON.toJSONString(payload).getBytes());

        return new MqttPublishMessage(mqttFixedHeader, mqttPublishVariableHeader, buf);
    }

    private boolean checkConnected(ChannelHandlerContext ctx, MqttMessage msg){

        if (sessionContext.isConnected()) {
            return true;
        }else {
            log.info("[{}] closing current session due to invalid msg order {}!", sessionId, msg);
            ctx.close();
            return false;
        }

    }


    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {

        if (sessionContext.isConnected()) {
            transportService.process(deviceInfo, SessionEventEnum.CLOSED, null);
            transportService.deregisterSession(deviceInfo);
            sessionContext.setDeviceInfo(null);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[{}] Unexpected Exception", sessionId, cause);
        ctx.close();
    }

    @Override
    public void onRemoteSessionCloseCommand(UUID sessionId) {
        log.info("[{}] received the remote command to closed the session!", sessionId);
        processDisconnect(sessionContext.getChannel());
    }
}
