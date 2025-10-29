package itmo.cityservice.service.impl;

import itmo.cityservice.mapper.CityMapper;
import itmo.cityservice.model.dto.CityDTO;
import itmo.cityservice.model.dto.CreateCityRequest;
import itmo.cityservice.model.entity.City;
import itmo.cityservice.model.entity.Coordinates;
import itmo.cityservice.model.entity.Human;
import itmo.cityservice.repository.CityRepository;
import itmo.cityservice.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements CityService {
	private final CityRepository cityRepository;
	private final CityMapper cityMapper;
	
	@Override
	@Transactional(readOnly = true)
	public CityDTO getCityById(Long id) {
		City city = cityRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("City not found with id: " + id));
		return cityMapper.toDto(city);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<CityDTO> getCities(Optional<List<String>> sort, Optional<Integer> page,
	                               Optional<Integer> pageSize, Optional<Map<String, String>> filter) {
		
		Pageable pageable = createPageable(page, pageSize, sort);
		Specification<City> spec = createFilterSpecification(filter);
		Page<City> cityPage = cityRepository.findAll(spec, pageable);
		
		List<CityDTO> dtoList = cityPage.getContent().stream()
				.map(cityMapper::toDto)
				.toList();
		
		return new PageImpl<>(dtoList, pageable, cityPage.getTotalElements());
	}
	
	@Override
	public CityDTO createCity(Long id, CreateCityRequest createCityRequest) {
		if (cityRepository.existsById(id)) {
			throw new RuntimeException("City with id " + id + " already exists");
		}
		
		City city = cityMapper.toEntity(createCityRequest);
		city.setId(id);
		City savedEntity = cityRepository.save(city);
		return cityMapper.toDto(savedEntity);
	}
	
	@Override
	public CityDTO updateCity(Long id, CreateCityRequest createCityRequest) {
		City existingEntity = cityRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("City not found with id: " + id));
		
		cityMapper.updateEntityFromDto(createCityRequest, existingEntity);
		City updatedEntity = cityRepository.save(existingEntity);
		return cityMapper.toDto(updatedEntity);
	}
	
	@Override
	public void deleteCity(Long id) {
		if (!cityRepository.existsById(id)) {
			throw new RuntimeException("City not found with id: " + id);
		}
		cityRepository.deleteById(id);
	}
	
	private Pageable createPageable(Optional<Integer> page, Optional<Integer> pageSize,
	                                Optional<List<String>> sort) {
		int pageNumber = page.orElse(1) - 1;
		int size = pageSize.orElse(20);
		
		if (sort.isPresent() && !sort.get().isEmpty()) {
			List<Sort.Order> orders = sort.get().stream()
					.map(this::createSortOrder)
					.toList();
			return PageRequest.of(pageNumber, size, Sort.by(orders));
		}
		
		return PageRequest.of(pageNumber, size);
	}
	
	private Sort.Order createSortOrder(String sortField) {
		if (sortField.startsWith("-")) {
			return Sort.Order.desc(sortField.substring(1));
		} else {
			return Sort.Order.asc(sortField);
		}
	}
	private Specification<City> createFilterSpecification(Optional<Map<String, String>> filter) {
		return (root, query, criteriaBuilder) -> {
			if (filter.isEmpty() || filter.get().isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			
			List<Predicate> predicates = new ArrayList<>();
			for (Map.Entry<String, String> entry : filter.get().entrySet()) {
				String field = entry.getKey();
				String value = entry.getValue();
				
				if (field.contains(".")) {
					String[] path = field.split("\\.");
					if (path.length == 2) {
						predicates.add(criteriaBuilder.equal(
								root.get(path[0]).get(path[1]),
								parseValue(value)
						));
					}
				} else {
					predicates.add(criteriaBuilder.equal(root.get(field), parseValue(value)));
				}
			}
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	private Object parseValue(String value) {
		try {
			if (value.equals("true") || value.equals("false")) {
				return Boolean.parseBoolean(value);
			} else if (value.contains(".")) {
				return Double.parseDouble(value);
			} else {
				return Long.parseLong(value);
			}
		} catch (NumberFormatException e) {
			return value;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Double getAverageCarCode() {
		return cityRepository.getAverageCarCode();
	}
	
	@Override
	@Transactional(readOnly = true)
	public String getCityWithMinName() {
		return cityRepository.findCityWithMinName();
	}
	
	@Override
	@Transactional
	public void deleteCitiesBySeaLevel(Double metersAboveSeaLevel) {
		cityRepository.deleteByMetersAboveSeaLevel(metersAboveSeaLevel);
	}
}