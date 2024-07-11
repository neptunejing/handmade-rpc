package com.nanqing.example.consumer;

import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        // 加载配置
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
    }
}
