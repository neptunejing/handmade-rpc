package com.nanqing.rpc.fault.tolerant;

public interface TolerantStrategyKeys {
    String FAIL_FAST = "failFast"; // 快速恢复
    String FAIL_SAFE = "failSafe"; // 静默处理
}
