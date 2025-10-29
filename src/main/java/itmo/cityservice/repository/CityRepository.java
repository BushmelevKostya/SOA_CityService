package itmo.cityservice.repository;

import itmo.cityservice.model.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
	Page<City> findAll(Specification<City> spec, Pageable pageable);
	
	@Query("SELECT AVG(c.carCode) FROM City c")
	Double getAverageCarCode();
	
	@Query("SELECT c.name FROM City c ORDER BY c.name ASC LIMIT 1")
	String findCityWithMinName();
	
	void deleteByMetersAboveSeaLevel(Double metersAboveSeaLevel);
}
