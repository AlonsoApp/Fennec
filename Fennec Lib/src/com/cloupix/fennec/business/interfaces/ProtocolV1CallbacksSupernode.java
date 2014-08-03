package com.cloupix.fennec.business.interfaces;

import com.cloupix.fennec.logic.security.SecurityLevel;

import java.security.*;
import java.security.cert.Certificate;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksSupernode extends ProtocolV1Callbacks {

    KeyPair getKeyPair();

    byte[] validateSha(String sha);

    boolean registerDevice(int port, byte[] authKey);

    Certificate getSignedCert();

    PublicKey verifyCert(Certificate cert);

    void authenticateAnalytic(int port, byte[] authKey, String publicKeyHex, boolean signed);

    void negotiateSecurityLevelAnalytic(int port, SecurityLevel securityLevel);
}
