package itmo.cityservice.service;

import itmo.cityservice.model.dto.CityDTO;
import itmo.cityservice.model.dto.CreateCityRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CityService {
    CityDTO getCityById(Long id);
    Page<CityDTO> getCities(Optional<List<String>> sort, Optional<Integer> page,
                            Optional<Integer> pageSize, Optional<Map<String, String>> filter);
    CityDTO createCity(Long id, CreateCityRequest createCityRequest);
    CityDTO updateCity(Long id, CreateCityRequest createCityRequest);
    void deleteCity(Long id);
    Double getAverageCarCode();
    String getCityWithMinName();
    void deleteCitiesBySeaLevel(Double metersAboveSeaLevel);
}