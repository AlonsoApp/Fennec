package com.cloupix.fennec.business.interfaces;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksSupernode extends ProtocolV1Callbacks {

    PublicKey getCertPublicKey() throws NoSuchAlgorithmException;

    PrivateKey getCertPrivateKey() throws NoSuchAlgorithmException;

    boolean validateSha(String sha);

}
