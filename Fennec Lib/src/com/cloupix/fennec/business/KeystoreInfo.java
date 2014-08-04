package com.cloupix.fennec.business;

import java.util.ArrayList;

/**
 * Created by AlonsoUSA on 04/08/14.
 *
 */
public class KeystoreInfo {

    private String password;
    private ArrayList<CertificateInfo> certificateList;


    public KeystoreInfo() {
    }

    public KeystoreInfo(String password) {
        this.password = password;
        this.certificateList = new ArrayList<CertificateInfo>();
    }

    public KeystoreInfo(String password, ArrayList<CertificateInfo> certificateList) {
        this.password = password;
        this.certificateList = certificateList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<CertificateInfo> getCertificateList() {
        return certificateList;
    }

    public void setCertificateList(ArrayList<CertificateInfo> certificateList) {
        this.certificateList = certificateList;
    }

    public void addCertificateInfo(CertificateInfo certificateInfo){
        this.certificateList.add(certificateInfo);
    }

    public CertificateInfo getCertificateInfo(boolean signed){
        for(CertificateInfo cert : certificateList){
            if(cert.isSigned() == signed)
                return cert;
        }
        return null;
    }
}
