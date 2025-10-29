package itmo.cityservice.service;

import itmo.cityservice.exception.BadRequestException;
import itmo.cityservice.exception.NotFoundException;
import itmo.cityservice.exception.ValidationException;
import itmo.cityservice.mapper.CityMapper;
import itmo.cityservice.model.dto.*;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.model.entity.Climate;
import itmo.cityservice.repository.CityRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    public CitiesResponseDto getCities(List<String> sort, int page, int pageSize, String filter) {
        if (page < 1) throw new BadRequestException("Номер страницы должен быть больше 0");
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Размер страницы должен быть от 1 до 100");
        }

        Specification<City> spec = CityFilterSpecificationBuilder.build(filter);

        Sort sorting = createSort(sort);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sorting);
        Page<City> cityPage = cityRepository.findAll(spec, pageRequest);

        CitiesResponseDto response = new CitiesResponseDto();
        response.setData(cityMapper.toDtoList(cityPage.getContent()));
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalPages(cityPage.getTotalPages());

        return response;
    }

    public CityDto getCityById(Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException("Неверный ID города");
        }

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Город с указанным ID не найден"));

        return cityMapper.toDto(city);
    }

    @Transactional
    public CityDto createCity(CityCreateRequestDto dto) {
        validateCityCreateRequest(dto);

        City city = cityMapper.toEntity(dto);
        City savedCity = cityRepository.save(city);

        return cityMapper.toDto(savedCity);
    }

    @Transactional
    public CityDto updateCity(Long id, CityCreateRequestDto dto) {
        if (id == null || id < 1) {
            throw new BadRequestException("Неверный ID города");
        }

        validateCityCreateRequest(dto);

        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Город с указанным ID не найден"));

        cityMapper.updateEntityFromDto(existingCity, dto);
        City updatedCity = cityRepository.save(existingCity);

        return cityMapper.toDto(updatedCity);
    }

    @Transactional
    public void deleteCity(Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException("Неверный ID города");
        }

        if (!cityRepository.existsById(id)) {
            throw new NotFoundException("Город с указанным ID не найден");
        }

        cityRepository.deleteById(id);
    }

    @Transactional
    public DeleteResultDto deleteCitiesBySeaLevel(Double metersAboveSeaLevel) {
        if (metersAboveSeaLevel == null) {
            throw new BadRequestException("Не указана высота над уровнем моря");
        }

        List<City> citiesToDelete = cityRepository.findByMetersAboveSeaLevel(metersAboveSeaLevel);
        cityRepository.deleteAll(citiesToDelete);

        DeleteResultDto result = new DeleteResultDto();
        result.setDeletedCount(citiesToDelete.size());
        return result;
    }

    public AverageResultDto getAverageCarCode() {
        Double average = cityRepository.findAverageCarCode();

        if (average == null) {
            throw new NotFoundException("Коллекция пуста");
        }

        AverageResultDto result = new AverageResultDto();
        result.setAverage(average);
        return result;
    }

    public CityDto getCityWithMinName() {
        City city = cityRepository.findFirstByOrderByNameAsc()
                .orElseThrow(() -> new NotFoundException("Коллекция пуста"));

        return cityMapper.toDto(city);
    }

    private void validateCityCreateRequest(CityCreateRequestDto dto) {
        if (dto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new ValidationException("Название города обязательно");
        }

        if (dto.getCoordinates() == null) {
            throw new ValidationException("Координаты обязательны");
        } else {
            if (dto.getCoordinates().getX() == null || dto.getCoordinates().getX() <= -190) {
                throw new ValidationException("Координата X должна быть больше -190");
            }
            if (dto.getCoordinates().getY() == null) {
                throw new ValidationException("Координата Y обязательна");
            }
        }

        if (dto.getArea() == null || dto.getArea() <= 0) {
            throw new ValidationException("Площадь города должна быть больше 0");
        }
        if (dto.getPopulation() == null || dto.getPopulation() <= 0) {
            throw new ValidationException("Население города должно быть больше 0");
        }
        if (dto.getCarCode() == null || dto.getCarCode() < 1 || dto.getCarCode() > 1000) {
            throw new ValidationException("Код автомобиля должен быть в диапазоне от 1 до 1000");
        }

        if (dto.getClimate() == null) {
            throw new ValidationException("Климат обязателен");
        } else {
            try {
                Climate.valueOf(dto.getClimate().name());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Недопустимое значение климата");
            }
        }

        if (dto.getGovernor() == null) {
            throw new ValidationException("Губернатор обязателен");
        } else {
            if (dto.getGovernor().getName() == null || dto.getGovernor().getName().isEmpty()) {
                throw new ValidationException("Имя губернатора обязательно");
            }
            if (dto.getGovernor().getAge() != null && dto.getGovernor().getAge() <= 0) {
                throw new ValidationException("Возраст губернатора должен быть больше 0");
            }
            if (dto.getGovernor().getHeight() != null && dto.getGovernor().getHeight() <= 0) {
                throw new ValidationException("Рост губернатора должен быть больше 0");
            }
        }
    }

    private Sort createSort(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        List<String> validFields = Arrays.asList(
                "id", "name", "coordinates.x", "coordinates.y", "creationDate",
                "area", "population", "metersAboveSeaLevel", "carCode", "climate",
                "standardOfLiving", "governor.name", "governor.age", "governor.height", "governor.birthday"
        );

        for (String sortField : sortFields) {
            boolean isDescending = sortField.startsWith("-");
            String fieldName = isDescending ? sortField.substring(1) : sortField;

            if (!validFields.contains(fieldName)) {
                throw new BadRequestException("Недопустимое поле для сортировки: " + fieldName);
            }

            orders.add(isDescending ?
                    Sort.Order.desc(fieldName) :
                    Sort.Order.asc(fieldName));
        }

        return Sort.by(orders);
    }
}
