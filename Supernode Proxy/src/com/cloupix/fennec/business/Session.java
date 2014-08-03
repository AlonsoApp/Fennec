package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.Logic;
import com.cloupix.fennec.logic.network.PassiveRequestManager;
import com.cloupix.fennec.logic.security.*;
import com.sun.deploy.security.AuthKey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements Runnable, ProtocolV1CallbacksSupernode{

    private Logic logic;

    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    //Analytics
    private AnalyticSession analyticSession;

    public Session(Socket sourceSocket) throws IOException {
        this.logic = new Logic();
        this.analyticSession = new AnalyticSession();
        this.analyticSession.addConnection(sourceSocket.getLocalPort(),
                new AnalyticConnection(sourceSocket.getInetAddress().getHostAddress(),
                        AnalyticConnection.TYPE_ACTIVE,
                        new AnalyticDevice()));

        this.sourceSocket = sourceSocket;
        this.passiveRequestManager = new PassiveRequestManager(sourceSocket, this);
    }

    @Override
    public void run() {

        //TODO Con un return aqui es sufuciente para terminar el thread? o hay que hacer Thread.stop()
        if(!isAlive())
            return;

        try{
            passiveRequestManager.start();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //TODO Cerrar todo por si acaso
            //requestManager.finish();
        }
    }

    public boolean isAlive() {
        return !sourceSocket.isClosed();
    }

    @Override
    public KeyPair getKeyPair() {
        try {
            return logic.getSupernodeKeyPair();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] validateSha(String sha) {
        try {
            return logic.validateSha(sha);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        /*
        if(device == null)
            return null;

        analyticSession.getConnection(port).setDevice(device);
        return device.getAnalyticAuthentication().getAuthKey();
        */
    }

    @Override
    public boolean registerDevice(int port, byte[] authKey) {
        return logic.registerDevice(authKey);
        /*
        AnalyticDevice device = logic.registerDevice(authKey);
        if(device == null)
            return true;

        analyticSession.getConnection(port).setDevice(device);
        return false;
        */
    }

    @Override
    public Certificate getSignedCert() {
        try {
            return logic.getSupernodeCert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PublicKey verifyCert(Certificate cert) {
        return logic.verifyNodeCert(cert);
    }





    @Override
    public void authenticateAnalytic(int port, byte[] authKey, String publicKeyHex, boolean signed) {
        /*
        if(publicKeyHex!=null)
            analyticSession.getConnection(port).setDeviceCertificate(new AnalyticDeviceCertificate(publicKeyHex));
        */

    }

    @Override
    public void negotiateSecurityLevelAnalytic(int port, SecurityLevel securityLevel) {

    }
}
