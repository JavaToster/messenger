package com.example.Messenger.util.browser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class Parser {
    public String addAttributeToHtml(String html, String id, String attrName, String attrValue){
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById(id);
        element.attr(attrName, attrValue);
        return doc.text();
    }
}
