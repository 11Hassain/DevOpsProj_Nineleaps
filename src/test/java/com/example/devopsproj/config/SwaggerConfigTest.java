package com.example.devopsproj.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.jupiter.api.Assertions.*;

public class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    void testApi() {
        // Act
        Docket docket = swaggerConfig.api();

        // Assert
        assertNotNull(docket);
        assertEquals(DocumentationType.SWAGGER_2, docket.getDocumentationType());
        assertNotNull(docket.select());
        assertNotNull(docket.select().apis(RequestHandlerSelectors.any()));
        assertNotNull(docket.select().paths(PathSelectors.any()));
    }
}
