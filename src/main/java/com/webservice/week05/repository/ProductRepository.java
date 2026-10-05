package com.webservice.week05.repository;

import com.webservice.week05.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    List<Product> findAll();
    Optional<Product> findById(String id);
    Product update(Product product);
    void deleteById(String id);
}
