package itmo.cityservice.ejb.business;

import itmo.cityservice.ejb.config.SpringConfig;
import itmo.cityservice.ejb.exception.NotFoundException;
import itmo.cityservice.ejb.mapper.CityMapper;
import itmo.cityservice.ejb.model.dto.*;
import itmo.cityservice.ejb.model.entity.City;
import itmo.cityservice.ejb.repository.CityRepository;
import itmo.cityservice.ejb.service.CityFilterSpecificationBuilder;
import itmo.cityservice.ejb.service.CityRequestValidator;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class CityBusinessServiceBean implements CityBusinessServiceRemote {
    
    private CityRepository cityRepository;
    private CityMapper cityMapper;
    private CityRequestValidator validator;
    private ApplicationContext applicationContext;
    
    @PostConstruct
    public void init() {
        applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        cityRepository = applicationContext.getBean(CityRepository.class);
        cityMapper = applicationContext.getBean(CityMapper.class);
        validator = applicationContext.getBean(CityRequestValidator.class);
    }

    @Override
    public CitiesResponseDto getCities(List<String> sort, int page, int pageSize, String filter) {
        validator.validatePaginationParams(page, pageSize);
        validator.validateSortFields(sort);
        validator.validateFilterParam(filter);

        Specification<City> spec = CityFilterSpecificationBuilder.build(filter);

        Sort sorting = createSort(sort);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sorting);
        Page<City> cityPage = cityRepository.findAll(spec, pageRequest);

        CitiesResponseDto response = new CitiesResponseDto();
        response.setData(cityMapper.toDtoList(cityPage.getContent()));
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalPages(cityPage.getTotalPages());

        return response;
    }

    @Override
    public CityDto getCityById(Long id) {
        validator.validateCityId(id);

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Город с указанным ID не найден"));

        return cityMapper.toDto(city);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CityDto createCity(CityCreateRequestDto dto) {
        validator.validateCityCreateRequest(dto);

        City city = cityMapper.toEntity(dto);
        City savedCity = cityRepository.save(city);

        return cityMapper.toDto(savedCity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CityDto updateCity(Long id, CityCreateRequestDto dto) {
        validator.validateCityId(id);

        validator.validateCityCreateRequest(dto);

        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Город с указанным ID не найден"));

        cityMapper.updateEntityFromDto(existingCity, dto);
        City updatedCity = cityRepository.save(existingCity);

        return cityMapper.toDto(updatedCity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCity(Long id) {
        validator.validateCityId(id);

        if (!cityRepository.existsById(id)) {
            throw new NotFoundException("Город с указанным ID не найден");
        }

        cityRepository.deleteById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DeleteResultDto deleteCitiesBySeaLevel(Double metersAboveSeaLevel) {
        validator.validateSeaLevelParam(metersAboveSeaLevel);

        List<City> citiesToDelete = cityRepository.findByMetersAboveSeaLevel(metersAboveSeaLevel);
        cityRepository.deleteAll(citiesToDelete);

        DeleteResultDto result = new DeleteResultDto();
        result.setDeletedCount(citiesToDelete.size());
        return result;
    }

    @Override
    public AverageResultDto getAverageCarCode() {
        Double average = cityRepository.findAverageCarCode();

        if (average == null) {
            throw new NotFoundException("Коллекция пуста");
        }

        AverageResultDto result = new AverageResultDto();
        result.setAverage(average);
        return result;
    }

    @Override
    public CityDto getCityWithMinName() {
        City city = cityRepository.findFirstByOrderByNameAsc()
                .orElseThrow(() -> new NotFoundException("Коллекция пуста"));

        return cityMapper.toDto(city);
    }

    private Sort createSort(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (String sortField : sortFields) {
            boolean isDescending = sortField.startsWith("-");
            String fieldName = isDescending ? sortField.substring(1) : sortField;

            orders.add(isDescending ?
                    Sort.Order.desc(fieldName) :
                    Sort.Order.asc(fieldName));
        }

        return Sort.by(orders);
    }
}

