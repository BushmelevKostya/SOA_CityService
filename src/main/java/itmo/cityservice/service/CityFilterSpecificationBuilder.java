package itmo.cityservice.service;

import itmo.cityservice.exception.BadRequestException;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.model.entity.Climate;
import itmo.cityservice.model.entity.StandardOfLiving;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
import lombok.Data;

public class CityFilterSpecificationBuilder {

    public static Specification<City> build(String filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null || filter.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            try {
                Map<String, String> filterParams = parseFilterParams(filter);
                List<Predicate> predicates = new ArrayList<>();

                for (Map.Entry<String, String> entry : filterParams.entrySet()) {
                    FilterCondition condition = parseFieldAndOperator(entry.getKey());
                    String field = condition.getField();
                    String operator = condition.getOperator();
                    String value = entry.getValue();

                    validateFilterFieldName(field);
                    Predicate predicate = createPredicate(root, criteriaBuilder, field, operator, value);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }

                return predicates.isEmpty() 
                    ? criteriaBuilder.conjunction() 
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid filter format: " + e.getMessage());
            }
        };
    }

    private static Map<String, String> parseFilterParams(String filter) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = filter.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0].trim(), keyValue[1].trim());
            } else {
                throw new IllegalArgumentException(
                    "Invalid filter condition format: " + pair + 
                    ". Expected field[operator]=value"
                );
            }
        }
        return params;
    }

    private static FilterCondition parseFieldAndOperator(String fieldWithOperator) {
        Matcher matcher = Pattern.compile("(.*?)\\[(.*?)]").matcher(fieldWithOperator);
        if (matcher.find()) {
            return new FilterCondition(matcher.group(1), matcher.group(2));
        }
        throw new IllegalArgumentException(
            "Invalid field with operator format: " + fieldWithOperator + 
            ". Expected field[operator]"
        );
    }

    private static void validateFilterFieldName(String field) {
        List<String> validFields = Arrays.asList(
            "name", "coordinates.x", "coordinates.y", "area", 
            "population", "metersAboveSeaLevel", "carCode", 
            "climate", "standardOfLiving", "governor.name",
            "governor.age", "governor.height", "governor.birthday"
        );
        
        if (!validFields.contains(field)) {
            throw new IllegalArgumentException(
                "Invalid filter field: " + field + 
                ". Valid fields: " + String.join(", ", validFields)
            );
        }
    }

    private static Predicate createPredicate(
        Root<City> root, 
        CriteriaBuilder cb, 
        String field, 
        String operator, 
        String value
    ) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Filter value cannot be empty");
        }

        Class<?> fieldType = getFieldType(field);
        Expression<?> fieldExpr = getFieldExpression(root, field);

        switch (operator.toLowerCase()) {
            case "eq": return cb.equal(fieldExpr, convertValue(fieldType, value));
            case "ne": return cb.notEqual(fieldExpr, convertValue(fieldType, value));
            case "gt": return cb.greaterThan((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "lt": return cb.lessThan((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "gte": return cb.greaterThanOrEqualTo((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "lte": return cb.lessThanOrEqualTo((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "like": return cb.like((Expression<String>) fieldExpr, "%" + value + "%");
            case "in": return fieldExpr.in(parseArrayValue(fieldType, value));
            case "notin": return cb.not(fieldExpr.in(parseArrayValue(fieldType, value)));
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static Class<?> getFieldType(String field) {
        if (field.startsWith("coordinates.")) {
            return Double.class;
        } else if (field.startsWith("governor.")) {
            String govField = field.substring("governor.".length());
            switch (govField) {
                case "age": return Integer.class;
                case "height": return Double.class;
                case "birthday": return LocalDate.class;
                default: return String.class;
            }
        }
        
        switch (field) {
            case "area":
            case "population":
            case "carCode":
                return Integer.class;
            case "metersAboveSeaLevel":
                return Double.class;
            case "creationDate":
                return LocalDateTime.class;
            case "climate":
                return Climate.class;
            case "standardOfLiving":
                return StandardOfLiving.class;
            default:
                return String.class;
        }
    }

    private static Expression<?> getFieldExpression(Root<City> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.");
            From<?, ?> from = root;
            for (int i = 0; i < parts.length - 1; i++) {
                from = root.join(parts[i], JoinType.LEFT);
            }
            return from.get(parts[parts.length - 1]);
        }
        return root.get(field);
    }

    private static Object convertValue(Class<?> targetType, String value) {
        try {
            if (targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(value);
            } else if (targetType == LocalDateTime.class) {
                return LocalDateTime.parse(value);
            } else if (targetType == Climate.class) {
                return Climate.valueOf(value.toUpperCase());
            } else if (targetType == StandardOfLiving.class) {
                return StandardOfLiving.valueOf(value.toUpperCase());
            }
            return value;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for type " + targetType.getSimpleName()
            );
        }
    }

    private static Object[] parseArrayValue(Class<?> elementType, String values) {
        return Arrays.stream(values.split(","))
            .map(String::trim)
            .map(v -> convertValue(elementType, v))
            .toArray();
    }

    @Data
    private static class FilterCondition {
        private String field;
        private String operator;

        public FilterCondition(String group, String group1) {
        }


        public String getField() {
            return this.field;
        }

        public String getOperator() {
            return this.operator;
        }
    }
}
