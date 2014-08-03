package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticDeviceCertificate {

    private long idDeviceCertificate;
    private String publicKeyHex;
    private long creationTimestamp;

    public AnalyticDeviceCertificate() {
    }

    public AnalyticDeviceCertificate(String publicKeyHex) {
        this.publicKeyHex = publicKeyHex;
    }

    public long getIdDeviceCertificate() {
        return idDeviceCertificate;
    }

    public void setIdDeviceCertificate(long idDeviceCertificate) {
        this.idDeviceCertificate = idDeviceCertificate;
    }

    public String getPublicKeyHex() {
        return publicKeyHex;
    }

    public void setPublicKeyHex(String publicKeyHex) {
        this.publicKeyHex = publicKeyHex;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
