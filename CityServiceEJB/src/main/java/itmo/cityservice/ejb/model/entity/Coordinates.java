package itmo.cityservice.ejb.model.entity;

import jakarta.persistence.*;

@Embeddable
public class Coordinates {
    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}

