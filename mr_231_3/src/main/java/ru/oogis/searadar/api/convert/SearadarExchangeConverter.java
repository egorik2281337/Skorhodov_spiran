package ru.oogis.searadar.api.convert;

import org.apache.camel.Exchange;
import ru.oogis.hydra.api.ExchangeConverter;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.List;


public interface SearadarExchangeConverter extends ExchangeConverter<SearadarStationMessage> {

    List<SearadarStationMessage> convert(Exchange exchange) throws Exception;

}