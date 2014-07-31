package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.network.PassiveRequestManager;
import com.cloupix.fennec.logic.security.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements Runnable, ProtocolV1CallbacksSupernode{

    // TODO Esto no debería ir aqui, tiene que ir en una base de datos
    private KeyPair keyPair;

    private String clientAuthKeySha;
    private byte[] clientAuthKey;

    private HashMap<String, byte[]> map;

    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    public Session(Socket sourceSocket) throws IOException {
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
}
