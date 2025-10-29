package itmo.cityservice.model.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.Valid;


import jakarta.annotation.Generated;
import lombok.Setter;

/**
 * GetCities200Response
 */
@Setter
@JsonTypeName("getCities_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T12:00:39.661908700+03:00[Europe/Moscow]")
@JacksonXmlRootElement(localName = "CityResponse")
public class GetCities200Response implements Serializable {
  private Long id;

  @Valid
  private List<@Valid CityDTO> data;

  private Integer page;

  private Integer pageSize;

  private Integer totalPages;

  public GetCities200Response data(List<@Valid CityDTO> data) {
    this.data = data;
    return this;
  }

  public GetCities200Response addDataItem(CityDTO dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @Valid 
  @JsonProperty("data")
  public List<@Valid CityDTO> getData() {
    return data;
  }
	
	public GetCities200Response page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Текущий номер страницы
   * @return page
  */
  
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }
	
	public GetCities200Response pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Размер страницы
   * @return pageSize
  */
  
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }
	
	public GetCities200Response totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Общее количество страниц
   * @return totalPages
  */
  
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }
	
	@Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetCities200Response getCities200Response = (GetCities200Response) o;
    return Objects.equals(this.data, getCities200Response.data) &&
        Objects.equals(this.page, getCities200Response.page) &&
        Objects.equals(this.pageSize, getCities200Response.pageSize) &&
        Objects.equals(this.totalPages, getCities200Response.totalPages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, page, pageSize, totalPages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetCities200Response {\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
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

