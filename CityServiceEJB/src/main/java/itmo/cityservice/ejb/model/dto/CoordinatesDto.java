package itmo.cityservice.ejb.model.dto;

import itmo.cityservice.ejb.service.BigDecimalXmlAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class CoordinatesDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    private BigDecimal x;

    @XmlElement
    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    private BigDecimal y;

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }
}

