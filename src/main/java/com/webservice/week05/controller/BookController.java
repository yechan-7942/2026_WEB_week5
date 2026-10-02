package com.webservice.week05.controller;

import com.webservice.week05.dto.*;
import com.webservice.week05.repository.BookRepository;
import com.webservice.week05.service.BookService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) { this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
        BookResponse response = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }
    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }




    // TODO 11: POST /api/books - 생성 후 201 Created 반환 -> ok
    // TODO 12: GET /api/books - 전체 조회- > ok
    // TODO 13: GET /api/books/{id} - 단건 조회->ok
    // TODO 14: PUT /api/books/{id} - 수정->ok
    // TODO 15: DELETE /api/books/{id} - 삭제 후 204 No Content 반환
}
