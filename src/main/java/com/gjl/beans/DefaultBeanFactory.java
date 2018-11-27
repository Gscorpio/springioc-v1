package com.gjl.beans;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Program:springioc-v1
 * @Description:通用Bean工厂
 * @Author:郭金龙
 * @Date:2018-11-27 00:12
 * @Version:V1.0
 **/
public class DefaultBeanFactory implements BeanFactory,BeanDefinitionRegistry {

    private final Log logger = LogFactory.getLog((getClass()));

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<String,Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        //验证参数
        Objects.requireNonNull(name,"注册bean需要给入beanName");
        Objects.requireNonNull(beanDefinition,"注册bean需要给入beanDefinition");
        //检验给入的bean是否合法
        if(beanDefinition.validate()){
            throw new Exception("名字为["+name+"]的bean定义不合法："+beanDefinition);
        }

        //检验是否已经存在了
        if(this.containsBeanDefinition(name)){
            throw  new Exception("名字为["+name+"]的bean定义已经存在："+this.getBeanDefinition(name));
        }

        beanDefinitionMap.put(name,beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String name) throws Exception {
        return this.doGet(name);
    }

    /**
     * 用protected修饰是想让其子类也能访问该方法
     * */
    protected Object doGet(String beanName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Objects.requireNonNull(beanName,"beanName不能为空！");

        Object instance = this.beanMap.get(beanName);
        if(instance != null){
            return instance;
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Objects.requireNonNull(beanDefinition,"beanDefinition不能为空");

        Class<?> type = beanDefinition.getBeanClass();
        if(type != null){
            if(StringUtils.isEmpty(beanDefinition.getFactoryBeanName())){
                // 构造方法来构造对象
                return this.createInstanceByConstructor(beanDefinition);
            }else{
                // 静态工厂方法
                return this.createInstanceByStaticFacoryMethod(beanDefinition);
            }
        }else{
            // 工厂bean方式来构造对象
            return this.createInstanceByFactoryBean(beanDefinition);
        }


    }
    // 构造方法来构造对象
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException {
        return beanDefinition.getBeanClass().newInstance();
    }

    // 静态工厂方法
    private Object createInstanceByStaticFacoryMethod(BeanDefinition bd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = bd.getBeanClass();
        Method m = type.getMethod(bd.getFactoryMethodName(),null);
        return m.invoke(type,null);
    }

    // 工厂bean方式来构造对象
    private Object createInstanceByFactoryBean(BeanDefinition bd) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object factoryBean = this.doGet(bd.getFactoryBeanName());
        Method m = factoryBean.getClass().getMethod(bd.getFactoryMethodName(),null);
        return m.invoke(factoryBean,null);
    }

    /**
     * 执行初始化方法
     * @param  bd bean定义
     * @param  instance bean实例
     * */
    private void doInit(BeanDefinition bd,Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(!StringUtils.isEmpty(bd.getInitMethodName())){
            Method m = instance.getClass().getMethod(bd.getInitMethodName(),null);
            m.invoke(instance,null);
        }
    }
}
