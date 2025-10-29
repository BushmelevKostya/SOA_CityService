package itmo.cityservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "city")
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @Column(nullable = false)
    private Integer area;

    @Column(nullable = false)
    private Integer population;

    @Column(name = "meters_above_sea_level")
    private Double metersAboveSeaLevel;

    @Column(name = "car_code", nullable = false)
    private Long carCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Climate climate;

    @Enumerated(EnumType.STRING)
    @Column(name = "standard_of_living")
    private StandardOfLiving standardOfLiving;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "governor_id")
    private Human governor;
}

