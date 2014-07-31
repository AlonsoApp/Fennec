package com.cloupix.fennec.business.interfaces;

import com.cloupix.fennec.business.Profile;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksServices extends ProtocolV1Callbacks {


    void storeAuthKey(byte[] authKey);

    byte[] getAuthKey();

    String getAuthKeySha();

    Profile getProfile();
}
