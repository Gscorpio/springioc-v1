package com.gjl.beans;

public interface BeanFactory {

    /**
     * 获取bean
     * @param  name bean名称
     * @return  bean实例
     * @throws Exception
     * */
    Object getBean(String name) throws Exception;
}
