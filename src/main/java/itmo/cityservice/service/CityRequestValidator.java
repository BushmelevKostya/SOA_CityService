package itmo.cityservice.service;

import itmo.cityservice.exception.BadRequestException;
import itmo.cityservice.exception.ValidationException;
import itmo.cityservice.model.dto.CityCreateRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class CityRequestValidator {

    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "name", "coordinates.x", "coordinates.y", "creationDate",
        "area", "population", "metersAboveSeaLevel", "carCode", "climate",
        "standardOfLiving", "governor.name", "governor.age", "governor.height", "governor.birthday"
    );

    public void validatePaginationParams(int page, int pageSize) {
        if (page < 1) {
            throw new BadRequestException("Номер страницы должен быть больше 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Размер страницы должен быть от 1 до 100");
        }
    }

    public void validateCityId(Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException("Неверный ID города");
        }
    }

    public void validateSeaLevelParam(Double metersAboveSeaLevel) {
        if (metersAboveSeaLevel == null) {
            throw new BadRequestException("Не указана высота над уровнем моря");
        }
    }

    public void validateSortFields(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return;
        }

        for (String sortField : sortFields) {
            String fieldName = sortField.startsWith("-") ? sortField.substring(1) : sortField;
            if (!VALID_SORT_FIELDS.contains(fieldName)) {
                throw new BadRequestException("Недопустимое поле для сортировки: " + fieldName);
            }
        }
    }

    public void validateFilterParam(String filter) {
        if (filter != null && filter.length() > 1000) {
            throw new BadRequestException("Фильтр слишком длинный");
        }
    }


    public void validateCityCreateRequest(CityCreateRequestDto dto) {
        if (dto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Название города обязательно");
        }

        if (dto.getCoordinates() == null) {
            throw new ValidationException("Координаты обязательны");
        } else {
            if (dto.getCoordinates().getX() == null) {
                throw new ValidationException("Координата X обязательна");
            }

            BigDecimal minValue = new BigDecimal("-190");
            if (dto.getCoordinates().getX().compareTo(minValue) <= 0) {
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
        }

        if (dto.getGovernor() == null) {
            throw new ValidationException("Губернатор обязателен");
        } else {
            if (dto.getGovernor().getName() == null || dto.getGovernor().getName().trim().isEmpty()) {
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
}
