package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticTransmission {

    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_RESPONSE = 2;

    private long idTransmission;

    private int transmissionType;
    private int contentLenght;
    private long timestamp;

    public AnalyticTransmission(int transmissionType, int contentLenght, long timestamp) {
        this.transmissionType = transmissionType;
        this.contentLenght = contentLenght;
        this.timestamp = timestamp;
    }

    public long getIdTransmission() {
        return idTransmission;
    }

    public void setIdTransmission(long idTransmission) {
        this.idTransmission = idTransmission;
    }

    public int getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(int transmissionType) {
        this.transmissionType = transmissionType;
    }

    public int getContentLenght() {
        return contentLenght;
    }

    public void setContentLenght(int contentLenght) {
        this.contentLenght = contentLenght;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
