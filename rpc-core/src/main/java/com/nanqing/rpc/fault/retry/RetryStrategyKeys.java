package com.nanqing.rpc.fault.retry;

public interface RetryStrategyKeys {
    String NO = "no"; // 不重试
    String FIXED_INTERVAL = "fixedInterval"; // 固定间隔
}
