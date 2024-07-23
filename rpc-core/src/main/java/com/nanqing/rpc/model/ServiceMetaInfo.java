package com.nanqing.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务元信息（注册信息）
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    @Builder.Default
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口
     */
    private Integer servicePort;

    /**
     * 服务分组（暂未实现）
     */
    @Builder.Default
    private String serviceGroup = "default";

    /**
     * 获取完整键名
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
        // 后续可包含分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
    }

    /**
     * 获取服务注册节点键名
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, serviceVersion);
    }

    /**
     * 获取完整服务地址
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}

