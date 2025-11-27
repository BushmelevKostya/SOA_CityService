package itmo.cityservice.ejb.model.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class ErrorResponse {
    private String message;

    public ErrorResponse() {}

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
