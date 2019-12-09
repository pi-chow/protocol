package com.cetiti.iotp.protocol.transport.mqtt.service;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouliyu
 * @since 2019-12-06 17:01:41
 */
@Slf4j
public abstract class AbstractTransportService implements TransportService {

    /**
     * 回调函数线程
     * */
    protected ExecutorService transportCallbackExecutor;


    public void init(){

        transportCallbackExecutor = Executors.newWorkStealingPool(10);
    }

    public void destory(){

        if (transportCallbackExecutor != null) {

            transportCallbackExecutor.shutdownNow();
        }
    }
}
