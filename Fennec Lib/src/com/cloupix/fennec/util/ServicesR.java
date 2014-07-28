package com.cloupix.fennec.util;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class ServicesR extends R{

    public ServicesR(){
        super();
        this.nodeType = TYPE_SERVICES;

        this.supernodeIp = localHostIp;

        this.portExternal = 1170;
        this.portInternalListener = 1171;
        this.portExternalListener = 1172;
    }
}
