package com.cloupix.fennec.business;

import com.cloupix.fennec.logic.network.FennecProtocolV1;
import com.cloupix.fennec.logic.security.SecurityLevel;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticConnection {

    public static final int TYPE_ACTIVE = 1;
    public static final int TYPE_PASSIVE = 2;

    public static final int FENNEC_V1 = 1;

    private long idConnection;
    private String sourceIp;
    private String destinationIp;
    private int destinationPort;
    private int connectionType;
    private int protocolVersion;


    private AnalyticDevice device;
    private AnalyticDeviceCertificate deviceCertificate;
    private SecurityLevel securityLevel;

    public AnalyticConnection() {
        this.device = new AnalyticDevice();
    }

    public AnalyticConnection(String sourceIp, int connectionType, AnalyticDevice device) {
        this.sourceIp = sourceIp;
        this.connectionType = connectionType;
        this.device = device;
        this.device = new AnalyticDevice();
    }

    public long getIdConnection() {
        return idConnection;
    }

    public void setIdConnection(long idConnection) {
        this.idConnection = idConnection;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public AnalyticDevice getDevice() {
        return device;
    }

    public void setDevice(AnalyticDevice device) {
        this.device = device;
    }

    public AnalyticDeviceCertificate getDeviceCertificate() {
        return deviceCertificate;
    }

    public void setDeviceCertificate(AnalyticDeviceCertificate deviceCertificate) {
        this.deviceCertificate = deviceCertificate;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        if(protocolVersion.equals(FennecProtocolV1.versionName)){
            this.protocolVersion = FENNEC_V1;
        }
    }
}
