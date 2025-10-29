package itmo.cityservice.service;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return v != null ? LocalDate.parse(v) : null;
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v != null ? v.toString() : null;
    }
}
