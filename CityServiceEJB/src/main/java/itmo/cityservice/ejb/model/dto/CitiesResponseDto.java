package itmo.cityservice.ejb.model.dto;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "CitiesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CitiesResponseDto {
    @XmlElementWrapper(name = "data")
    @XmlElement(name = "City")
    private List<CityDto> data;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public List<CityDto> getData() {
        return data;
    }

    public void setData(List<CityDto> data) {
        this.data = data;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}

