package com.sivalabs.ft.features;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(DatabaseConfiguration.class)
class FeatureServiceApplicationTests {

    @Test
    void contextLoads() {}
}
