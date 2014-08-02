package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.Logic;
import com.cloupix.fennec.logic.network.PassiveRequestManager;
import com.cloupix.fennec.logic.security.*;

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

    private HashMap<String, byte[]> map;

    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    public Session(Socket sourceSocket) throws IOException {
        this.logic = new Logic();


        this.sourceSocket = sourceSocket;
        this.passiveRequestManager = new PassiveRequestManager(sourceSocket, this);
    }

    @Override
    public void run() {

        // TODO Esto simula la bd borrarlo
        map = new HashMap<String, byte[]>();

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
        // TODO Validar
        // TODO devolver el authKey propio de ese sha
        return map.get(sha);
    }

    @Override
    public boolean registerDevice(byte[] authKey) {
        // TODO Guardar en BBDD
        try {
            String sha = com.cloupix.fennec.logic.security.SecurityManager.SHAsum(authKey);
            map.put(sha, authKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
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
}
