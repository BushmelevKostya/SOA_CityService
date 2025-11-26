package itmo.cityservice.service;

import itmo.cityservice.ejb.business.CityBusinessServiceBean;
import itmo.cityservice.ejb.business.CityBusinessServiceRemote;
import itmo.cityservice.ejb.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityBusinessServiceRemote cityBusinessService;

    public CityService() {
        this.cityBusinessService = new CityBusinessServiceBean();
    }

    public CitiesResponseDto getCities(List<String> sort, int page, int pageSize, String filter) {
        return cityBusinessService.getCities(sort, page, pageSize, filter);
    }

    public CityDto getCityById(Long id) {
        return cityBusinessService.getCityById(id);
    }

    public CityDto createCity(CityCreateRequestDto dto) {
        return cityBusinessService.createCity(dto);
    }

    public CityDto updateCity(Long id, CityCreateRequestDto dto) {
        return cityBusinessService.updateCity(id, dto);
    }

    public void deleteCity(Long id) {
        cityBusinessService.deleteCity(id);
    }

    public DeleteResultDto deleteCitiesBySeaLevel(Double metersAboveSeaLevel) {
        return cityBusinessService.deleteCitiesBySeaLevel(metersAboveSeaLevel);
    }

    public AverageResultDto getAverageCarCode() {
        return cityBusinessService.getAverageCarCode();
    }

    public CityDto getCityWithMinName() {
        return cityBusinessService.getCityWithMinName();
    }
}
