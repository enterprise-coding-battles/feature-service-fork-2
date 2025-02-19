package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.DatabaseConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DatabaseConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByCode() {
        Product productByCode = productRepository
                .findByCode("intellij")
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        assertThat(productByCode.getCode())
                .as("Product code does not match condition")
                .isEqualTo("intellij");
    }
}
