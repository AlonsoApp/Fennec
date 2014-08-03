package com.cloupix.fennec.util;

import java.security.KeyPair;
import java.security.cert.Certificate;

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

    protected String keystorePath;
    protected String rootcaPath;
    protected String databasePath;
    protected String authKeyPath;

    private KeyPair keyPair;
    private Certificate signedCertificate;

    protected int nodeType;

    public static final String charset = "utf-8";

    // TODO Almacenar esto bien
    private byte[] authKey;


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

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
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

    public byte[] getAuthKey() {
        return authKey;
    }

    public void setAuthKey(byte[] authKey) {
        this.authKey = authKey;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getRootcaPath() {
        return rootcaPath;
    }

    public void setRootcaPath(String rootcaPath) {
        this.rootcaPath = rootcaPath;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setSignedCertificate(Certificate signedCertificate) {
        this.signedCertificate = signedCertificate;
    }

    public Certificate getSignedCertificate() {
        return signedCertificate;
    }

    public String getAuthKeyPath() {
        return authKeyPath;
    }

    public void setAuthKeyPath(String authKeyPath) {
        this.authKeyPath = authKeyPath;
    }
}
