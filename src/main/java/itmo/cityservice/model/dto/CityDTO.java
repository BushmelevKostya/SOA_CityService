package itmo.cityservice.model.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import itmo.cityservice.model.entity.Climate;
import itmo.cityservice.model.entity.Coordinates;
import itmo.cityservice.model.entity.Human;
import itmo.cityservice.model.entity.StandardOfLiving;

import java.time.OffsetDateTime;
@JacksonXmlRootElement(localName = "City")
public record CityDTO(Long id, String name, CoordinatesDTO coordinates, OffsetDateTime creationDate, Integer area, Integer population, Double metersAboveSeaLevel, Long carCode, Climate climate, StandardOfLiving standardOfLiving, HumanDTO governor) {
	@Override
	public Long id() {
		return id;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public CoordinatesDTO coordinates() {
		return coordinates;
	}
	
	@Override
	public OffsetDateTime creationDate() {
		return creationDate;
	}
	
	@Override
	public Integer area() {
		return area;
	}
	
	@Override
	public Integer population() {
		return population;
	}
	
	@Override
	public Double metersAboveSeaLevel() {
		return metersAboveSeaLevel;
	}
	
	@Override
	public Long carCode() {
		return carCode;
	}
	
	@Override
	public Climate climate() {
		return climate;
	}
	
	@Override
	public StandardOfLiving standardOfLiving() {
		return standardOfLiving;
	}
	
	@Override
	public HumanDTO governor() {
		return governor;
	}
	
}