package itmo.cityservice.ejb.repository;

import itmo.cityservice.ejb.model.entity.City;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Stateless
public class CityRepositoryBean {

    @PersistenceContext
    private EntityManager entityManager;

    public List<City> findAll() {
        TypedQuery<City> query = entityManager.createQuery("SELECT c FROM City c", City.class);
        return query.getResultList();
    }

    public List<City> findAll(int page, int size) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c FROM City c", City.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
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

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) FROM City c", Long.class);
        return query.getSingleResult();
    }
}
