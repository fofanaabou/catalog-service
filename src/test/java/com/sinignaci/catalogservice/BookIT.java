package com.sinignaci.catalogservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sinignaci.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("integration")
class BookIT extends CatalogServiceApplicationTests {

    private static KeycloakToken bjornToken;
    private static KeycloakToken isabelleToken;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.4")
            .withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri/", () -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
    }

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        isabelleToken = authenticateWith("isabelle", "password", webClient);
        bjornToken = authenticateWith("bjorn", "password", webClient);
    }

    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.builder()
                .isbn("9781234567895")
                .title("Title")
                .author("Author")
                .price(9.90)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        System.out.println(isabelleToken.accessToken());

        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleToken.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPutRequestThenBookUpdated() {
        var bookIsbn = "1231231239";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90).toBuilder().publisher("Polarsophia").build();
        Book createdBook = webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleToken.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        var bookToUpdate = createdBook.toBuilder().price(7.9).build();

        webTestClient
                .put()
                .uri("/books/" + bookIsbn)
                .headers(headers -> headers.setBearerAuth(isabelleToken.accessToken()))
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
                });
    }

    @Test
    void whenDeleteRequestThenBookDeleted() {
        var bookIsbn = "1231231233";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90).toBuilder().publisher("Polarsophia").build();
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleToken.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated();

        webTestClient
                .delete()
                .uri("/books/" + bookIsbn)
                .headers(headers -> headers.setBearerAuth(isabelleToken.accessToken()))
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage ->
                        assertThat(errorMessage).contains("The book with ISBN " + bookIsbn + " was not found.")
                );
    }

    @Test
    void whenPostRequestUnauthorizedThen403() {
        var expectedBook = Book.of("1231231231", "Title", "Author",
                9.90).withPublisher("Polarsophia");

        webTestClient.post().uri("/books")
                .headers(headers ->
                        headers.setBearerAuth(bjornToken.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedBook = Book.of("1231231231", "Title", "Author",
                9.90).withPublisher("Polarsophia");

        webTestClient.post().uri("/books")
    .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private record KeycloakToken(@JsonProperty("access_token") String accessToken) {
    }

    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }
}
