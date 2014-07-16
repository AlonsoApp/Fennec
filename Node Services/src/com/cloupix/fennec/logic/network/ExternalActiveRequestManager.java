package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.util.AppConfig;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 02/07/14.
 *
 */
public class ExternalActiveRequestManager extends ActiveRequestManager {

    public ExternalActiveRequestManager() {
    }

    @Override
    public int connect(String deviceIP, int devicePORT) throws IOException {
        socket = new Socket(AppConfig.SUPERNODE_IP, R.PORT_SUPERNODE);

        dos = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return super.connect(deviceIP, devicePORT);
    }
}


