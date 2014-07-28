package com.cloupix.fennec.util;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public abstract class R {

    public static final int TYPE_LIBRARY = 1;
    public static final int TYPE_SERVICES = 2;
    public static final int TYPE_SUPERNODE = 3;

    private static R instance;


    protected String localHostIp = "127.0.0.1";
    protected String supernodeIp;
    protected int portInternalListener;
    protected int portExternal;
    protected int portExternalListener;

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    protected int nodeType;

    // TODO Almacenar esto bien
    private String authKey;


    /*
    *public static int NODE_SERVICES_PORT = 1171;//Antes era 1172

    public static final int PORT_INTERNAL = 1171;
    public static final int PORT_EXTERNAL = 1172;//Cambiar a 1170
    *public static final int PORT_SUPERNODE = 1170;

    public static final int PORT_INTERNAL_FAKE = 1174;
    public static final int PORT_EXTERNAL_FAKE = 1173;
    */


    public static final int CODE_OK = 200;
    public static final int CODE_CONNECTION_REFUSED = 401;

    public static R getInstance(){
        return instance;
    }

    public static void build(int type){
        switch (type){
            case TYPE_LIBRARY: instance = new LibraryR(); break;
            case TYPE_SERVICES: instance = new ServicesR(); break;
            case TYPE_SUPERNODE: instance = new SupernodeR(); break;
        }
    }

    public String getLocalHostIp() {
        return localHostIp;
    }

    public void setLocalHostIp(String localHostIp) {
        this.localHostIp = localHostIp;
    }

    public int getPortInternalListener() {
        return portInternalListener;
    }

    public void setPortInternalListener(int portInternalListener) {
        this.portInternalListener = portInternalListener;
    }

    public int getPortExternal() {
        return portExternal;
    }

    public void setPortExternal(int portExternal) {
        this.portExternal = portExternal;
    }

    public int getPortExternalListener() {
        return portExternalListener;
    }

    public void setPortExternalListener(int portExternalListener) {
        this.portExternalListener = portExternalListener;
    }

    public String getSupernodeIp() {
        return supernodeIp;
    }

    public void setSupernodeIp(String supernodeIp) {
        this.supernodeIp = supernodeIp;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
