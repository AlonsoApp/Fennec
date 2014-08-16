package com.cloupix.fennec.business;

import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksLibrary;
import com.cloupix.fennec.logic.network.ActiveRequestManager;
import com.cloupix.fennec.util.R;

import java.io.IOException;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements ProtocolV1CallbacksLibrary{

    private ActiveRequestManager activeRequestManager;

    public void connect(String deviceIP, int devicePORT) throws IOException, SessionException {
        this.activeRequestManager = new ActiveRequestManager();
        this.activeRequestManager.start(R.getInstance().getLocalHostIp(), R.getInstance().getPortExternal(), this);
        try {
            activeRequestManager.connect(deviceIP, devicePORT);
        } catch (ProtocolException eP){
            activeRequestManager.sendError(/** Pasarle un error para que lo procese y lo comunique a la otra parte */);
        } catch (SessionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] transmit(byte[] content) throws Exception {
        // TODO Hacer comprobaciones de estado y cambiar el string por un byte[]
        return activeRequestManager.transmit(content);
    }

    public void disconnect() throws IOException, ProtocolException {
        activeRequestManager.disconnect();
        activeRequestManager.close();
    }

    @Override
    public Status onConnectionRequest(String sourceIp) {
        return null;
    }

    @Override
    public byte[] onTransmitRequest(byte[] content) {
        return null;
    }
}
