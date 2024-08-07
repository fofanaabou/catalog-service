package com.sinignaci.catalogservice.service;

import com.sinignaci.catalogservice.domain.Book;
import com.sinignaci.catalogservice.handler.BookAlreadyExistsException;
import com.sinignaci.catalogservice.handler.BookNotFoundException;

public interface IBook {
    Iterable<Book> viewBookList();
    Book viewBookDetails(String isbn) throws BookNotFoundException;

    Book addBookToCatalog(Book book) throws BookAlreadyExistsException;

    Book editBookDetails(String isbn, Book book);
    void removeBookFromCatalog(String isbn);
}
