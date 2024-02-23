package ru.oogis.searadar.api.message;

import java.sql.Timestamp;

/**
 * Based on NMEA Radar System Data (RSD)
 */
public class RadarSystemDataMessage extends SearadarStationMessage{

    private Double initialDistance;
    private Double initialBearing;
    private Double movingCircleOfDistance;
    private Double bearing;
    private Double distanceFromShip;
    private Double bearing2;
    private Double distanceScale;
    private String distanceUnit;
    private String displayOrientation;
    private String workingMode;


    public RadarSystemDataMessage() {

    }


    @Override
    public Timestamp getMsgRecTime() {
        return super.getMsgRecTime();
    }

    @Override
    public void setMsgRecTime(Timestamp msgRecTime) {
        super.setMsgRecTime(msgRecTime);
    }

    public Double getInitialDistance() {
        return initialDistance;
    }

    public void setInitialDistance(Double initialDistance) {
        this.initialDistance = initialDistance;
    }

    public Double getInitialBearing() {
        return initialBearing;
    }

    public void setInitialBearing(Double initialBearing) {
        this.initialBearing = initialBearing;
    }

    public Double getMovingCircleOfDistance() {
        return movingCircleOfDistance;
    }

    public void setMovingCircleOfDistance(Double movingCircleOfDistance) {
        this.movingCircleOfDistance = movingCircleOfDistance;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getDistanceFromShip() {
        return distanceFromShip;
    }

    public void setDistanceFromShip(Double distanceFromShip) {
        this.distanceFromShip = distanceFromShip;
    }

    public Double getBearing2() {
        return bearing2;
    }

    public void setBearing2(Double bearing2) {
        this.bearing2 = bearing2;
    }

    public Double getDistanceScale() {
        return distanceScale;
    }

    public void setDistanceScale(Double distanceScale) {
        this.distanceScale = distanceScale;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(String displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    public String getWorkingMode() {
        return workingMode;
    }

    public void setWorkingMode(String workingMode) {
        this.workingMode = workingMode;
    }

    @Override
    public String toString() {
        return "RadarSystemData{" +
                "msgRecTime=" +
                ", initialDistance=" + initialDistance +
                ", initialBearing=" + initialBearing +
                ", movingCircleOfDistance=" + movingCircleOfDistance +
                ", bearing=" + bearing +
                ", distanceFromShip=" + distanceFromShip +
                ", bearing2=" + bearing2 +
                ", distanceScale=" + distanceScale +
                ", distanceUnit=" + distanceUnit +
                ", displayOrientation=" + displayOrientation +
                ", workingMode=" + workingMode +
                '}';
    }
}
