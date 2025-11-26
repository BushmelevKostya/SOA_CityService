package itmo.cityservice.ejb.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Embeddable
public class Human {
    @Column(nullable = false)
    private String name;

    private Integer age;

    private Double height;

    @Column(name = "birthday")
    private LocalDate birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}

