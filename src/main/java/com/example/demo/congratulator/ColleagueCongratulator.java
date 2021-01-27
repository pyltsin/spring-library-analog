package com.example.demo.congratulator;

import com.example.demo.spring.annotation.Congratulate;
import com.example.demo.spring.annotation.CongratulateTo;

@Congratulate("С уважением, Пупкин")
public interface ColleagueCongratulator {
    @CongratulateTo("Коллега")
    void сongratulate();
}
