package ru.oogis.hydra.validate.nmea;

import ru.oogis.hydra.validate.PayloadValidator;

public class NMEACRCValidator implements PayloadValidator<String> {

    @Override
    public boolean validate(String p_payload) {
        return isCRCValid(p_payload);
    }

    protected boolean isCRCValid(String p_payLoad) {
        if (p_payLoad != null) {
            if (p_payLoad.contains("*")) {
                int a_starPos = p_payLoad.indexOf("*");
                int a_expected = Integer.valueOf(p_payLoad.substring(a_starPos + 1), 16);
                String a_str = p_payLoad.substring(1, a_starPos);
                byte[] a_buf = a_str.getBytes();
                int a_crc = 0;
                for (byte b : a_buf) {
                    a_crc ^= b;
                }
                return a_expected == a_crc;
            } else throw new StringIndexOutOfBoundsException(-1);
        }
        return false;
    }
}
