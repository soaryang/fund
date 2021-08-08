package com.fund.controller;

import com.fund.service.FundService;
import com.fund.vo.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {


    @Autowired
    private FundService fundService;


    @RequestMapping("search")
    public List<Fund> search(String fundName) {
       return fundService.search(fundName);

    }

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
