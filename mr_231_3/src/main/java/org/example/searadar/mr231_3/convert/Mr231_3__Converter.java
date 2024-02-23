package org.example.searadar.mr231_3.convert;

import org.apache.camel.Exchange;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.*;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


//Класс для парсинга по протоколу mr-231-3
 
public class Mr231_3Converter implements SearadarExchangeConverter {

    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};
    private static final Pattern TTM_PATTERN = Pattern.compile("\\$1RA2TTM3,(\\d{2}),(\\d{2}\\.\\d{2}),(\\d{3}\\.\\d),(T),(\\d{2}\\.\\d),(\\d{3}\\.\\d),(T),(\\d{2}\\.\\d),(\\d{3}\\.\\d),(\\d{2}\\.\\d),(\\d{2}\\.\\d),([bpd]),([LQT]),\\d*,([AP]),([0-9A-Fa-f]{4})\\*\\d{2}.*");
    private static final Pattern RSD_PATTERN = Pattern.compile("\\$1RA2RSD3,(\\d+\\.\\d),(\\d+\\.\\d),(\\d+\\.\\d),(\\d+\\.\\d),.*,(\\d+\\.\\d),(\\d+\\.\\d),([KN]),([CHN]),([SP]),\\d*,([0-9A-Fa-f]{4})\\*\\d{2}.*");

    private String[] fields;
    private String msgType;

    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) {
        return convert(exchange.getIn().getBody(String.class));
    }

    public List<SearadarStationMessage> convert(String message) {
        List<SearadarStationMessage> msgList = new ArrayList<>();
        readFields(message);

        switch (msgType) {
            case "TTM": msgList.add(parseTTM());
                break;
            case "RSD": {
                RadarSystemDataMessage rsd = parseRSD();
                InvalidMessage invalidMessage = checkRSD(rsd);
                if (invalidMessage != null)  msgList.add(invalidMessage);
                else msgList.add(rsd);
                break;
            }
        }

        return msgList;
    }
// Разбитие одной строки на части
    private void readFields(String msg) {
        String temp = msg.substring(3, msg.indexOf("*")).trim();
        fields = temp.split(Pattern.quote(","));
        msgType = fields[0];
    }
     
     //Класс, наследуемый от TrackedTargetMessage. Нужен для реализации установки значения интервала (period) и вывода его на экран
     
    private TrackedTargetMessage parseTTM() {
        Matcher matcher = TTM_PATTERN.matcher(String.join(",", fields));
        if (matcher.matches()) {
            TrackedTargetMessage ttm = new TrackedTargetMessage();
            Long msgRecTimeMillis = System.currentTimeMillis();

            ttm.setMsgTime(msgRecTimeMillis);
            TargetStatus status = TargetStatus.UNRELIABLE_DATA;
            IFF iff = IFF.UNKNOWN;
            TargetType type = TargetType.UNKNOWN;

            switch (fields[12]) {
                case "L" : status = TargetStatus.LOST;
                    break;
                case "Q" : status = TargetStatus.UNRELIABLE_DATA;
                    break;
                case "T" : status = TargetStatus.TRACKED;
                    break;
            }

            switch (fields[11]) {
                case "b" : iff = IFF.FRIEND;
                    break;
                case "p" : iff = IFF.FOE;
                    break;
                case "d" : iff = IFF.UNKNOWN;
                    break;
            }

            ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
            ttm.setTargetNumber(Integer.parseInt(fields[1]));
            ttm.setDistance(Double.parseDouble(fields[2]));
            ttm.setBearing(Double.parseDouble(fields[3]));
            ttm.setCourse(Double.parseDouble(fields[6]));
            ttm.setSpeed(Double.parseDouble(fields[5]));
            ttm.setStatus(status);
            ttm.setIff(iff);

            ttm.setType(type);

            return ttm;
        } else {
            throw new IllegalArgumentException("Invalid TTM format: " + Arrays.toString(fields));
        }
    }
     // Класс, наследуемый от RadarSystemDataMessage. Нужен для установки значения контрольной суммы и вывода на экран
    
    private RadarSystemDataMessage parseRSD() {
        Matcher matcher = RSD_PATTERN.matcher(String.join(",", fields));
        if (matcher.matches()) {
            RadarSystemDataMessage rsd = new RadarSystemDataMessage();

            rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
            rsd.setInitialDistance(Double.parseDouble(fields[1]));
            rsd.setInitialBearing(Double.parseDouble(fields[2]));
            rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
            rsd.setBearing(Double.parseDouble(fields[4]));
            rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
            rsd.setBearing2(Double.parseDouble(fields[10]));
            rsd.setDistanceScale(Double.parseDouble(fields[11]));
            rsd.setDistanceUnit(fields[12]);
            rsd.setDisplayOrientation(fields[13]);
            rsd.setWorkingMode(fields[14]);

            return rsd;
        } else {
            throw new IllegalArgumentException("Invalid RSD format: " + Arrays.toString(fields));
        }
    }

    private InvalidMessage checkRSD(RadarSystemDataMessage rsd) {
        InvalidMessage invalidMessage = new InvalidMessage();
        String infoMsg = "";

        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {
            infoMsg = "RSD message. Wrong distance scale value: " + rsd.getDistanceScale();
            invalidMessage.setInfoMsg(infoMsg);
            return invalidMessage;
        }

        return null;
    }
}