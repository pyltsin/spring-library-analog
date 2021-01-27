package com.example.demo.spring;

import com.example.demo.spring.annotation.CongratulateTo;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CongratulationFactoryBean extends AbstractFactoryBean<Object> implements ApplicationContextAware {

    public static final String CONGRATULATE = "сongratulate";
    public static final String AND = "And";
    private ApplicationContext applicationContext;

    private final Class<?> type;
    private final String configName;

    public CongratulationFactoryBean(Class<?> type, String configName) {
        this.type = type;
        this.configName = configName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    protected Object createInstance() {
        String sign = null;
        if (configName != null) {
            ConfigCongratulation bean = (ConfigCongratulation) applicationContext.getBean(configName);
            sign = bean.getSign();
        }
        Map<Method, MethodHandler> methodToHandler = new LinkedHashMap<>();
        for (Method method : type.getMethods()) {
            if (!AopUtils.isEqualsMethod(method) &&
                    !AopUtils.isToStringMethod(method) &&
                    !AopUtils.isHashCodeMethod(method) &&
                    !method.getName().startsWith(CONGRATULATE)
            ) {
                throw new UnsupportedOperationException("Method " + method.getName() + " is unsupported");
            }
            String methodName = method.getName();
            if (methodName.startsWith(CONGRATULATE)) {
                if (!"void".equals(method.getReturnType().getCanonicalName())) {
                    throw new UnsupportedOperationException("Congratulate method must return void");
                }
                List<String> members = new ArrayList<>();
                CongratulateTo annotation = method.getAnnotation(CongratulateTo.class);
                if (annotation != null) {
                    members.add(annotation.value());
                }
                members.addAll(Arrays.asList(methodName.replace(CONGRATULATE, "").split(AND)));
                MethodHandler handler = new MethodHandler(sign, members);
                methodToHandler.put(method, handler);
            }
        }

        ProxyFactory pf = new ProxyFactory();
        pf.setInterfaces(type);
        pf.addAdvice((MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();

            if (AopUtils.isToStringMethod(method)) {
                return "proxyCongratulation, target:" + type.getCanonicalName();
            }

            MethodHandler methodHandler = methodToHandler.get(method);
            if (methodHandler != null) {
                methodHandler.congratulate();
                return null;
            }
            return null;
        });

        return pf.getProxy();
    }
}
