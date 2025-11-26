package itmo.cityservice.ejb.service;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;

public class BigDecimalXmlAdapter extends XmlAdapter<String, BigDecimal> {
    @Override
    public BigDecimal unmarshal(String v) throws Exception {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + v, e);
        }
    }

    @Override
    public String marshal(BigDecimal v) throws Exception {
        return v != null ? v.toPlainString() : null;
    }
}

