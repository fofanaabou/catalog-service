package com.sinignaci.catalogservice.demo;

import com.sinignaci.catalogservice.domain.Book;
import com.sinignaci.catalogservice.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test-data")
@RequiredArgsConstructor
public class BookDataLoader implements CommandLineRunner  {

    private final BookRepository bookRepository;
    @Override
    public void run(String... args) {
        loadBookTestData();
    }
    private void loadBookTestData() {
        var book1 =  Book.of("1234567891", "Northern Lights",
                "Lyra Silverstar", 9.90);
        var book2 =  Book.of("1234567892", "Polar Journey",
                "Iorek Polarson", 12.90);
        bookRepository.save(book1);
        bookRepository.save(book2);
    }
}
