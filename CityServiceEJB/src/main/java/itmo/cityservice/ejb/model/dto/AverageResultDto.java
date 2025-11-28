package itmo.cityservice.ejb.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "AverageResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class AverageResultDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    private Double average;

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }
}

