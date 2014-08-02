package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksServices;
import com.cloupix.fennec.logic.Logic;
import com.cloupix.fennec.logic.network.PassiveRequestManager;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements Runnable, ProtocolV1CallbacksServices{

    private Logic logic;

    //TODO Esto no dbería estar aqui
    private byte[] authkey;

    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    public Session(Socket sourceSocket) throws IOException {
        this.logic = new Logic();

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
            //TODO Catchear bien todas las excepciones
            e.printStackTrace();
        }finally {
            //TODO Cerrar todo por si acaso
            //requestManager.finish();
        }
    }

    public boolean isAlive() {
        //TODO Currarse más este método
        return !sourceSocket.isClosed();
    }

    @Override
    public void storeAuthKey(byte[] authKey) {

        //TODO Guardar la AuthKey
        this.authkey = authKey;
    }

    @Override
    public byte[] getAuthKey() {
        //TODO Dar una AuthKey
        return authkey;
    }

    @Override
    public String getAuthKeySha() {
        //TODO De momento no se usa, si se considera necesario implementarlo, si no borrarlo
        return null;
    }

    @Override
    public Profile getProfile() {
        // TODO devolver el profile del device
        return new Profile();
    }

    @Override
    public KeyPair getKeyPair() {
        try {
            return logic.getKeyPair();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Certificate getSignedCert() {
        try {
            return logic.getCert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PublicKey verifyCert(Certificate cert) {
        return logic.verifyCert(cert);
    }
}
