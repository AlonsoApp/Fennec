package com.cloupix.fennec.util;

import com.cloupix.fennec.business.CertificateInfo;
import com.cloupix.fennec.business.KeystoreInfo;

import java.util.ArrayList;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class ServicesR extends R{

    public ServicesR(){
        super();
        this.nodeType = TYPE_SERVICES;

        this.supernodeDefaultIp = localHostIp;

        this.portExternal = 1170;
        this.portInternalListener = 1171;
        this.portExternalListener = 1172;

        this.keystorePath = "./Node Services/data/keystore.jks";
        this.rootcaPath = "./Node Services/data/root_ca.pem";
        this.authKeyPath = "./Node Services/data/authkey.fnc";
        this.configPath = "./Node Services/data/config.xml";

        // Default keystore info
        ArrayList<CertificateInfo> certList = new ArrayList<CertificateInfo>();
        certList.add(new CertificateInfo("com.cloupix.fennec", "demopassword", false));
        certList.add(new CertificateInfo("signed", "demopassword", true));

        this.keystoreInfo =  new KeystoreInfo("demopassword", certList);
    }
}
