package ru.oogis.searadar.api.message;

public class InvalidMessage extends SearadarStationMessage{

    private String infoMsg;

    public String getInfoMsg() {
        return infoMsg;
    }

    public void setInfoMsg(String infoMsg) {
        this.infoMsg = infoMsg;
    }
}
