package com.fund.controller;

import com.fund.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @Autowired
    private FundService fundService;

    @RequestMapping("crawl")
    public String save() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                fundService.showFund();
            }
        }).start();
        return "success";
    }

}
