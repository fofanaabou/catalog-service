package com.sinignaci.catalogservice.controller;

import com.sinignaci.catalogservice.domain.Book;
import com.sinignaci.catalogservice.service.IBook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final IBook iBook;

    @GetMapping
    public ResponseEntity<List<Book>> get() {
        return ResponseEntity.ok(iBook.viewBookList());
    }

    @GetMapping("{isbn}")
    public ResponseEntity<Book> getByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(iBook.viewBookDetails(isbn));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Book> post(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(iBook.addBookToCatalog(book), HttpStatus.CREATED);
    }

    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        iBook.removeBookFromCatalog(isbn);
    }

    @PutMapping("{isbn}")
    public ResponseEntity<Book> put(@PathVariable String isbn, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(iBook.editBookDetails(isbn, book));
    }
}