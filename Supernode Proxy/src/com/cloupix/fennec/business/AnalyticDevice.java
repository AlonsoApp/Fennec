package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticDevice {

    private long idDevice;
    private AnalyticAuthentication analyticAuthentication;

    public AnalyticDevice() {
    }

    public long getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(long idDevice) {
        this.idDevice = idDevice;
    }

    public AnalyticAuthentication getAnalyticAuthentication() {
        return analyticAuthentication;
    }

    public void setAnalyticAuthentication(AnalyticAuthentication analyticAuthentication) {
        this.analyticAuthentication = analyticAuthentication;
    }
}
