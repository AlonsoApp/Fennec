package com.cloupix.fennec.util;

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
    }
}
