package com.fund.service;


import com.alibaba.fastjson.JSON;
import com.fund.common.HttpComponent;
import com.fund.dao.FundDao;
import com.fund.util.HtmlElementEnum;
import com.fund.util.HtmlUtil;
import com.fund.vo.Fund;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@Service
public class FundService {


    @Autowired
    private HttpComponent httpComponent;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    public static final String fundListUrl = "https://fundapi.eastmoney.com/fundtradenew.aspx?ft=%s&sc=1n&st=desc" +
            "&pi=%s" +
            "&pn=%s" +
            "&cp=&ct=&cd=&ms=&fr=&plevel=&fst=&ftype=&fr1=&fl=0&isab=1";

    public static final String fundInfoUrl = "http://fundf10.eastmoney.com/gmbd_%s.html";

    @Autowired
    private FundDao fundDao;


    public List<Fund> search(String fundName) {
//        // 创建一个查询条件对象
////        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
////        // 拼接查询条件
////        //queryBuilder.should(QueryBuilders.termQuery("fundName", fundName));
////        queryBuilder.should(QueryBuilders.termsQuery("manager", "施红俊"));
////        // 创建聚合查询条件
////        // 创建查询对象
////        SearchQuery build = new NativeSearchQueryBuilder()
////                .withQuery(queryBuilder) //添加查询条件
////                .withPageable(PageRequest.of(0, 10)) //符合查询条件的文档分页（不是聚合的分页）
////                .build();
////
////        // 执行查询
////        AggregatedPage<Fund> testEntities = elasticsearchTemplate.queryForPage(build, Fund.class);
////
////        List<Fund> fundList = testEntities.getContent();
////
////        return fundList;

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("fundName", fundName));
        queryBuilder.must(QueryBuilders.matchQuery("manager", "吴逸"));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(0, 10)).build();
        return elasticsearchTemplate.queryForList(searchQuery, Fund.class);

    }

    public void showFund() {
        Map<String, String> headMap = Maps.newHashMap();
        headMap.put("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        headMap.put("Host", "fund.eastmoney.com");
        String type = "zs";
        String url = String.format(fundListUrl, type, 1, 100);

        String result = httpComponent.doGet(url, headMap);
        if (StringUtils.isEmpty(result)) {

        }
        //Map maps = JSON.parseObject(result, Map.class);
        Map maps = (Map) JSON.parse(result.substring(result.indexOf("{"), result.length() - 1));
        int allPages = Integer.parseInt(String.valueOf(maps.get("allPages")));
        int pageNum = Integer.parseInt(String.valueOf(maps.get("pageNum")));
        //int allRecords = Integer.parseInt(String.valueOf(maps.get("allRecords")));
        for (int i = 1; i <= allPages; i++) {
            saveFund(type, i, pageNum, headMap);
        }
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
        headMap.put("Host", "fund.eastmoney.com");
        String tempResult = httpComponent.doGet(url, headMap);
        Map tempMap = (Map) JSON.parse(tempResult.substring(tempResult.indexOf("{"), tempResult.length() - 1));
        List<String> list = JSON.parseArray(String.valueOf(tempMap.get("datas")), String.class);
        list.stream().filter(e -> {
            String id = e.split("\\|")[0];
            Fund fund = fundDao.findByCode(id);
            if (fund == null) {
                return true;
            }
            log.info("code is exist,{}", id);
            return false;
        }).forEach(e -> {

            String id = e.split("\\|")[0];

            try {
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Fund fundBean = new Fund();
            String fundName = e.split("\\|")[1];
            fundBean.setFundName(fundName);

            fundBean.setId(atomicLong.incrementAndGet());
            fundBean.setCode(id);
            log.info("fundBean:{}", JSON.toJSONString(fundBean));
            fundBean.setType(type);
            getFundBaseInfo(fundBean);
            //fundList.add(fundBean);
            fundDao.save(fundBean);

        });
    }

    public void getFundBaseInfo(Fund fund) {
        String url = String.format(fundInfoUrl, fund.getCode());
        String tempResult = httpComponent.doGet(url, Maps.newHashMap());
        if (StringUtils.isEmpty(tempResult)) {
            return;
        }
        Document document = Jsoup.parse(tempResult);
        Elements elements = document.getElementsByClass("bs_gl");
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        Element element = elements.get(0);
        Elements trElements = element.getElementsByTag(HtmlElementEnum.P.name());
        Element elementFirst = trElements.get(0);
        if (elementFirst != null) {
            Elements tdElements = elementFirst.getElementsByTag(HtmlElementEnum.LABEL.name());
            for (Element tempElement : tdElements) {
                String html = HtmlUtil.removeBlank(tempElement.html());
                if (html.contains("成立日期")) {
                    fund.setEstablishedTime(HtmlUtil.getHtmlElementHtmlOne(tempElement, HtmlElementEnum.SPAN.name()));
                } else if (html.contains("基金经理")) {
                    fund.setManager(HtmlUtil.getHtmlElementHtmlList(tempElement, HtmlElementEnum.A.name()));
                } else if (html.contains("类型")) {
                    fund.setTypeName(HtmlUtil.getHtmlElementHtmlOne(tempElement, HtmlElementEnum.SPAN.name()));
                } else if (html.contains("管理人")) {
                    fund.setManagerCompany(HtmlUtil.getHtmlElementHtmlOne(tempElement, HtmlElementEnum.A.name()));
                } else if (html.contains("资产规模")) {
                    fund.setScale(HtmlUtil.getHtmlElementHtmlOne(tempElement, HtmlElementEnum.SPAN.name()));
                }

            }
        }
    }
}
