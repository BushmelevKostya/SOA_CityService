package itmo.cityservice.model.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.Column;

import java.time.LocalDate;
@JacksonXmlRootElement(localName = "Human")
public record HumanDTO(Long id, String name, Long age, Double height, LocalDate birthday) {

	
	@Override
	public Long id() {
		return id;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Long age() {
		return age;
	}
	
	@Override
	public Double height() {
		return height;
	}
	
	@Override
	public LocalDate birthday() {
		return birthday;
	}
}