package com.smartlogi.smartlogidms.common.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {
    private final List<SearchCriteria> criteriaList;

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

        for (SearchCriteria c : criteriaList) {
            predicates.add(buildPredicate(root, cb, c));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder cb, SearchCriteria c) {
        return switch (c.op()) {
            case EQUAL -> cb.equal(getPath(root, c.key()), c.value());
            case LIKE -> {
                Path<String> path = getPath(root, c.key());
                yield cb.like(cb.lower(path), "%" + c.value().toString().toLowerCase() + "%");
            }
            case IN -> getPath(root, c.key()).in((List<?>) c.value());
            case GREATER_THAN -> cb.greaterThan(getPath(root, c.key()), (Comparable) c.value());
            case LESS_THAN -> cb.lessThan(getPath(root, c.key()), (Comparable) c.value());
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(getPath(root, c.key()), (Comparable) c.value());
            case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(getPath(root, c.key()), (Comparable) c.value());
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
}