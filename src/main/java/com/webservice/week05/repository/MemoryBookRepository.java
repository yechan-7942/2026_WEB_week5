package com.webservice.week05.repository;

import com.webservice.week05.domain.Book;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MemoryBookRepository implements BookRepository {
    private final Map<Long, Book> store = new LinkedHashMap<>();
    private long sequence = 0L;

    @Override
    public Book save(Book book) {
        Long newId= sequence++;
        book.setId(newId);
        store.put(newId, book);
        return book;
    }
    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }
    @Override
    public Optional<Book> findById(Long id) {
        Book book = store.get(id);
        return Optional.ofNullable(book);
    }
    @Override
    public Book update(Book book) {
        store.put(book.getId(), book);
        return book;
    }
    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
