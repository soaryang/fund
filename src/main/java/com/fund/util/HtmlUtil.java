package com.fund.util;

import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class HtmlUtil {

    public static String removeBlank(String str) {
        return str.replace(" ", "").replace("&nbsp;", "");
    }

    public static String getHtmlElementHtmlOne(Element element, String tag) {
        Elements elementElementsByTag = element.getElementsByTag(tag);
        if (CollectionUtils.isEmpty(elementElementsByTag)) {
            return StringUtil.EMPTY_STRING;
        }
        return elementElementsByTag.get(0).html();
    }

    public static List<String> getHtmlElementHtmlList(Element element, String tag) {
        Elements elementElementsByTag = element.getElementsByTag(tag);
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(elementElementsByTag)) {
            return list;
        }
        for(Element temp:elementElementsByTag){
            list.add(temp.html());
        }
        return list;
    }
}
