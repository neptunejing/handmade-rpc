package com.nanqing.rpc.springboot.starter.bootstrap;

import com.nanqing.rpc.proxy.ServiceProxyFactory;
import com.nanqing.rpc.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC 服务消费者启动
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    /**
     * Bean 初始化后执行，注入服务
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference == null) continue;
            // 为属性生成代理对象
            Class<?> interfaceClass = rpcReference.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = field.getType();
            }
            field.setAccessible(true);
            Object proxyObj = ServiceProxyFactory.getProxy(interfaceClass);
            try {
                field.set(bean, proxyObj);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("为字段注入代理对象失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
