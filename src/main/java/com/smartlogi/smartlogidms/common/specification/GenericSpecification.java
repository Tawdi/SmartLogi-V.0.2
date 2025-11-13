package com.smartlogi.smartlogidms.common.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {
    private final transient  List<SearchCriteria> criteriaList;

    public GenericSpecification() {
        this.criteriaList = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        if (criteria.value() != null && !criteria.value().toString().isBlank()) {
            criteriaList.add(criteria);
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicatesSearch = new ArrayList<>(); // (OR) list of SearchCriteria with c.op() == LIKE

        for (SearchCriteria c : criteriaList) {
            if (c.op().equals(SearchOperation.LIKE))
                predicatesSearch.add(buildPredicate(root, cb, c));
            else predicates.add(buildPredicate(root, cb, c));

        }

        // 1<>1
        if (predicates.isEmpty() && predicatesSearch.isEmpty()) {
            return cb.conjunction();
        }

        Predicate searchPredicate = predicatesSearch.isEmpty()
                ? cb.conjunction()
                : cb.or(predicatesSearch.toArray(new Predicate[0]));

        Predicate otherPredicate = predicates.isEmpty()
                ? cb.conjunction()
                : cb.and(predicates.toArray(new Predicate[0]));

        return cb.and(searchPredicate, otherPredicate);
    }

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder cb, SearchCriteria c) {
        return switch (c.op()) {
            case EQUAL -> cb.equal(getPath(root, c.key()), c.value());
            case NEQ -> cb.notEqual(getPath(root, c.key()), c.value());
            case LIKE -> {
                Path<String> path = getPath(root, c.key());
                yield cb.like(cb.lower(path), "%" + c.value().toString().toLowerCase() + "%");
            }
            case IN -> getPath(root, c.key()).in((List<?>) c.value());
            case GREATER_THAN -> cb.greaterThan(getPath(root, c.key()), (Comparable) c.value());
            case LESS_THAN -> cb.lessThan(getPath(root, c.key()), (Comparable) c.value());
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(getPath(root, c.key()), (Comparable) c.value());
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(getPath(root, c.key()), (Comparable) c.value());
            case BETWEEN -> {
                Path<Comparable> path = getPath(root, c.key());
                String[] values = c.value().toString().split("-");
                if (values.length == 2) {
                    Comparable low = (Comparable) parseValue(values[0]);
                    Comparable high = (Comparable) parseValue(values[1]);
                    yield cb.between(path, low, high);
                } else {
                    throw new IllegalArgumentException("Between requires value like '20-30'");
                }
            }
            case JOIN -> {
                Join<T, ?> join = root.join(c.joinTable(), JoinType.LEFT);
                yield cb.equal(join.get(c.key()), c.value());
            }

        };
    }

    @SuppressWarnings("unchecked")
    private <R> Path<R> getPath(Root<T> root, String key) {
        String[] parts = key.split("\\.");
        Path<R> path = (Path<R>) root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }

    private Object parseValue(String value) {
        // Try Double
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) { /* ignore */ }

        // Try LocalDateTime
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) { /* ignore */ }

        // Try LocalDate
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) { /* ignore */ }

        return value; // String
    }
}