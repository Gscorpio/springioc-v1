package com.gjl.beans;


/**
 * bean定义注册接口
 * */
public interface BeanDefinitionRegistry {

    /**
     * 注册bean定义
     * */
    void registerBeanDefinition(String name,BeanDefinition beanDefinition) throws Exception;

    /**
     * 获取bean定义
     * */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * bean定义是否已存在
     * */
    boolean containsBeanDefinition(String beanName);
}
