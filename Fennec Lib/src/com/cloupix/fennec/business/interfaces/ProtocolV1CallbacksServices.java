package com.cloupix.fennec.business.interfaces;

import com.cloupix.fennec.business.Profile;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksServices extends ProtocolV1Callbacks {


    void storeAuthKey(byte[] authKey);

    byte[] getAuthKey();

    KeyPair getKeyPair();

    Certificate getSignedCert();

    PublicKey verifyCert(Certificate cert);
}
