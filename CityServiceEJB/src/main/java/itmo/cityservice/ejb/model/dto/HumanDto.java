package itmo.cityservice.ejb.model.dto;

import itmo.cityservice.ejb.service.LocalDateXmlAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement(name = "Human")
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private String name;
    @XmlElement
    private Integer age;
    @XmlElement
    private Double height;
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @XmlElement
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

