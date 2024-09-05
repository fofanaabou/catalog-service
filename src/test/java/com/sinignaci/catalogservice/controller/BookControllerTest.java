package com.sinignaci.catalogservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinignaci.catalogservice.config.SecurityConfig;
import com.sinignaci.catalogservice.domain.Book;
import com.sinignaci.catalogservice.handler.BookNotFoundException;
import com.sinignaci.catalogservice.service.IBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerTest {

    public static final String ROLE_EMPLOYEE = "ROLE_employee";
     public static final String ROLE_CUSTOMER = "ROLE_customer";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private IBook iBook;

    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {

        String isbn = "73737313940";
        given(iBook.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }


    @Test
    void when_delete_book_with_employee_role_then_should_return_2024() throws Exception {

        var isbn = "73737313982";

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/books/" + isbn)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_employee"))))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void when_delete_book_with_customer_role_then_should_return_403() throws Exception {

        var isbn = "73737313982";

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + isbn)
                .with(jwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_customer"))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    @Test
    void when_delete_book_not_authorized_then_should_return_401() throws Exception {

        var isbn = "73737313982";

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + isbn))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    void whenPostBookWithEmployeeRoleThenShouldReturn201() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90).withPublisher("Polarsophia");
        given(iBook.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
                .andExpect(status().isCreated());
    }

    @Test
    void whenPostBookWithCustomerRoleThenShouldReturn403() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90).withPublisher("Polarsophia");
        given(iBook.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPostBookAndNotAuthenticatedThenShouldReturn403() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90).withPublisher("Polarsophia");
        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenPutBookWithEmployeeRoleThenShouldReturn200() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90).withPublisher("Polarsophia");
        given(iBook.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
        mockMvc
                .perform(put("/books/" + isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutBookWithCustomerRoleThenShouldReturn403() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90).withPublisher("Polarsophia");
        given(iBook.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
        mockMvc
                .perform(put("/books/" + isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate))
                        .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPutBookAndNotAuthenticatedThenShouldReturn401() throws Exception {
        var isbn = "7373731394";
        var bookToCreate = Book.of(isbn, "Title", "Author", 9.90) .withPublisher("Polarsophia");
        mockMvc
                .perform(put("/books/" + isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate)))
                .andExpect(status().isUnauthorized());
    }
}