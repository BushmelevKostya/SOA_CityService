package itmo.cityservice.ejb.business;

import itmo.cityservice.ejb.exception.BadRequestException;
import itmo.cityservice.ejb.model.dto.*;
import itmo.cityservice.ejb.model.entity.City;
import itmo.cityservice.ejb.repository.CityRepositoryBean;
import itmo.cityservice.ejb.mapper.CityMapperBean;
import itmo.cityservice.ejb.service.CityFilterBuilder;
import itmo.cityservice.ejb.service.CityValidatorBean;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.jboss.ejb3.annotation.Pool;
import org.jboss.logging.Logger;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Stateless
@Pool("slsb-strict-max-pool")
public class CityBusinessServiceBean implements CityBusinessServiceRemote {
    private static final Logger logger = Logger.getLogger(CityBusinessServiceBean.class.getName());

    @PostConstruct
    public void init() {
        logger.info("EJB Instance CREATED: " + this.hashCode() +
                " | Thread: " + Thread.currentThread().getName());
    }

    @PreDestroy
    public void destroy() {
        logger.info("EJB Instance DESTROYED: " + this.hashCode());
    }

    @EJB
    private CityRepositoryBean cityRepository;

    @EJB
    private CityMapperBean cityMapper;

    @EJB
    private CityValidatorBean validator;

    @Override
    public CitiesResponseDto getCities(List<String> sort, int page, int pageSize, String filter) {
        logger.info("📍 getCities() STARTED | Instance: " + this.hashCode() +
                " | Thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (page < 1) {
            throw new BadRequestException("Номер страницы должен быть больше 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Размер страницы должен быть от 1 до 100");
        }

        if (sort != null && !sort.isEmpty()) {
            CityFilterBuilder.validateSortFields(sort);
        }

        List<City> cities = cityRepository.findAll(sort, filter, page - 1, pageSize);
        long totalCities = cityRepository.count(filter);

        CitiesResponseDto response = new CitiesResponseDto();
        response.setData(cityMapper.toDtoList(cities));
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalPages((int) Math.ceil((double) totalCities / pageSize));
        logger.info("📍 getCities() FINISHED | Instance: " + this.hashCode());
        return response;
    }

    @Override
    public CityDto getCityById(Long id) {
        validator.validateCityId(id);

        City city = cityRepository.findById(id);
        if (city == null) {
            throw new itmo.cityservice.ejb.exception.NotFoundException("Город с указанным ID не найден");
        }

        return cityMapper.toDto(city);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CityDto createCity(CityCreateRequestDto dto) {
        validator.validateCityCreateRequest(dto);

        City city = cityMapper.toEntity(dto);
        city.setCreationDate(ZonedDateTime.now().toLocalDateTime());
        City savedCity = cityRepository.save(city);

        return cityMapper.toDto(savedCity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CityDto updateCity(Long id, CityCreateRequestDto dto) {
        validator.validateCityId(id);
        validator.validateCityCreateRequest(dto);

        City existingCity = cityRepository.findById(id);
        if (existingCity == null) {
            throw new itmo.cityservice.ejb.exception.NotFoundException("Город с указанным ID не найден");
        }

        cityMapper.updateEntityFromDto(existingCity, dto);
        City updatedCity = cityRepository.save(existingCity);

        return cityMapper.toDto(updatedCity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCity(Long id) {
        validator.validateCityId(id);

        City city = cityRepository.findById(id);
        if (city == null) {
            throw new itmo.cityservice.ejb.exception.NotFoundException("Город с указанным ID не найден");
        }

        cityRepository.deleteById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DeleteResultDto deleteCitiesBySeaLevel(Double metersAboveSeaLevel) {
        validator.validateSeaLevelParam(metersAboveSeaLevel);

        List<City> citiesToDelete = cityRepository.findByMetersAboveSeaLevel(metersAboveSeaLevel);
        citiesToDelete.forEach(cityRepository::delete);

        DeleteResultDto result = new DeleteResultDto();
        result.setDeletedCount(citiesToDelete.size());
        return result;
    }

    @Override
    public AverageResultDto getAverageCarCode() {
        Double average = cityRepository.findAverageCarCode();

        if (average == null) {
            throw new itmo.cityservice.ejb.exception.NotFoundException("Коллекция пуста");
        }

        AverageResultDto result = new AverageResultDto();
        result.setAverage(average);
        return result;
    }

    @Override
    public CityDto getCityWithMinName() {
        City city = cityRepository.findFirstByOrderByNameAsc()
                .orElseThrow(() -> new itmo.cityservice.ejb.exception.NotFoundException("Коллекция пуста"));

        return cityMapper.toDto(city);
    }

    @Override
    public int getPoolCurrentSize() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            ObjectName queryName = new ObjectName("jboss.as:subsystem=ejb3,strict-max-bean-instance-pool=*");
            Set<ObjectName> names = server.queryNames(queryName, null);

            for (ObjectName name : names) {
                System.out.println("Пул: " + name);

                MBeanInfo info = server.getMBeanInfo(name);
                System.out.println("Доступные атрибуты:");
                for (MBeanAttributeInfo attr : info.getAttributes()) {
                    System.out.println("  - " + attr.getName() + " (" + attr.getType() + ")");
                }

                try {
                    return (Integer) server.getAttribute(name, "derivedSize");
                } catch (Exception e1) {
                    System.out.println("derivedSize не найден");
                }

                try {
                    return (Integer) server.getAttribute(name, "AvailableCount");
                } catch (Exception e2) {
                    System.out.println("AvailableCount не найден");
                }

                try {
                    return (Integer) server.getAttribute(name, "CreatedCount");
                } catch (Exception e3) {
                    System.out.println("CreatedCount не найден");
                }
            }

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
