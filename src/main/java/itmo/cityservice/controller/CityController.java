package itmo.cityservice.controller;

import itmo.cityservice.exception.BadRequestException;
import itmo.cityservice.exception.NotFoundException;
import itmo.cityservice.exception.ValidationException;
import itmo.cityservice.model.dto.*;
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
    public ResponseEntity<?> getCityById(@PathVariable Long id) {
        try {
            CityDto city = cityService.getCityById(id);
            return ResponseEntity.ok(city);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createCity(@RequestBody CityCreateRequestDto cityDto) {
        try {
            CityDto createdCity = cityService.createCity(cityDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
        } catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> updateCity(@PathVariable Long id, @RequestBody CityCreateRequestDto cityDto) {
        try {
            CityDto updatedCity = cityService.updateCity(id, cityDto);
            return ResponseEntity.ok(updatedCity);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        try {
            cityService.deleteCity(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/sea-level", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<DeleteResultDto> deleteCitiesBySeaLevel(
            @RequestParam Double metersAboveSeaLevel) {
        DeleteResultDto result = cityService.deleteCitiesBySeaLevel(metersAboveSeaLevel);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/car-code/avg", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getAverageCarCode() {
        try {
            AverageResultDto result = cityService.getAverageCarCode();
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/name/min", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getCityWithMinName() {
        try {
            CityDto city = cityService.getCityWithMinName();
            return ResponseEntity.ok(city);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
