package ru.oogis.searadar.api.message;

import java.sql.Timestamp;

/**
 * Based on NMEA Water speed and heading (VHW)
 */
public class WaterSpeedHeadingMessage extends SearadarStationMessage{

    private Double course;
    private String courseAttr;
    private Double speed;
    private String speedUnit;


    public WaterSpeedHeadingMessage() {

    }


    @Override
    public Timestamp getMsgRecTime() {
        return super.getMsgRecTime();
    }

    @Override
    public void setMsgRecTime(Timestamp msgRecTime) {
        super.setMsgRecTime(msgRecTime);
    }

    public Double getCourse() {
        return course;
    }

    public void setCourse(Double course) {
        this.course = course;
    }

    public String getCourseAttr() {
        return courseAttr;
    }

    public void setCourseAttr(String courseAttr) {
        this.courseAttr = courseAttr;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    @Override
    public String toString() {
        return "WaterSpeedHeadingMessage{" +
                "msgRecTime=" + getMsgRecTime() +
                ", course=" + course +
                ", courseAttr=" + courseAttr +
                ", speed=" + speed +
                ", speedUnit=" + speedUnit +
                '}';
    }
}
