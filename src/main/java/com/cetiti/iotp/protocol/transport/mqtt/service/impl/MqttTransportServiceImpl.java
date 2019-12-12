package com.cetiti.iotp.protocol.transport.mqtt.service.impl;

import com.alibaba.fastjson.JSON;
import com.cetiti.iotp.protocol.transport.mqtt.AsyncCallbackTemplate;
import com.cetiti.iotp.protocol.transport.mqtt.TransportServiceCallback;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import com.cetiti.iotp.protocol.transport.mqtt.enums.SessionEventEnum;
import com.cetiti.iotp.protocol.transport.mqtt.service.AbstractTransportService;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import com.google.common.util.concurrent.*;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author zhouliyu
 * @since 2019-12-06 13:57:20
 */
@Slf4j
@Service
public class MqttTransportServiceImpl extends AbstractTransportService {

    private ListeningExecutorService service;


    @Override
    @PostConstruct
    public void init(){

        this.service = MoreExecutors.listeningDecorator(
                Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {

                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("transportServiceImpl-thread-pool");

                        return thread;
                    }
                })
        );

        super.init();

    }


    @Override
    public void process(String userName, TransportServiceCallback<DeviceInfo> callback) {
        log.info("processing msg: {}", userName);
        AsyncCallbackTemplate.withCallback(processAuthToken(userName),
                callback::onSuccess,
                callback::onError,
                transportCallbackExecutor);
    }

    private ListenableFuture<DeviceInfo> processAuthToken(String userName){

        log.info("processing authToken [{}]", userName);

        return findByIdAsync(userName);

    }



    @Override
    protected void doProcess(DeviceInfo deviceInfo, SessionEventEnum sessionEvent, TransportServiceCallback<Void> callback) {

        log.info("[{}] processing [{}]", deviceInfo, sessionEvent);

    }

    @Override
    protected void doProcess(DeviceInfo deviceInfo, MqttPublishMessage msg, TransportServiceCallback<CommonRequestPayload> callback) {

        log.info("[{}] processing message", deviceInfo);

        AsyncCallbackTemplate.withCallback(processPublish(msg),
                callback::onSuccess,
                callback::onError,
                transportCallbackExecutor);
    }

    /**
     * Async 异步查询-可用于大数据量查询
     * */
    private ListenableFuture<DeviceInfo> findByIdAsync(String userName){

        log.info("get entity by key async {}", userName);

        return service.submit(new Callable<DeviceInfo>() {

            @Override
            public DeviceInfo call() throws Exception {

                return DeviceInfo.parse(userName);
            }
        });

    }

    private <response> ListenableFuture processPublish(MqttPublishMessage mqttPublishMessage){

        SettableFuture<response> future = SettableFuture.create();

        String payload = mqttPublishMessage.payload().toString(CharsetUtil.UTF_8);

        if (StringUtils.isEmpty(payload)) {
            future.set((response) new NullPointerException("publish msg"));
        }else {
            CommonRequestPayload commonRequestPayload = new CommonRequestPayload();
            try {
                commonRequestPayload = JSON.parseObject(payload, CommonRequestPayload.class);
                future.set((response) commonRequestPayload);
            }catch (Throwable throwable) {
                future.set((response) throwable);
            }
            log.info("device payload : [{}]", commonRequestPayload.toString());
        }

        return future;

    }


    @PreDestroy
    public void shutdown(){

        super.destory();

        if (service !=null) {
            service.shutdownNow();
        }
    }

}
