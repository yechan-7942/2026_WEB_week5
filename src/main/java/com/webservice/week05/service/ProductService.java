package com.webservice.week05.service;

import com.webservice.week05.domain.Product;
import com.webservice.week05.dto.ProductRequest;
import com.webservice.week05.dto.ProductResponse;
import com.webservice.week05.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(null, request.name(), request.count(), request.price(), request.day());
        return ProductResponse.from(repository.save(product));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductResponse::from).toList();
    }

    public ProductResponse findById(String id) {
        return ProductResponse.from(findProduct(id));
    }

    public ProductResponse update(String id, ProductRequest request) {
        Product product = findProduct(id);
        product.setName(request.name());
        product.setCount(request.count());
        product.setPrice(request.price());
        product.setDay(request.day());
        return ProductResponse.from(repository.update(product));
    }

    public void delete(String id) {
        findProduct(id);
        repository.deleteById(id);
    }

    private Product findProduct(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }
}
