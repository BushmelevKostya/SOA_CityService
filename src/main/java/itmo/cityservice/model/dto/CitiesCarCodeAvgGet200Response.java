package itmo.cityservice.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Setter;


import jakarta.annotation.Generated;

/**
 * CitiesCarCodeAvgGet200Response
 */
@Setter
@JsonTypeName("_cities_car_code_avg_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T12:00:39.661908700+03:00[Europe/Moscow]")
@JacksonXmlRootElement(localName = "CitiesCarCode")
public class CitiesCarCodeAvgGet200Response implements Serializable {
  private Long id;

  private Double average;

  public CitiesCarCodeAvgGet200Response average(Double average) {
    this.average = average;
    return this;
  }

  /**
   * Среднее значение поля carCode
   * @return average
  */
  
  @JsonProperty("average")
  public Double getAverage() {
    return average;
  }
	
	@Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CitiesCarCodeAvgGet200Response citiesCarCodeAvgGet200Response = (CitiesCarCodeAvgGet200Response) o;
    return Objects.equals(this.average, citiesCarCodeAvgGet200Response.average);
  }

  @Override
  public int hashCode() {
    return Objects.hash(average);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CitiesCarCodeAvgGet200Response {\n");
    sb.append("    average: ").append(toIndentedString(average)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

