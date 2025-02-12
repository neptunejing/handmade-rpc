package com.nanqing.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.nanqing.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 出现 Exception 异常时重试
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS)) // 3s 作为重试间隔时间
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // stopAfterAccept 超过最大重复次数时停止
                .withRetryListener(new RetryListener() { // 监听重试回调
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        // 打印当前的重试次数
                        log.info("重试次数：{}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
