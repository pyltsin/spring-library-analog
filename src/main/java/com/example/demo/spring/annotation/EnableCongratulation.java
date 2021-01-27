package com.example.demo.spring.annotation;

import com.example.demo.spring.CongratulatorsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(CongratulatorsRegistrar.class)
public @interface EnableCongratulation {
}
