package itmo.cityservice.ejb.repository;

import itmo.cityservice.ejb.model.entity.City;
import itmo.cityservice.ejb.service.CityFilterBuilder;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class CityRepositoryBean {

    @PersistenceContext
    private EntityManager entityManager;

    public List<City> findAll(List<String> sortFields, String filter, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<City> query = cb.createQuery(City.class);
        Root<City> root = query.from(City.class);

        Predicate predicate = CityFilterBuilder.build(root, query, cb, filter);
        query.where(predicate);

        if (sortFields != null && !sortFields.isEmpty()) {
            CityFilterBuilder.validateSortFields(sortFields); // Валидация сортировки
            List<Order> orders = createSortOrders(root, cb, sortFields);
            query.orderBy(orders);
        }

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    private List<Order> createSortOrders(Root<City> root, CriteriaBuilder cb, List<String> sortFields) {
        List<Order> orders = new ArrayList<>();

        for (String sortField : sortFields) {
            boolean isDescending = sortField.startsWith("-");
            String fieldName = isDescending ? sortField.substring(1) : sortField;

            Expression<?> fieldExpr = getFieldExpression(root, fieldName);
            if (isDescending) {
                orders.add(cb.desc(fieldExpr));
            } else {
                orders.add(cb.asc(fieldExpr));
            }
        }

        return orders;
    }

    private Expression<?> getFieldExpression(Root<City> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.", 2);
            if (parts.length == 2) {
                Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                return join.get(parts[1]);
            }
        }
        return root.get(field);
    }

    public City findById(Long id) {
        return entityManager.find(City.class, id);
    }

    public City save(City city) {
        if (city.getId() == null) {
            entityManager.persist(city);
            return city;
        } else {
            return entityManager.merge(city);
        }
    }

    public void delete(City city) {
        entityManager.remove(entityManager.contains(city) ? city : entityManager.merge(city));
    }

    public void deleteById(Long id) {
        City city = findById(id);
        if (city != null) {
            delete(city);
        }
    }

    public List<City> findByMetersAboveSeaLevel(Double metersAboveSeaLevel) {
        TypedQuery<City> query = entityManager.createQuery(
                "SELECT c FROM City c WHERE c.metersAboveSeaLevel = :level", City.class);
        query.setParameter("level", metersAboveSeaLevel);
        return query.getResultList();
    }

    public Double findAverageCarCode() {
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT AVG(c.carCode) FROM City c", Double.class);
        return query.getSingleResult();
    }

    public Optional<City> findFirstByOrderByNameAsc() {
        TypedQuery<City> query = entityManager.createQuery(
                "SELECT c FROM City c ORDER BY c.name ASC", City.class);
        query.setMaxResults(1);
        List<City> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public long count(String filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<City> root = query.from(City.class);

        Predicate predicate = CityFilterBuilder.build(root, query, cb, filter);
        query.select(cb.count(root)).where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }
}
