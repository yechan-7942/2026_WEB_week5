package com.webservice.week05.service;

import com.webservice.week05.domain.Book;
import com.webservice.week05.dto.*;
import com.webservice.week05.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository repository;
    public BookService(BookRepository repository) { this.repository = repository; }

    public BookResponse create(BookRequest request) {
        Book book = new Book(null,request.title(),request.author(),request.price());
        Book saved = repository.save(book);
        return new BookResponse(saved.getId(), saved.getTitle(), saved.getAuthor(), saved.getPrice());
    }
    public List<BookResponse> findAll() {
        return repository.findAll().stream().map(book -> new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice())).toList();
        // TODO 7: 모든 Book을 BookResponse 목록으로 변환하여 반환

    }

    public BookResponse findById(Long id) {
        Optional<Book> book = repository.findById(id);

        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return new BookResponse(book.get().getId(), book.get().getTitle(), book.get().getAuthor(), book.get().getPrice());
        // TODO 8: 없는 id는 404 NOT_FOUND, 있으면 BookResponse 반환

    }
    public BookResponse update(Long id, BookRequest request) {
        Optional<Book> book = repository.findById(id);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        Book newBook= new Book(id,request.title(),request.author(),request.price());
        Book updated = repository.update(newBook);
        return new BookResponse(updated.getId(), updated.getTitle(), updated.getAuthor(), updated.getPrice());
        // TODO 9: 존재 여부 확인 -> 값 변경 -> repository.update() -> 응답 반환
    }
    public void delete(Long id) {
        Optional<Book> book = repository.findById(id);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        repository.deleteById(id);
        // TODO 10: 존재 여부 확인 후 삭제. 없는 id는 404 NOT_FOUND
    }
}
