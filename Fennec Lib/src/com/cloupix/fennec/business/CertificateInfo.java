package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 04/08/14.
 *
 */
public class CertificateInfo {

    private String alias;
    private String password;
    private boolean signed;


    public CertificateInfo(String alias, String password, boolean signed) {
        this.alias = alias;
        this.password = password;
        this.signed = signed;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }
}
