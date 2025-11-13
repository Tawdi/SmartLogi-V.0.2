package com.smartlogi.smartlogidms.common.specification;

/**
 *
 * @param key       nom du champ (ex: "statut", "zone.nom")
 * @param value     (ex : "DELEVERED" , "safi")
 * @param op        Sql Operation (IN , LIKE ,JOIN , GREATER_THAN ...)
 * @param joinTable table name if needed
 */
public record SearchCriteria(
        String key,
        Object value,
        SearchOperation op,
        String joinTable) {


    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this(key, value, operation, null);
    }


}
