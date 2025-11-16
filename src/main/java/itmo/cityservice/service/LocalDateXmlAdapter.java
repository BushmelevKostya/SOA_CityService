package itmo.cityservice.service;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return v != null ? LocalDateTime.parse(v, FORMATTER) : null;
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v != null ? v.format(FORMATTER) : null;
    }
}
