package itmo.cityservice.ejb.service;

import itmo.cityservice.ejb.exception.BadRequestException;
import itmo.cityservice.ejb.model.entity.City;
import itmo.cityservice.ejb.model.entity.Climate;
import itmo.cityservice.ejb.model.entity.StandardOfLiving;
import jakarta.persistence.criteria.*;
import java.time.*;
import java.util.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CityFilterBuilder {

    private static final Set<String> VALID_FILTER_FIELDS = new HashSet<>(Arrays.asList(
            "id", "name", "coordinates.x", "coordinates.y", "creationDate",
            "area", "population", "metersAboveSeaLevel", "carCode",
            "climate", "standardOfLiving", "governor.name",
            "governor.age", "governor.height", "governor.birthday"
    ));

    // Список допустимых полей для сортировки (включая с префиксом "-")
    private static final Set<String> VALID_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "id", "name", "coordinates.x", "coordinates.y", "creationDate",
            "area", "population", "metersAboveSeaLevel", "carCode",
            "climate", "standardOfLiving", "governor.name",
            "governor.age", "governor.height", "governor.birthday",
            "-id", "-name", "-coordinates.x", "-coordinates.y", "-creationDate",
            "-area", "-population", "-metersAboveSeaLevel", "-carCode",
            "-climate", "-standardOfLiving", "-governor.name",
            "-governor.age", "-governor.height", "-governor.birthday"
    ));

    public static class FilterCriteria {
        private final String field;
        private final String operator;
        private final String value;

        public FilterCriteria(String field, String operator, String value) {
            this.field = field;
            this.operator = operator;
            this.value = value;
        }

        public String getField() { return field; }
        public String getOperator() { return operator; }
        public String getValue() { return value; }
    }

    // Валидация полей сортировки
    public static void validateSortFields(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return;
        }

        for (String sortField : sortFields) {
            if (!VALID_SORT_FIELDS.contains(sortField)) {
                throw new BadRequestException(
                        "Недопустимое поле для сортировки: " + sortField +
                                ". Допустимые поля: " + String.join(", ", VALID_SORT_FIELDS)
                );
            }
        }
    }

    public static Predicate build(Root<City> root, CriteriaQuery<?> query, CriteriaBuilder cb, String filter) {
        if (filter == null || filter.isEmpty()) {
            return cb.conjunction();
        }

        try {
            List<FilterCriteria> filterParams = parseFilterParams(filter);
            List<Predicate> predicates = new ArrayList<>();

            for (FilterCriteria param : filterParams) {
                validateFilterFieldName(param.getField());
                Predicate predicate = createPredicate(root, cb, param.getField(), param.getOperator(), param.getValue());
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid filter format: " + e.getMessage());
        }
    }

    private static List<FilterCriteria> parseFilterParams(String filter) {
        List<FilterCriteria> params = new ArrayList<>();

        if (filter == null || filter.isEmpty()) {
            return params;
        }

        String decodedFilter = URLDecoder.decode(filter, StandardCharsets.UTF_8);

        String[] pairs = decodedFilter.split("&");

        for (String pair : pairs) {
            try {
                pair = pair.trim();
                if (pair.isEmpty()) continue;

                if (pair.contains(".") && pair.contains(":")) {
                    int dotPos = pair.indexOf('.');
                    int colonPos = pair.indexOf(':');

                    if (dotPos > 0 && colonPos > dotPos) {
                        String field = pair.substring(0, dotPos);
                        String operator = pair.substring(dotPos + 1, colonPos);
                        String value = pair.substring(colonPos + 1);

                        params.add(new FilterCriteria(field, operator, value));
                        continue;
                    }
                }

                if (pair.contains("[") && pair.contains("]") && pair.contains("=")) {
                    int openBracketPos = pair.indexOf('[');
                    int closeBracketPos = pair.indexOf(']');
                    int equalsPos = pair.indexOf('=');

                    if (openBracketPos > 0 && closeBracketPos > openBracketPos && equalsPos > closeBracketPos) {
                        String field = pair.substring(0, openBracketPos);
                        String operator = pair.substring(openBracketPos + 1, closeBracketPos);
                        String value = pair.substring(equalsPos + 1);

                        params.add(new FilterCriteria(field, operator, value));
                        continue;
                    }
                }

                if (pair.contains("=") && !pair.contains("[") && !pair.contains(".")) {
                    int equalsPos = pair.indexOf('=');
                    if (equalsPos > 0) {
                        String field = pair.substring(0, equalsPos);
                        String value = pair.substring(equalsPos + 1);
                        params.add(new FilterCriteria(field, "eq", value));
                    }
                }

            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid filter format: " + pair + ". Error: " + e.getMessage());
            }
        }
        return params;
    }


    private static void validateFilterFieldName(String field) {
        if (!VALID_FILTER_FIELDS.contains(field)) {
            throw new IllegalArgumentException(
                    "Invalid filter field: " + field +
                            ". Valid fields: " + String.join(", ", VALID_FILTER_FIELDS)
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
        if (value == null) {
            throw new IllegalArgumentException("Filter value cannot be null");
        }

        Class<?> fieldType = getFieldType(field);
        Expression<?> fieldExpr = getFieldExpression(root, field);

        switch (operator.toLowerCase()) {
            case "eq":
                return cb.equal(fieldExpr, convertValue(fieldType, value));
            case "ne":
                return cb.notEqual(fieldExpr, convertValue(fieldType, value));
            case "gt":
                return cb.greaterThan((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "lt":
                return cb.lessThan((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "gte":
                return cb.greaterThanOrEqualTo((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "lte":
                return cb.lessThanOrEqualTo((Expression<Comparable>) fieldExpr, (Comparable) convertValue(fieldType, value));
            case "like":
                if (String.class.isAssignableFrom(fieldExpr.getJavaType())) {
                    return cb.like(cb.lower((Expression<String>) fieldExpr), "%" + value.toLowerCase() + "%");
                } else {
                    throw new IllegalArgumentException("LIKE operator can only be used with string fields");
                }
            case "in":
                return fieldExpr.in(parseArrayValue(fieldType, value));
            case "notin":
                return cb.not(fieldExpr.in(parseArrayValue(fieldType, value)));
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static Class<?> getFieldType(String field) {
        if (field.startsWith("coordinates.")) {
            return Double.class;
        } else if (field.startsWith("governor.")) {
            String govField = field.substring("governor.".length());
            switch (govField) {
                case "age": return Long.class;
                case "height": return Double.class;
                case "birthday": return LocalDate.class;
                default: return String.class;
            }
        }

        switch (field) {
            case "id":
            case "carCode":
                return Long.class;
            case "area":
            case "population":
                return Integer.class;
            case "metersAboveSeaLevel":
            case "coordinates.x":
            case "coordinates.y":
            case "governor.height":
                return Double.class;
            case "creationDate":
                return ZonedDateTime.class;
            case "climate":
                return Climate.class;
            case "standardOfLiving":
                return StandardOfLiving.class;
            case "governor.birthday":
                return LocalDate.class;
            default:
                return String.class;
        }
    }

    private static Expression<?> getFieldExpression(Root<City> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.", 2);
            if (parts.length == 2) {
                Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                return join.get(parts[1]);
            }
        }
        return root.get(field);
    }

    private static Object convertValue(Class<?> targetType, String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Filter value cannot be empty");
        }

        try {
            if (targetType == Long.class) {
                return Long.parseLong(value);
            } else if (targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(value);
            } else if (targetType == ZonedDateTime.class) {
                return ZonedDateTime.parse(value);
            } else if (targetType == Climate.class) {
                return Climate.valueOf(value.toUpperCase());
            } else if (targetType == StandardOfLiving.class) {
                return StandardOfLiving.valueOf(value.toUpperCase());
            }
            return value;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid value '" + value + "' for type " + targetType.getSimpleName() + ": " + e.getMessage()
            );
        }
    }

    private static Object[] parseArrayValue(Class<?> elementType, String values) {
        if (values == null || values.trim().isEmpty()) {
            return new Object[0];
        }

        return Arrays.stream(values.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(v -> convertValue(elementType, v))
                .toArray();
    }
}
