package com.cloupix.fennec.business;

import com.cloupix.fennec.logic.network.ActiveRequestManager;

import java.io.IOException;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session {

    public enum ContentType{
        TEXT_PLAIN, TEXT_JSON
    }

    private ActiveRequestManager activeRequestManager;

    public void connect(String deviceIP, int devicePORT) throws IOException {
        this.activeRequestManager = new ActiveRequestManager();
        activeRequestManager.connect(deviceIP, devicePORT);
    }

    public String transmit(String msg) throws IOException {
        // TODO Hacer comprobaciones de estado y cambiar el string por un byte[]
        return activeRequestManager.transmit(msg);
    }

    public void disconnect() throws IOException {
        activeRequestManager.disconnect();
    }
}
