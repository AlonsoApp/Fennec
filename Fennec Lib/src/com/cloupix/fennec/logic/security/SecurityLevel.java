package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.util.R;

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

    public static SecurityLevel generate(){
        if(R.getInstance().getSecurityLevel() != null)
            return R.getInstance().getSecurityLevel();
        else
            return generateDynamically();
    }

    public static SecurityLevel generateDynamically(){
        // TODO Hacer el benchmarc ya que el user no ah especificado security level
        System.out.print("Generando nivel de seguridad automatico");
        return new SecurityLevel("B", 0);
    }
}
