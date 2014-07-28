package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.logic.network.FennecProtocol;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class SecurityManagerA extends SecurityManager {

    private PublicKey pubKey;
    private PrivateKey privKey;

    public SecurityManagerA(SecurityLevel securityLevel){
        this.securityLevel = securityLevel;
    }


    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public void setPubKey(byte[] encodedPubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedPubKey));
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public void setPrivKey(PrivateKey privKey) {
        this.privKey = privKey;
    }

    public void setPrivKey(byte[] encodedPrivKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.privKey = KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(encodedPrivKey));
    }


    @Override
    public byte[] cipher(String msg) {
        return cipher(msg.getBytes());
    }

    @Override
    public byte[] cipher(byte[] content) {
        return content;
    }

    @Override
    public byte[] decipher(byte[] content) {
        return content;
    }

    @Override
    public String decipherToString(byte[] content) throws UnsupportedEncodingException {
        return new String(decipher(content), "utf-8");
    }

    @Override
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }


    public byte[] cipherWithPublic(byte[] content) throws Exception {
        return AsymmetricCipher.encrypt(content, pubKey, getXform());
    }

    public byte[] cipherWithPrivate(byte[] content) throws Exception {
        return AsymmetricCipher.encrypt(content, privKey, getXform());
    }

    public byte[] decipherWithPublic(byte[] content) throws Exception {
        return AsymmetricCipher.decrypt(content, pubKey, getXform());
    }

    public byte[] decipherWithPrivate(byte[] content) throws Exception {
        return AsymmetricCipher.decrypt(content, privKey, getXform());
    }

    private String getXform(){
        // SI alguna vez cambiamos de algoritmo ahi es donde se  mete el siwtch en funcion de securityLevel
        return "RSA/ECB/PKCS1Padding";
    }

    public String decipherWithPublicToString(byte[] content) throws Exception {
        return new String(decipherWithPublic(content), "utf-8");
    }
}
