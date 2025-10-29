package itmo.cityservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
@JacksonXmlRootElement(localName = "Coordinates")
public record CoordinatesDTO(Long id, Double x, Double y) {

	@Override
	public Long id() {
		return id;
	}
	
	@Override
	public Double x() {
		return x;
	}
	
	@Override
	public Double y() {
		return y;
	}
}