package com.nanqing.rpc.springboot.starter.annotation;

import com.nanqing.rpc.springboot.starter.bootstrap.BootstrapperSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动 RPC 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({BootstrapperSelector.class})
// @Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 是否启动 server（默认启动）
     *
     * @return
     */
    boolean needServer() default true;
}
