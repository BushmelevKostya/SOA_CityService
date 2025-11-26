package itmo.cityservice.ejb.model.dto;

import itmo.cityservice.ejb.model.entity.Climate;
import itmo.cityservice.ejb.model.entity.StandardOfLiving;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CityCreateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CityCreateRequestDto {
    private String name;
    private CoordinatesDto coordinates;
    private Integer area;
    private Integer population;
    private Double metersAboveSeaLevel;
    private Integer carCode;
    private Climate climate;
    private StandardOfLiving standardOfLiving;
    private HumanDto governor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinatesDto getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Double getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    public void setMetersAboveSeaLevel(Double metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public Integer getCarCode() {
        return carCode;
    }

    public void setCarCode(Integer carCode) {
        this.carCode = carCode;
    }

    public Climate getClimate() {
        return climate;
    }

    public void setClimate(Climate climate) {
        this.climate = climate;
    }

    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }

    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public HumanDto getGovernor() {
        return governor;
    }

    public void setGovernor(HumanDto governor) {
        this.governor = governor;
    }
}

