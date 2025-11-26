package itmo.cityservice.ejb.service;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return v != null ? LocalDate.parse(v, FORMATTER) : null;
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v != null ? v.format(FORMATTER) : null;
    }
}

