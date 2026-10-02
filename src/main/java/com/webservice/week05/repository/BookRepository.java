package com.webservice.week05.repository;

import com.webservice.week05.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book update(Book book);
    void deleteById(Long id);
}
