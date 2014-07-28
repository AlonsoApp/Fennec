package com.cloupix.fennec.util;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class LibraryR extends R{

    public LibraryR(){
        super();
        this.nodeType = TYPE_LIBRARY;
        
        this.portExternal = 1171;
        this.portInternalListener = 1;
        this.portExternalListener = 1179;
    }

}
