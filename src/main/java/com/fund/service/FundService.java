package com.fund.service;


import com.alibaba.fastjson.JSON;
import com.fund.common.HttpComponent;
import com.fund.dao.FundDao;
import com.fund.vo.Fund;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@Service
public class FundService {


    @Autowired
    private HttpComponent httpComponent;


    public static final String fundListUrl = "https://fundapi.eastmoney.com/fundtradenew.aspx?ft=%s&sc=1n&st=desc" +
            "&pi=%s" +
            "&pn=%s" +
            "&cp=&ct=&cd=&ms=&fr=&plevel=&fst=&ftype=&fr1=&fl=0&isab=1";

    @Autowired
    private FundDao fundDao;

    public void showFund() {
        Map<String, String> headMap = Maps.newHashMap();
        headMap.put("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        String type = "zs";
        String url = String.format(fundListUrl, type, 1, 100);
        String result = httpComponent.doGet(url, headMap);
        if (StringUtils.isEmpty(result)) {

        }
        //Map maps = JSON.parseObject(result, Map.class);
        Map maps = (Map) JSON.parse(result.substring(result.indexOf("{"), result.length() - 1));
        int allPages = Integer.parseInt(String.valueOf(maps.get("allPages")));
        int pageNum = Integer.parseInt(String.valueOf(maps.get("pageNum")));
        int allRecords = Integer.parseInt(String.valueOf(maps.get("allRecords")));
        Stream.of(allPages).forEach(e -> {
            saveFund(type, e, pageNum, headMap);
            //log.info(JSON.toJSONString(fundList));
        });
    }

    public void saveFund(String type, int index, int pageNum, Map<String, String> headMap) {

        Date date = new Date();
        AtomicLong atomicLong = new AtomicLong(date.getTime());
        String url = String.format(fundListUrl, type, index, pageNum);
        try {
            Thread.sleep(new Random().nextInt(5000));
        } catch (InterruptedException e1) {
            log.error("thread interrupt error", e1);
        }
        String tempResult = httpComponent.doGet(url, headMap);
        Map tempMap = (Map) JSON.parse(tempResult.substring(tempResult.indexOf("{"), tempResult.length() - 1));
        List<String> list = JSON.parseArray(String.valueOf(tempMap.get("datas")), String.class);
        list.stream().forEach(e -> {
            try {
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Fund fundBean = new Fund();
            String fundName = e.split("\\|")[1];
            fundBean.setFundName(fundName);
            String id = e.split("\\|")[0];
            fundBean.setId(atomicLong.incrementAndGet());
            fundBean.setCode(id);
            log.info("fundBean:{}", JSON.toJSONString(fundBean));
            fundBean.setType(type);
            //fundList.add(fundBean);
            fundDao.save(fundBean);
        });
    }
}
