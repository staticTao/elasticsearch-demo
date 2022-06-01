package com.example.esjd.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:解析网页
 * @Author: TGP
 * @Date: 2022/5/11 16:31
 **/

public class HtmlParseUtil {

    public static List<Map> getParseHtml(String keywords) throws IOException {
        String URL = "https://search.jd.com/Search?keyword="+URLEncoder.encode(keywords, "UTF-8");;
        Document document = Jsoup.parse(new URL(URL), 30000);
        Element goodsList = document.getElementById("J_goodsList");
        Elements elements = goodsList.getElementsByTag("li");
        List<Map> list = new ArrayList<>();
        for (Element el:elements) {
            Map<String , String> map = new HashMap<>();
            map.put("img",el.getElementsByTag("img").get(0).attr("data-lazy-img"));
            map.put("price",el.getElementsByClass("p-price").get(0).text());
            map.put("name",el.getElementsByClass("p-name").get(0).text());
            list.add(map);
        }
        return list;
    }

}
