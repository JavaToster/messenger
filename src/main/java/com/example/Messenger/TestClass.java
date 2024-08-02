package com.example.Messenger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestClass {
    public static void main(String[] args){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

        HttpEntity<String> entity = new HttpEntity<>("body", httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange("https://kazan.hh.ru/search/vacancy?text=Java+junior", HttpMethod.GET, entity, String.class);

        List<Vacancy> vacancies = new ArrayList<>();

        Document doc = Jsoup.parse(response.getBody());
        Elements divElements = doc.getElementsByClass("vacancy-card--z_UXteNo7bRGzxWVcL7y font-inter");
        for(Element element: divElements) {
            Vacancy vacancy = new Vacancy();
            String vacancyName = element.getElementsByClass("bloko-header-section-2").getFirst().text();
            String salary = element.getElementsByClass("bloko-text").getFirst().text();
            String url = element.getElementsByClass("bloko-link").getFirst().attr("href");
            vacancy.setVacancyName(vacancyName);
            vacancy.setSalary(salary);
            vacancy.setUrl(url);
            vacancies.add(vacancy);
        }

        for(Vacancy vacancy: vacancies){
            System.out.println(vacancy.getVacancyName() +" - "+ vacancy.getSalary() + " - "+ vacancy.getUrl());
        }
    }
}

class Vacancy{
    private String vacancyName;
    private String salary;
    private String url;

    public String getVacancyName() {
        return vacancyName;
    }

    public void setVacancyName(String vacancyName) {
        this.vacancyName = vacancyName;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Vacancy(){

    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "vacancyName='" + vacancyName + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
