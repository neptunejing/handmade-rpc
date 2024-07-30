package com.nanqing.rpc.fault.tolerant;

import com.nanqing.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {
    /**
     * 容错
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
