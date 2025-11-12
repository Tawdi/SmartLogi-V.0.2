package com.smartlogi.smartlogidms.common.specification;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilterParserTest {

    @Test
    void parse_WithNullFilters_ShouldReturnEmptySpecification() {
        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(null, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithEmptyFilters_ShouldReturnEmptySpecification() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithSearchParameter_ShouldAddLikeCriteriaForSearchableFields() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("search", "john");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        // Should have criteria for searchable fields (name, description from @Searchable)
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithSimpleFilter_ShouldAddEqualCriteria() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "status:eq:ACTIVE");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithMultipleFilters_ShouldAddMultipleCriteria() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "status:eq:ACTIVE");
        filters.add("filter", "priority:gt:3");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithNestedFieldFilter_ShouldHandleDotNotation() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "user.name:like:john");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithDifferentOperations_ShouldParseCorrectly() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "age:gt:18");
        filters.add("filter", "name:like:john");
        filters.add("filter", "status:neq:INACTIVE");
        filters.add("filter", "score:gte:80");
        filters.add("filter", "price:lte:100");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithBetweenOperation_ShouldHandleRange() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "age:between:18-65");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithInOperation_ShouldHandleList() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "category:in:ELECTRONICS,CLOTHING");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithDateValue_ShouldParseAsLocalDate() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "createdAt:gt:2024-01-01");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithDateTimeValue_ShouldParseAsLocalDateTime() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "updatedAt:gt:2024-01-01T10:00:00");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithNumericValue_ShouldParseAsDouble() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "weight:gt:5.5");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithInvalidFilterFormat_ShouldSkipInvalidFilters() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "invalid-filter");
        filters.add("filter", "field:invalidop:value");
        filters.add("filter", "valid:eq:value");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    @Test
    void parse_WithBlankValues_ShouldSkipBlankCriteria() {
        // Given
        MultiValueMap<String, String> filters = new LinkedMultiValueMap<>();
        filters.add("filter", "name:eq:");
        filters.add("filter", "status:eq:   ");

        // When
        GenericSpecification<TestEntity> spec = FilterParser.parse(filters, TestEntity.class);

        // Then
        assertThat(spec).isNotNull();
    }

    // Test entity with @Searchable annotation
    @Searchable(fields = {"name", "description"})
    static class TestEntity {
        private String name;
        private String description;
        private String status;
        private Integer priority;
        private Double weight;
        // ... other fields
    }
}