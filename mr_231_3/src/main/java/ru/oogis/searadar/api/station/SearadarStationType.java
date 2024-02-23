package ru.oogis.searadar.api.station;

import ru.oogis.hydra.api.MessageSourceType;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.Map;

public interface SearadarStationType extends MessageSourceType<SearadarStationMessage> {

    Map<String, Object> getBeansForRegistration();

}
