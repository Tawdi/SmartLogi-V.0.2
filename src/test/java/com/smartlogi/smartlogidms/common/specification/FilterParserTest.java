package com.smartlogi.smartlogidms.common.specification;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FilterParserTest {

    @BeforeEach
    void setup() {
        // setup if needed later
    }

    @ParameterizedTest(name = "Filter test case #{index} with filters: {0}")
    @MethodSource("filterTestDataProvider")
    void parse_ShouldHandleVariousFilterScenarios(MultiValueMap<String, String> filters) {
        // Act
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Assert
        assertThat(spec).isNotNull();
    }

    static Stream<MultiValueMap<String, String>> filterTestDataProvider() {
        return Stream.of(
                null, // Null filters
                new LinkedMultiValueMap<>(), // Empty filters
                build("search", "john"), // Searchable field
                build("filter", "status:eq:ACTIVE"), // Simple filter
                build("filter", "status:eq:ACTIVE", "priority:gt:3"), // Multiple filters
                build("filter", "user.name:like:john"), // Nested field
                build("filter", "age:gt:18", "name:like:john", "status:neq:INACTIVE", "score:gte:80", "price:lte:100"), // Multiple operations
                build("filter", "age:between:18-65"), // Range
                build("filter", "category:in:ELECTRONICS,CLOTHING"), // IN operation
                build("filter", "createdAt:gt:2024-01-01"), // Date
                build("filter", "updatedAt:gt:2024-01-01T10:00:00"), // DateTime
                build("filter", "weight:gt:5.5"), // Numeric
                build("filter", "invalid-filter", "field:invalidop:value", "valid:eq:value"), // Invalid formats
                build("filter", "name:eq:", "status:eq:   ") // Blank values
        );
    }

    private static MultiValueMap<String, String> build(String key, String... values) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (String v : values) map.add(key, v);
        return map;
    }

    @Searchable(fields = {"name", "description" })
    static class TestEntity {
    }
}
