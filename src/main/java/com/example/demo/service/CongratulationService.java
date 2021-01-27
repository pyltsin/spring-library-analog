package com.example.demo.service;

import com.example.demo.congratulator.ColleagueCongratulator;
import com.example.demo.congratulator.FamilyCongratulator;
import com.example.demo.spring.Congratulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CongratulationService {
    @Autowired
    private Congratulator familyCongratulator;
    @Autowired
    private ColleagueCongratulator colleagueCongratulator;

    @PostConstruct
    public void init() {
        ((FamilyCongratulator)familyCongratulator).сongratulateМамаAndПапа();
        colleagueCongratulator.сongratulate();
    }
}
