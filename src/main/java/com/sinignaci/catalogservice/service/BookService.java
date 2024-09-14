package com.sinignaci.catalogservice.service;

import com.sinignaci.catalogservice.domain.Book;
import com.sinignaci.catalogservice.handler.BookAlreadyExistsException;
import com.sinignaci.catalogservice.handler.BookNotFoundException;
import com.sinignaci.catalogservice.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService implements IBook {

    private final BookRepository bookRepository;

    @Override
    public Iterable<Book> viewBookList() {
        log.info("Find all books.");
        return bookRepository.findAll();
    }

    @Override
    public Book viewBookDetails(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    public Book addBookToCatalog(Book book) {
        if (bookRepository.existsByIsbn(book.isbn())) {
            log.error("The  book with isbn {} already exist", book.isbn());
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepository.save(book);
    }

    @Override
    public Book editBookDetails(String isbn, Book book) {
        return bookRepository.findByIsbn(isbn)
                .map(existingBook -> {
                    var bookToUpdate = updateBook(book, existingBook);
                    return bookRepository.save(bookToUpdate);
                })
                .orElseGet(() -> addBookToCatalog(book));
    }

    private static Book updateBook(Book book, Book existingBook) {
        return existingBook.toBuilder()
                .isbn(existingBook.isbn())
                .title(book.title())
                .price(book.price())
                .author(book.author())
                .build();
    }

    @Override
    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }
}
