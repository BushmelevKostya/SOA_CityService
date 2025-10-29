package itmo.cityservice.mapper;

import itmo.cityservice.model.dto.CityDTO;
import itmo.cityservice.model.dto.CoordinatesDTO;
import itmo.cityservice.model.dto.CreateCityRequest;
import itmo.cityservice.model.dto.HumanDTO;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.model.entity.Coordinates;
import itmo.cityservice.model.entity.Human;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class CityMapper {

    public CityDTO toDto(City entity) {
        return new CityDTO(
                entity.getId(),
                entity.getName(),
                toCoordinatesDto(entity.getCoordinates()),
                entity.getCreationDate(),
                entity.getArea(),
                entity.getPopulation(),
                entity.getMetersAboveSeaLevel(),
                entity.getCarCode(),
                entity.getClimate(),
                entity.getStandardOfLiving(),
                toHumanDto(entity.getGovernor())
        );
    }

    public City toEntity(CreateCityRequest dto) {
        City city = new City();
        city.setName(dto.getName());
        city.setCoordinates(toCoordinatesEntity(dto.getCoordinates()));
        city.setCreationDate(OffsetDateTime.now());
        city.setArea(dto.getArea());
        city.setPopulation(dto.getPopulation());
        city.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        city.setCarCode(dto.getCarCode());
        city.setClimate(dto.getClimate());
        city.setStandardOfLiving(dto.getStandardOfLiving());
        city.setGovernor(toHumanEntity(dto.getGovernor()));
        return city;
    }

    public void updateEntityFromDto(CreateCityRequest dto, City entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getCoordinates() != null) {
            entity.setCoordinates(toCoordinatesEntity(dto.getCoordinates()));
        }
        if (dto.getArea() != null) {
            entity.setArea(dto.getArea());
        }
        if (dto.getPopulation() != null) {
            entity.setPopulation(dto.getPopulation());
        }
        if (dto.getMetersAboveSeaLevel() != null) {
            entity.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        }
        if (dto.getCarCode() != null) {
            entity.setCarCode(dto.getCarCode());
        }
        if (dto.getClimate() != null) {
            entity.setClimate(dto.getClimate());
        }
        if (dto.getStandardOfLiving() != null) {
            entity.setStandardOfLiving(dto.getStandardOfLiving());
        }
        if (dto.getGovernor() != null) {
            entity.setGovernor(toHumanEntity(dto.getGovernor()));
        }
    }

    public CoordinatesDTO toCoordinatesDto(Coordinates entity) {
        if (entity == null) {
            return null;
        }
	    return new CoordinatesDTO(entity.getId(), entity.getX(), entity.getY());
    }

    public Coordinates toCoordinatesEntity(CoordinatesDTO dto) {
        if (dto == null) {
            return null;
        }
        Coordinates entity = new Coordinates();
        entity.setId(dto.id());
        entity.setX(dto.x());
        entity.setY(dto.y());
        return entity;
    }

    public HumanDTO toHumanDto(Human entity) {
        if (entity == null) {
            return null;
        }
	    return new HumanDTO(entity.getId(), entity.getName(), entity.getAge(), entity.getHeight(), entity.getBirthday());
    }

    public Human toHumanEntity(HumanDTO dto) {
        if (dto == null) {
            return null;
        }
        Human entity = new Human();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setAge(dto.age());
        entity.setHeight(dto.height());
        entity.setBirthday(dto.birthday());
        return entity;
    }
    
    public Coordinates createNewCoordinates(Double x, Double y) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(x);
        coordinates.setY(y);
        return coordinates;
    }
    public Human createNewHuman(String name, Long age, Double height, LocalDate birthday) {
        Human human = new Human();
        human.setName(name);
        human.setAge(age);
        human.setHeight(height);
        human.setBirthday(birthday);
        return human;
    }
}