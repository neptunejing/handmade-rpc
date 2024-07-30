package com.nanqing.rpc.fault.tolerant;

import com.nanqing.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理策略 - 记录日志，正常返回一个对象
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("静默处理异常", e);
        return new RpcResponse();
    }
}
