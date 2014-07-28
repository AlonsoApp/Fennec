package com.cloupix.fennec.business.interfaces;

import com.cloupix.fennec.business.Status;

/**
 * Created by AlonsoUSA on 24/07/14.
 *
 */
public interface ProtocolV1CallbacksLibrary extends ProtocolV1Callbacks {

    Status onConnectionRequest(String sourceIp);

    byte[] onTransmitRequest(byte[] content);
}
