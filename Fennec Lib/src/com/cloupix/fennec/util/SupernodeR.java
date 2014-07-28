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
    }
}
