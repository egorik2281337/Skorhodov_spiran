package ru.oogis.searadar.api.message;

import java.io.Serializable;
import java.sql.Timestamp;

public class SearadarStationMessage implements Serializable {

    private Timestamp msgRecTime;              // Время на момент получения сообщения


    public SearadarStationMessage() {}


    public Timestamp getMsgRecTime() {
        return msgRecTime;
    }

    public void setMsgRecTime(Timestamp msgRecTime) {
        this.msgRecTime = msgRecTime;
    }

}
