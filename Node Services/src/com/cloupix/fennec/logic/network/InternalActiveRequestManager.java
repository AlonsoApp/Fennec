package com.cloupix.fennec.logic.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 03/07/14.
 *
 */
public class InternalActiveRequestManager extends ActiveRequestManager {

    public InternalActiveRequestManager() {
    }

    @Override
    public int connect(String deviceIP, int devicePORT) throws IOException {
        socket = new Socket(deviceIP, devicePORT);

        dos = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        return super.connect(deviceIP, devicePORT);
    }
}
