package com.example.demo.service;

import com.example.demo.congratulator.ColleagueCongratulator;
import com.example.demo.congratulator.FamilyCongratulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CongratulationService2 {
    @Autowired
    private FamilyCongratulator familyCongratulator;
    @Autowired
    private ColleagueCongratulator colleagueCongratulator;

    @PostConstruct
    public void init() {
        familyCongratulator.сongratulateМамаAndПапа();
        colleagueCongratulator.сongratulate();
    }
}
