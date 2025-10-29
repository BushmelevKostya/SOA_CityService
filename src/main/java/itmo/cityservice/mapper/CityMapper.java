package itmo.cityservice.mapper;

import itmo.cityservice.model.dto.CityCreateRequestDto;
import itmo.cityservice.model.dto.CityDto;
import itmo.cityservice.model.dto.CoordinatesDto;
import itmo.cityservice.model.dto.HumanDto;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.model.entity.Coordinates;
import itmo.cityservice.model.entity.Human;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CityMapper {

    public CityDto toDto(City city) {
        if (city == null) {
            return null;
        }

        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setCoordinates(toDto(city.getCoordinates()));
        dto.setCreationDate(city.getCreationDate());
        dto.setArea(city.getArea());
        dto.setPopulation(city.getPopulation());
        dto.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        dto.setCarCode(city.getCarCode());
        dto.setClimate(city.getClimate());
        dto.setStandardOfLiving(city.getStandardOfLiving());
        dto.setGovernor(toDto(city.getGovernor()));

        return dto;
    }

    public List<CityDto> toDtoList(List<City> cities) {
        return cities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public City toEntity(CityCreateRequestDto dto) {
        if (dto == null) {
            return null;
        }

        City city = new City();
        city.setName(dto.getName());
        city.setCoordinates(toEntity(dto.getCoordinates()));
        city.setArea(dto.getArea());
        city.setPopulation(dto.getPopulation());
        city.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        city.setCarCode(dto.getCarCode());
        city.setClimate(dto.getClimate());
        city.setStandardOfLiving(dto.getStandardOfLiving());
        city.setGovernor(toEntity(dto.getGovernor()));

        return city;
    }

    public CoordinatesDto toDto(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }

        CoordinatesDto dto = new CoordinatesDto();
        dto.setX(coordinates.getX());
        dto.setY(coordinates.getY());

        return dto;
    }

    public Coordinates toEntity(CoordinatesDto dto) {
        if (dto == null) {
            return null;
        }

        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());

        return coordinates;
    }

    public HumanDto toDto(Human human) {
        if (human == null) {
            return null;
        }

        HumanDto dto = new HumanDto();
        dto.setName(human.getName());
        dto.setAge(human.getAge());
        dto.setHeight(human.getHeight());
        dto.setBirthday(human.getBirthday());

        return dto;
    }

    public Human toEntity(HumanDto dto) {
        if (dto == null) {
            return null;
        }

        Human human = new Human();
        human.setName(dto.getName());
        human.setAge(dto.getAge());
        human.setHeight(dto.getHeight());
        human.setBirthday(dto.getBirthday());

        return human;
    }

    public void updateEntityFromDto(City existingCity, CityCreateRequestDto dto) {
        if (dto == null || existingCity == null) {
            return;
        }

        existingCity.setName(dto.getName());
        existingCity.setArea(dto.getArea());
        existingCity.setPopulation(dto.getPopulation());
        existingCity.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        existingCity.setCarCode(dto.getCarCode());
        existingCity.setClimate(dto.getClimate());
        existingCity.setStandardOfLiving(dto.getStandardOfLiving());

        if (dto.getCoordinates() != null) {
            if (existingCity.getCoordinates() == null) {
                existingCity.setCoordinates(new Coordinates());
            }
            existingCity.getCoordinates().setX(dto.getCoordinates().getX());
            existingCity.getCoordinates().setY(dto.getCoordinates().getY());
        }

        if (dto.getGovernor() != null) {
            if (existingCity.getGovernor() == null) {
                existingCity.setGovernor(new Human());
            }
            Human governor = existingCity.getGovernor();
            governor.setName(dto.getGovernor().getName());
            governor.setAge(dto.getGovernor().getAge());
            governor.setHeight(dto.getGovernor().getHeight());
            governor.setBirthday(dto.getGovernor().getBirthday());
        }
    }
}
