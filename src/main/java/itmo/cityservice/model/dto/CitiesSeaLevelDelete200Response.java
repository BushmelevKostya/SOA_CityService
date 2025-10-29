package itmo.cityservice.model.dto;

import java.io.Serial;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Setter;


import jakarta.annotation.Generated;

/**
 * CitiesSeaLevelDelete200Response
 */
@Setter
@JsonTypeName("_cities_sea_level_delete_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T12:00:39.661908700+03:00[Europe/Moscow]")
@JacksonXmlRootElement(localName = "CitiesSeaLevel")
public class CitiesSeaLevelDelete200Response implements Serializable {
  private Long id;

  private Integer deletedCount;

  public CitiesSeaLevelDelete200Response deletedCount(Integer deletedCount) {
    this.deletedCount = deletedCount;
    return this;
  }

  /**
   * Количество удаленных городов
   * @return deletedCount
  */
  
  @JsonProperty("deletedCount")
  public Integer getDeletedCount() {
    return deletedCount;
  }
	
	@Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CitiesSeaLevelDelete200Response citiesSeaLevelDelete200Response = (CitiesSeaLevelDelete200Response) o;
    return Objects.equals(this.deletedCount, citiesSeaLevelDelete200Response.deletedCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deletedCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CitiesSeaLevelDelete200Response {\n");
    sb.append("    deletedCount: ").append(toIndentedString(deletedCount)).append("\n");
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

