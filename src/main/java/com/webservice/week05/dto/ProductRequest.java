package com.webservice.week05.dto;

public record ProductRequest(
        String name,
        String count,
        int price,
        int day
) {
}
