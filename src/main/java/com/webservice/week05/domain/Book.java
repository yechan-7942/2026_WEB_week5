package com.webservice.week05.domain;

public class Book {
    private Long id;
    private String title;
    private String author;
    private int price;

    public Book() {}
    public Book(Long id, String title, String author, int price) {
        this.id=id; this.title=title; this.author=author; this.price=price;
    }
    public Long getId(){ return id; }
    public void setId(Long id){ this.id=id; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title=title; }
    public String getAuthor(){ return author; }
    public void setAuthor(String author){ this.author=author; }
    public int getPrice(){ return price; }
    public void setPrice(int price){ this.price=price; }
}
