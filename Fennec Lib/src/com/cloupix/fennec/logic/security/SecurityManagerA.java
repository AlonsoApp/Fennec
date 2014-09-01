package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.util.R;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class SecurityManagerA extends SecurityManagerB {

    private PublicKey pubKey;
    private PrivateKey privKey;

    public SecurityManagerA(SecurityLevel securityLevel){
        super(securityLevel);
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

    /*
    @Override
    public CipheredContent cipher(byte[] content) throws Exception {
        return cipherWithPublic(content);
    }

    @Override
    public byte[] decipher(CipheredContent cipheredContent) throws Exception {
        return decipherWithPrivate(cipheredContent);
    }


    @Override
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    */

    public CipheredContent cipherWithPublic(byte[] content) throws Exception {
        return new CipheredContent(RSACipher.encrypt(content, pubKey, getXform()));
    }

    public CipheredContent cipherWithPrivate(byte[] content) throws Exception {
        return new CipheredContent(RSACipher.encrypt(content, privKey, getXform()));
    }

    public byte[] decipherWithPublic(CipheredContent cipheredContent) throws Exception {
        return RSACipher.decrypt(cipheredContent.getFullContent(), pubKey, getXform());
    }

    public byte[] decipherWithPrivate(CipheredContent cipheredContent) throws Exception {
        return RSACipher.decrypt(cipheredContent.getFullContent(), privKey, getXform());
    }

    private String getXform(){
        // SI alguna vez cambiamos de algoritmo ahi es donde se  mete el siwtch en funcion de securityLevel
        return "RSA/ECB/PKCS1Padding";
    }

    public String decipherWithPublicToString(CipheredContent cipheredContent) throws Exception {
        return new String(decipherWithPublic(cipheredContent), R.charset);
    }

    public String decipherWithPrivateToString(CipheredContent cipheredContent) throws Exception {
        return new String(decipherWithPrivate(cipheredContent), R.charset);
    }
}
