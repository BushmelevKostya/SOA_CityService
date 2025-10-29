package itmo.cityservice.repository;

import itmo.cityservice.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {
    List<City> findByMetersAboveSeaLevel(Double metersAboveSeaLevel);

    @Query("SELECT AVG(c.carCode) FROM City c")
    Double findAverageCarCode();

    Optional<City> findFirstByOrderByNameAsc();
}
