package com.webservice.week05.domain;

public class Product {
    private String id;
    private String name;
    private String count;
    private int price;
    private int day;

    public Product() {
    }
    public Product(String id, String name, String count, int price, int day) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.price = price;
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public int getDay() {
        return day;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
