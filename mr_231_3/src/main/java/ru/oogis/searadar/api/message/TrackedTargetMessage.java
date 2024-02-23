package ru.oogis.searadar.api.message;

import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;


/**
 * Based on NMEA Track Target Message (TTM)
 */
public class TrackedTargetMessage extends SearadarStationMessage {

    private Long msgTime;
    private Integer targetNumber;
    private Double distance;
    private Double bearing;
    private Double course;
    private Double speed;
    private TargetType type;
    private TargetStatus status;
    private IFF iff;


    public TrackedTargetMessage() {}


    @Override
    public Timestamp getMsgRecTime() {
        return super.getMsgRecTime();
    }

    @Override
    public void setMsgRecTime(Timestamp msgRecTime) {
        super.setMsgRecTime(msgRecTime);
    }

    public Long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Long msgTime) {
        this.msgTime = msgTime;
    }

    public Integer getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(Integer targetNumber) {
        this.targetNumber = targetNumber;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getCourse() {
        return course;
    }

    public void setCourse(Double course) {
        this.course = course;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public TargetType getType() {
        return type;
    }

    public void setType(TargetType type) {
        this.type = type;
    }

    public TargetStatus getStatus() {
        return status;
    }

    public void setStatus(TargetStatus status) {
        this.status = status;
    }

    public IFF getIff() {
        return iff;
    }

    public void setIff(IFF iff) {
        this.iff = iff;
    }

    @Override
    public String toString() {
        return "TrackedTargetMessage{" +
                "msgRecTime=" + getMsgRecTime() +
                ", msgTime=" + getMsgTime() +
                ", targetNumber=" + targetNumber +
                ", distance=" + distance +
                ", bearing=" + bearing +
                ", course=" + course +
                ", speed=" + speed +
                ", type=" + type +
                ", status=" + status +
                ", iff=" + iff +
                '}';
    }
}
