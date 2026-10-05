package com.webservice.week05.dto;

import com.webservice.week05.domain.Product;

public record ProductResponse(String id, String name, String category, String count, int price, int day) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getCount(),
                product.getPrice(),
                product.getDay()
        );
    }
}
