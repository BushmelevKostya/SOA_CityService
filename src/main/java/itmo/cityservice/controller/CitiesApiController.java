package itmo.cityservice.controller;

import itmo.cityservice.api.CitiesApi;
import itmo.cityservice.model.dto.CreateCityRequest;
import itmo.cityservice.model.dto.GetCities200Response;
import itmo.cityservice.model.dto.CityDTO;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.service.CityService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "${openapi.cityCollection.base-path:/api/v1}",
        produces = "application/xml",
        consumes = "application/xml"
)
@RequiredArgsConstructor
public class CitiesApiController implements CitiesApi {
    private final NativeWebRequest request;
    private final CityService cityService;
    
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    @Override
    public ResponseEntity<CityDTO> citiesIdGet(Long id) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(cityService.getCityById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Override
    public ResponseEntity<Void> citiesIdDelete(Long id) {
        try {
            cityService.deleteCity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Override
    public ResponseEntity<CityDTO> citiesIdPost(Long id, CreateCityRequest createCityRequest) {
        try {
            CityDTO createdCity = cityService.createCity(id, createCityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Override
    public ResponseEntity<CityDTO> citiesIdPut(Long id, CreateCityRequest createCityRequest) {
        try {
            CityDTO updatedCity = cityService.updateCity(id, createCityRequest);
            return ResponseEntity.ok(updatedCity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Override
    public ResponseEntity<GetCities200Response> getCities(Optional<List<String>> sort,
                                                          Optional<Integer> page,
                                                          Optional<Integer> pageSize,
                                                          Optional<Map<String, String>> filter) {
        try {
            Page<CityDTO> citiesPage = cityService.getCities(sort, page, pageSize, filter);
            
            GetCities200Response response = new GetCities200Response();
            response.setData(citiesPage.getContent());
            response.setTotalPages(citiesPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/cities/car-code/avg")
    public ResponseEntity<Double> getAverageCarCode() {
        try {
            Double average = cityService.getAverageCarCode();
            return ResponseEntity.ok(average);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cities/name/min")
    public ResponseEntity<String> getCityWithMinName() {
        try {
            String cityName = cityService.getCityWithMinName();
            return ResponseEntity.ok(cityName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/cities/sea-level")
    public ResponseEntity<Void> deleteCitiesBySeaLevel(@RequestParam Double metersAboveSeaLevel) {
        try {
            cityService.deleteCitiesBySeaLevel(metersAboveSeaLevel);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}