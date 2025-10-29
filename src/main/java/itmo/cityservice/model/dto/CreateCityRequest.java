package itmo.cityservice.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import itmo.cityservice.model.entity.Climate;
import itmo.cityservice.model.entity.StandardOfLiving;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import jakarta.annotation.Generated;
import lombok.Setter;

/**
 * CreateCityRequest
 */

@Setter
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T12:00:39.661908700+03:00[Europe/Moscow]")
@JacksonXmlRootElement(localName = "CityRequest")
public class CreateCityRequest implements Serializable {
  private Long id;

  private String name;

  private CoordinatesDTO coordinates;

  private Integer area;

  private Integer population;

  private Double metersAboveSeaLevel = null;

  private Long carCode;

  private Climate climate;

  private StandardOfLiving standardOfLiving;

  private HumanDTO governor;

  public CreateCityRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateCityRequest(String name, CoordinatesDTO coordinates, Integer area, Integer population, Long carCode, Climate climate, HumanDTO governor) {
    this.name = name;
    this.coordinates = coordinates;
    this.area = area;
    this.population = population;
    this.carCode = carCode;
    this.climate = climate;
    this.governor = governor;
  }

  public CreateCityRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Название города
   * @return name
  */
  @NotNull @Size(min = 1) 
  @JsonProperty("name")
  public String getName() {
    return name;
  }
	
	public CreateCityRequest coordinates(CoordinatesDTO coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  /**
   * Get coordinates
   * @return coordinates
  */
  @NotNull @Valid 
  @JsonProperty("coordinates")
  public CoordinatesDTO getCoordinates() {
    return coordinates;
  }
	
	public CreateCityRequest area(Integer area) {
    this.area = area;
    return this;
  }

  /**
   * Площадь города (> 0)
   * minimum: 1
   * @return area
  */
  @NotNull @Min(1) 
  @JsonProperty("area")
  public Integer getArea() {
    return area;
  }
	
	public CreateCityRequest population(Integer population) {
    this.population = population;
    return this;
  }

  /**
   * Население города (> 0)
   * minimum: 1
   * @return population
  */
  @NotNull @Min(1) 
  @JsonProperty("population")
  public Integer getPopulation() {
    return population;
  }
	
	public CreateCityRequest metersAboveSeaLevel(Double metersAboveSeaLevel) {
    this.metersAboveSeaLevel = metersAboveSeaLevel;
    return this;
  }

  /**
   * Высота над уровнем моря
   * @return metersAboveSeaLevel
  */
  
  @JsonProperty("metersAboveSeaLevel")
  public Double getMetersAboveSeaLevel() {
    return metersAboveSeaLevel;
  }
	
	public CreateCityRequest carCode(Long carCode) {
    this.carCode = carCode;
    return this;
  }

  /**
   * Код автомобиля (1-1000)
   * minimum: 1
   * maximum: 1000
   * @return carCode
  */
  @NotNull @Min(1L) @Max(1000L) 
  @JsonProperty("carCode")
  public Long getCarCode() {
    return carCode;
  }
	
	public CreateCityRequest climate(Climate climate) {
    this.climate = climate;
    return this;
  }

  /**
   * Климат
   *
   * @return climate
   */
  @JsonProperty("climate")
  public Climate getClimate() {
    return climate;
  }
	
	public CreateCityRequest standardOfLiving(StandardOfLiving standardOfLiving) {
    this.standardOfLiving = standardOfLiving;
    return this;
  }

  /**
   * Уровень жизни
   *
   * @return standardOfLiving
   */
  
  @JsonProperty("standardOfLiving")
  public StandardOfLiving getStandardOfLiving() {
    return standardOfLiving;
  }
	
	public CreateCityRequest governor(HumanDTO governor) {
    this.governor = governor;
    return this;
  }

  /**
   * Get governor
   * @return governor
  */
  @NotNull @Valid 
  @JsonProperty("governor")
  public HumanDTO getGovernor() {
    return governor;
  }
	
	@Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateCityRequest createCityRequest = (CreateCityRequest) o;
    return Objects.equals(this.name, createCityRequest.name) &&
        Objects.equals(this.coordinates, createCityRequest.coordinates) &&
        Objects.equals(this.area, createCityRequest.area) &&
        Objects.equals(this.population, createCityRequest.population) &&
        Objects.equals(this.metersAboveSeaLevel, createCityRequest.metersAboveSeaLevel) &&
        Objects.equals(this.carCode, createCityRequest.carCode) &&
        Objects.equals(this.climate, createCityRequest.climate) &&
        Objects.equals(this.standardOfLiving, createCityRequest.standardOfLiving) &&
        Objects.equals(this.governor, createCityRequest.governor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, coordinates, area, population, metersAboveSeaLevel, carCode, climate, standardOfLiving, governor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateCityRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    area: ").append(toIndentedString(area)).append("\n");
    sb.append("    population: ").append(toIndentedString(population)).append("\n");
    sb.append("    metersAboveSeaLevel: ").append(toIndentedString(metersAboveSeaLevel)).append("\n");
    sb.append("    carCode: ").append(toIndentedString(carCode)).append("\n");
    sb.append("    climate: ").append(toIndentedString(climate)).append("\n");
    sb.append("    standardOfLiving: ").append(toIndentedString(standardOfLiving)).append("\n");
    sb.append("    governor: ").append(toIndentedString(governor)).append("\n");
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

