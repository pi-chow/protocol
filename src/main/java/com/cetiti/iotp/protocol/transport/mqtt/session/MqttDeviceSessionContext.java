package com.cetiti.iotp.protocol.transport.mqtt.session;

import com.cetiti.iotp.protocol.transport.mqtt.MqttTopicMatcher;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * @author zhouliyu
 * @since 2019-12-05 19:51:40
 */
public class MqttDeviceSessionContext {

    @Getter
    private final UUID sessionId;

    @Getter
    private final ConcurrentMap<MqttTopicMatcher, Integer> mqttQosMap;

    @Getter
    private volatile DeviceInfo deviceInfo;

    @Getter
    private ChannelHandlerContext channel;

    private AtomicInteger msgIdSeq = new AtomicInteger(0);

    public MqttDeviceSessionContext(UUID sessionId, ConcurrentMap<MqttTopicMatcher, Integer> mqttQosMap) {
        this.sessionId = sessionId;
        this.mqttQosMap = mqttQosMap;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    public int nextMsgId() {
        return msgIdSeq.incrementAndGet();
    }

    public MqttQoS getQosForTopic(String topic){

       List<Integer> qosList = mqttQosMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(topic))
                .map(Map.Entry::getValue)
                .collect(toList());
       if (!qosList.isEmpty()){
           return MqttQoS.valueOf(qosList.get(0));
       }else {
           return MqttQoS.AT_LEAST_ONCE;
       }
    }

    public boolean isConnected(){

        return deviceInfo != null;
    }

}
