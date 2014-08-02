package com.cloupix.fennec.business.interfaces;

import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksSupernode extends ProtocolV1Callbacks {

    KeyPair getKeyPair();

    byte[] validateSha(String sha);

    boolean registerDevice(byte[] authKey);

    Certificate getSignedCert();

    PublicKey verifyCert(Certificate cert);
}
