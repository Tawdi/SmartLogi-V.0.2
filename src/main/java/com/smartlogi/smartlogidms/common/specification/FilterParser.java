package com.smartlogi.smartlogidms.common.specification;

import com.smartlogi.smartlogidms.common.annotation.Searchable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterParser {

    private static final Set<String> PAGEABLE_PARAMS = Set.of("page", "size", "sort");

    // FilterParser.java
    public static <T> GenericSpecification<T> parse(Map<String, String> filters, Class<T> entityClass) {
        GenericSpecification<T> spec = new GenericSpecification<>();

        if (filters == null || filters.isEmpty()) {
            return spec;
        }

        // === 1. GLOBAL SEARCH: ?search=John ===
        String search = filters.get("search");
        if (search != null && !search.isBlank()) {
            Searchable searchable = entityClass.getAnnotation(Searchable.class);
            if (searchable != null) {
                for (String field : searchable.fields()) {
                    spec.add(new SearchCriteria(field, search, SearchOperation.LIKE));
                }
            } else {
                // Fallback si pas d'annotation
                spec.add(new SearchCriteria("description", search, SearchOperation.LIKE));
                spec.add(new SearchCriteria("reference", search, SearchOperation.LIKE));
            }
        }

        // === 2. FILTERS: ?filter=score:lte:50 ===
        List<String> filterStrings = new ArrayList<>();
        filters.forEach((key, value) -> {
            if (PAGEABLE_PARAMS.contains(key)) return;
            if (key.startsWith("filter")) {
                filterStrings.add(value);
            }
        });

        for (String filterStr : filterStrings) {
            if (filterStr.isBlank()) continue;

            String[] parts = filterStr.split(":", 3);
            if (parts.length < 2) continue;

            String field = parts[0];
            String opStr = parts[1];
            String value = parts.length == 3 ? parts[2] : "";

            SearchOperation op = parseOp(opStr);
            Object parsedValue = parseValue(value.isEmpty() ? opStr : value);

            if (field.contains(".")) {
                String[] fieldParts = field.split("\\.", 2);
                spec.add(new SearchCriteria(fieldParts[1], parsedValue, op, fieldParts[0]));
            } else {
                spec.add(new SearchCriteria(field, parsedValue, op));
            }
        }

        return spec;
    }

    private static SearchOperation parseOp(String opStr) {
        return switch (opStr.toLowerCase()) {
            case "eq" -> SearchOperation.EQUAL;
            case "like" -> SearchOperation.LIKE;
            case "in" -> SearchOperation.IN;
            case "neq" -> SearchOperation.NEQ;
            case "gt" -> SearchOperation.GREATER_THAN;
            case "lt" -> SearchOperation.LESS_THAN;
            case "gte" -> SearchOperation.GREATER_THAN_OR_EQUAL;
            case "lte" -> SearchOperation.LESS_THAN_OR_EQUAL;
            case "between" -> SearchOperation.BETWEEN;
            default -> SearchOperation.EQUAL;
        };
    }

    private static Object parseValue(String value) {
        try { return Double.valueOf(value); } catch (NumberFormatException e) { }
        try { return LocalDateTime.parse(value); } catch (DateTimeParseException e) { }
        try { return LocalDate.parse(value); } catch (DateTimeParseException e) { }
        return value; // String
    }
}