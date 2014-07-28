package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.network.PassiveRequestManager;
import com.cloupix.fennec.logic.security.AsymmetricCipher;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements Runnable, ProtocolV1CallbacksSupernode{

    // TODO Esto no debería ir aqui, tiene que ir en una base de datos
    private KeyPair keyPair;


    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    public Session(Socket sourceSocket) throws IOException {
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
        //TODO Currarse más este método
        return !sourceSocket.isClosed();
    }

    @Override
    public PublicKey getCertPublicKey() throws NoSuchAlgorithmException {
        // TODO Sacar esto de la base de datos
        if(keyPair==null)
            keyPair = AsymmetricCipher.generateKeyPair("RSA/ECB/PKCS1Padding", 512);
        return keyPair.getPublic();
    }

    @Override
    public PrivateKey getCertPrivateKey() throws NoSuchAlgorithmException {
        // TODO Sacar esto de la base de datos
        if(keyPair==null)
            keyPair = AsymmetricCipher.generateKeyPair("RSA/ECB/PKCS1Padding", 512);
        return keyPair.getPrivate();
    }

    @Override
    public boolean validateSha(String sha) {
        //TODO Validar
        return true;
    }
}
