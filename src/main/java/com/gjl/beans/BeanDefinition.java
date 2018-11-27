package com.gjl.beans;

import org.springframework.util.StringUtils;

/**
 * Bean定义接口
 * */
public interface BeanDefinition {
    String SCOPE_SINGLETION = "singletion";

    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 类
     * */
    Class<?> getBeanClass();

    /**
     * Scope
     * */
    String getScope();

    /**
     * 是否单例
     * */
    boolean isSingletion();

    /**
     * 是否原型
     * */
    boolean isPrototype();

    /**
     * 工厂Bean名
     * */
    String getFactoryBeanName();

    /**
     * 工厂方法名
     * */
    String getFactoryMethodName();

    /**
     * 初始化方法
     * */
    String getInitMethodName();

    /**
     * 销毁方法
     * */
    String getDestoryMethodName();

    /**
     * 校验Bean定义是否合法
     * */
    default  boolean validate(){
        //没定义bean的class，有可能是通过工厂创建bean
        if(this.getBeanClass() == null){
            //如果没定义bean的class,也没定义工厂名域工厂方法名。则无法创建bean
            if(StringUtils.isEmpty(this.getFactoryBeanName()) || StringUtils.isEmpty(this.getFactoryMethodName())){
                return false;
            }
        }
        //即定义了类又定义了工厂bean则不合法。
        if(this.getBeanClass() != null && StringUtils.isEmpty(this.getFactoryBeanName())){
            return false;
        }
        return true;
    }
}
