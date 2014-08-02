package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.Profile;

/**
 * Created by AlonsoUSA on 22/07/14.
 *
 */
public class SecurityLevel {

    private String securityClass;
    private int securityLevel;

    public SecurityLevel() {
    }

    public SecurityLevel(String securityClass, int securityLevel) {
        this.securityClass = securityClass;
        this.securityLevel = securityLevel;
    }

    public String getSecurityClass() {
        return securityClass;
    }

    public void setSecurityClass(String securityClass) {
        this.securityClass = securityClass;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public boolean securityClassEquals(String a) {
        return a.equals(securityClass);
    }

    public static SecurityLevel generate(Profile profile){
        // TODO Hacer todo el análisis del dispositivo para determinar el securityLevel
        return new SecurityLevel("A", 1);
    }
}
