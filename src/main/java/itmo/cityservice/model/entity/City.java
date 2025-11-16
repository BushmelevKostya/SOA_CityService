package itmo.cityservice.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(nullable = false)
    private Integer area;

    @Column(nullable = false)
    private Integer population;

    @Column(name = "meters_above_sea_level")
    private Double metersAboveSeaLevel;

    @Column(name = "car_code", nullable = false)
    private Integer carCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Climate climate;

    @Enumerated(EnumType.STRING)
    @Column(name = "standard_of_living")
    private StandardOfLiving standardOfLiving;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "governor_name")),
            @AttributeOverride(name = "age", column = @Column(name = "governor_age")),
            @AttributeOverride(name = "height", column = @Column(name = "governor_height")),
            @AttributeOverride(name = "birthday", column = @Column(name = "governor_birthday"))
    })
    private Human governor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Double getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    public void setMetersAboveSeaLevel(Double metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public Integer getCarCode() {
        return carCode;
    }

    public void setCarCode(Integer carCode) {
        this.carCode = carCode;
    }

    public Climate getClimate() {
        return climate;
    }

    public void setClimate(Climate climate) {
        this.climate = climate;
    }

    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }

    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public Human getGovernor() {
        return governor;
    }

    public void setGovernor(Human governor) {
        this.governor = governor;
    }
}

