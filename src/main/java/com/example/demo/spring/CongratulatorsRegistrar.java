package com.example.demo.spring;

import com.example.demo.spring.annotation.Congratulate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Map;

public class CongratulatorsRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Congratulate.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(Congratulator.class));
        String basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>(scanner.findCandidateComponents(basePackage));

        for (BeanDefinition candidateComponent : candidateComponents) {
            ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) candidateComponent;
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            Assert.isTrue(annotationMetadata.isInterface(),
                    "Congratulator can only be specified on an interface");

            // verify annotated class is an interface
            Map<String, Object> attributes = annotationMetadata
                    .getAnnotationAttributes(Congratulate.class.getCanonicalName());

            registerBuilder(registry, annotationMetadata, attributes, candidateComponent);
        }
    }

    private void registerBuilder(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
                                 Map<String, Object> attributes, BeanDefinition candidateComponent) {
        String configName = null;
        if (!CollectionUtils.isEmpty(attributes)) {
            configName = "config." + annotationMetadata.getClassName();
            registerConfig(attributes, configName, registry);
        }
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(CongratulationFactoryBean.class);
        definition.addConstructorArgValue(className);
        definition.addConstructorArgValue(configName);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        String aliasName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(candidateComponent, registry);
        String name = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition,
                name, new String[]{aliasName});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void registerConfig(Map<String, Object> attributes, String configName, BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(ConfigCongratulation.class);
        definition.addPropertyValue("sign", attributes.get("value"));

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, configName,
                new String[]{});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                return !Congratulator.class.getCanonicalName().equals(beanDefinition.getMetadata().getClassName());
            }
        };
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
