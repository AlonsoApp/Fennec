package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticAuthentication {

    private long idAuthentication;
    private String authKeySha;
    private byte[] authKey;


    public AnalyticAuthentication() {
    }

    public AnalyticAuthentication(String authKeySha, byte[] authKey) {
        this.authKeySha = authKeySha;
        this.authKey = authKey;
    }

    public long getIdAuthentication() {
        return idAuthentication;
    }

    public void setIdAuthentication(long idAuthentication) {
        this.idAuthentication = idAuthentication;
    }

    public String getAuthKeySha() {
        return authKeySha;
    }

    public void setAuthKeySha(String authKeySha) {
        this.authKeySha = authKeySha;
    }

    public byte[] getAuthKey() {
        return authKey;
    }

    public void setAuthKey(byte[] authKey) {
        this.authKey = authKey;
    }
}
