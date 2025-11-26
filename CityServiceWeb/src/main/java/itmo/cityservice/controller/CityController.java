package itmo.cityservice.controller;

import itmo.cityservice.ejb.model.dto.*;
import itmo.cityservice.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
@XmlRootElement
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<CitiesResponseDto> getCities(
            @RequestParam(required = false) List<String> sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String filter) {

        CitiesResponseDto response = cityService.getCities(sort, page, pageSize, filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        CityDto city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @PostMapping(produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<CityDto> createCity(@RequestBody CityCreateRequestDto cityDto) {
        CityDto createdCity = cityService.createCity(cityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<CityDto> updateCity(@PathVariable Long id, @RequestBody CityCreateRequestDto cityDto) {
        CityDto updatedCity = cityService.updateCity(id, cityDto);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/sea-level", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<DeleteResultDto> deleteCitiesBySeaLevel(
            @RequestParam Double metersAboveSeaLevel) {
        DeleteResultDto result = cityService.deleteCitiesBySeaLevel(metersAboveSeaLevel);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/car-code/avg", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<AverageResultDto> getAverageCarCode() {
        AverageResultDto result = cityService.getAverageCarCode();
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/name/min", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<CityDto> getCityWithMinName() {
        CityDto city = cityService.getCityWithMinName();
        return ResponseEntity.ok(city);
    }
}
