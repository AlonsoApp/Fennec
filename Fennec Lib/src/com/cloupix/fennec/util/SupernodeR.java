package com.cloupix.fennec.util;

import com.cloupix.fennec.business.CertificateInfo;
import com.cloupix.fennec.business.KeystoreInfo;

import java.util.ArrayList;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class SupernodeR extends R{


    public SupernodeR(){
        super();
        this.nodeType = TYPE_SUPERNODE;

        this.portExternal = 1172;
        this.portInternalListener = 1171;
        this.portExternalListener = 1170;

        this.keystorePath = "./Supernode Proxy/data/keystore.jks";
        this.rootcaPath = "./Supernode Proxy/data/root_ca.pem";
        this.databasePath = "./Supernode Proxy/data/supernode.db";

        // Default keystore info
        ArrayList<CertificateInfo> certList = new ArrayList<CertificateInfo>();
        certList.add(new CertificateInfo("com.cloupix.fennec", "demopassword", false));
        certList.add(new CertificateInfo("signed", "demopassword", true));

        this.keystoreInfo =  new KeystoreInfo("demopassword", certList);
    }
}
