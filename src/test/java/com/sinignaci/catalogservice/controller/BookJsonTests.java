package com.sinignaci.catalogservice.controller;

import com.sinignaci.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception {
        var book = createBook();

        var jsonContent = json.write(book);

        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());
    }

    private static Book createBook() {
        return Book.builder().isbn("9781234567897")
                .title("Good dev")
                .author("Abou FOFNA")
                .price(79.9)
                .build();
    }

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                     "isbn": "9781234567897",
                     "title": "Good dev",
                     "author": "Abou FOFNA",
                     "price": 79.9
                 }
                """;

        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(createBook());
    }
}
