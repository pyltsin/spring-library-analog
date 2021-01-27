package com.example.demo.spring;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MethodHandler {
    private final String sing;
    private final List<String> member;


    public MethodHandler(String sing, List<String> member) {
        if (sing == null || sing.isBlank()) {
            this.sing = "Всегда ваш";
        } else {
            this.sing = sing;
        }
        this.member = member;
    }

    public void congratulate() {
        String text = member.stream().filter(StringUtils::hasText)
                .collect(Collectors.joining(",")) +
                "! " +
                "Поздравляю с Новым годом! " +
                sing;
        System.out.println(text);
    }
}
