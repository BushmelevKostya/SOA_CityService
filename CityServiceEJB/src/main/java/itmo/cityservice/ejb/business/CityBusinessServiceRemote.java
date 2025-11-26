package itmo.cityservice.ejb.business;

import itmo.cityservice.ejb.model.dto.*;

import java.util.List;

public interface CityBusinessServiceRemote {
    CitiesResponseDto getCities(List<String> sort, int page, int pageSize, String filter);
    
    CityDto getCityById(Long id);
    
    CityDto createCity(CityCreateRequestDto dto);
    
    CityDto updateCity(Long id, CityCreateRequestDto dto);
    
    void deleteCity(Long id);
    
    DeleteResultDto deleteCitiesBySeaLevel(Double metersAboveSeaLevel);
    
    AverageResultDto getAverageCarCode();
    
    CityDto getCityWithMinName();
}

