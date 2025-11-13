package com.smartlogi.smartlogidms.common.specification;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FilterParser {

    private FilterParser() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> GenericSpecification<T> parse(MultiValueMap<String, String> filters, Class<T> entityClass) {
        GenericSpecification<T> spec = new GenericSpecification<>();

        if (filters == null || filters.isEmpty()) {
            return spec;
        }

        // === 1. GLOBAL SEARCH: ?search=John ===
        String search = filters.getFirst("search");
        if (search != null && !search.isBlank()) {
            Searchable searchable = entityClass.getAnnotation(Searchable.class);
            if (searchable != null) {
                for (String field : searchable.fields()) {
                    spec.add(new SearchCriteria(field, search, SearchOperation.LIKE));
                }
            } else {
                // Fallback
                spec.add(new SearchCriteria("description", search, SearchOperation.LIKE));
                spec.add(new SearchCriteria("reference", search, SearchOperation.LIKE));
            }
        }

        // === 2. FILTERS: ?filter=statut:neq:CREATED&filter=poids:gt:3 ===
        List<String> filterValues = filters.get("filter"); // MultiValueMap.get() -> List<String>
        if (filterValues != null) {
            for (String filterStr : filterValues) {
                if (filterStr == null || filterStr.isBlank()) continue;

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
        }

        return spec;
    }

    private static SearchOperation parseOp(String opStr) {
        return switch (opStr.toLowerCase()) {
            case "eq" -> SearchOperation.EQUAL;
            case "neq" -> SearchOperation.NEQ;
            case "like" -> SearchOperation.LIKE;
            case "in" -> SearchOperation.IN;
            case "gt" -> SearchOperation.GREATER_THAN;
            case "lt" -> SearchOperation.LESS_THAN;
            case "gte" -> SearchOperation.GREATER_THAN_OR_EQUAL;
            case "lte" -> SearchOperation.LESS_THAN_OR_EQUAL;
            case "between" -> SearchOperation.BETWEEN;
            default -> SearchOperation.EQUAL;
        };
    }

    private static Object parseValue(String value) {
        if (value == null || value.isBlank()) return value;
        try { return Double.valueOf(value); } catch (NumberFormatException e) { /* ignore */ }
        try { return LocalDateTime.parse(value); } catch (DateTimeParseException e) { /* ignore */ }
        try { return LocalDate.parse(value); } catch (DateTimeParseException e) { /* ignore */ }

        return value; // String
    }
}