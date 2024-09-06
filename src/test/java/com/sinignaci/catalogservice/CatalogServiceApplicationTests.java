package com.sinignaci.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogServiceApplicationTests {

    @MockBean
    JwtDecoder jwtDecoder;
    @Test
    void contextLoads() {
        // Load context for texting
    }
}
