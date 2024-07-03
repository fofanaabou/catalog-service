package com.sinignaci.catalogservice.persistence;

import com.sinignaci.catalogservice.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> findAll();
    Optional<Book> findByIsbn(String isbn);
    Book save(Book book);
    boolean existsByIsbn(String isbn);
    void deleteByIsbn(String isbn);
}
