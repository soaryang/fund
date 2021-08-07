package com.fund.common;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HttpComponent {


    @Bean
    public HttpClient initHttpClient() {
        HttpClient client = new DefaultHttpClient();
        return client;
    }


    public String doGet(String url, Map<String, String> headersMap) {
        String result = StringUtil.EMPTY_STRING;
        HttpClient client = initHttpClient();
        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        request.addHeader("Host", "fund.eastmoney.com");
        if (!headersMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            HttpResponse response = client.execute(request);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
                return result;
            }
        } catch (IOException e) {
            log.error("doGet occur error", e);
        }
        return result;
    }

}
