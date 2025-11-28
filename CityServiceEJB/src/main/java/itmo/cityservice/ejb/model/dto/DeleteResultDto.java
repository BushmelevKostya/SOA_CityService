package itmo.cityservice.ejb.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "DeleteResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteResultDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    private Integer deletedCount;

    public Integer getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(Integer deletedCount) {
        this.deletedCount = deletedCount;
    }
}

