package com.cetiti.iotp.protocol.transport.mqtt;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author zhouliyu
 * @since 2019-12-06 14:00:16
 */
public class AsyncCallbackTemplate {

    public static <T> void withCallbackAndTimeout(ListenableFuture<T> future,
                                                 Consumer<T> onSuccess,
                                                 Consumer<Throwable> onFailure,
                                                 long timeoutInMs,
                                                 ScheduledExecutorService timeoutExecutor,
                                                 Executor callbackExecutor){

        future = Futures.withTimeout(future, timeoutInMs, TimeUnit.MILLISECONDS, timeoutExecutor);
        withCallback(future, onSuccess, onFailure, callbackExecutor);
    }


    public static <T> void withCallback(ListenableFuture<T> future, Consumer<T> onSuccess, Consumer<Throwable> onFailure, Executor executor){

        FutureCallback<T> callback = new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                try {
                    onSuccess.accept(result);
                }catch (Throwable throwable){
                    onFailure.accept(throwable);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                    onFailure.accept(throwable);
            }
        };

        if (executor != null) {

            Futures.addCallback(future, callback, executor);

        }else {

            Futures.addCallback(future, callback);
        }

    }

}
