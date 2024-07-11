package com.nanqing.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {
    /**
     * 加载配置对象
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        StringBuilder configNameBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configNameBuilder.append("-").append(environment);
        }
        configNameBuilder.append(".properties");
        Props props = new Props(configNameBuilder.toString());
        return props.toBean(clazz, prefix);
    }
}
