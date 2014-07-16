package com.cloupix.fennec.business;

import com.cloupix.fennec.logic.network.DaemonTCP;
import com.cloupix.fennec.logic.network.PassiveRequestManager;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 03/07/14.
 *
 */
public class ServerSession {

    private DaemonTCP daemonTCP;
    private ServerSessionCallbacks mCallbacks;
    private PassiveRequestManager passiveRequestManager;

    public ServerSession(ServerSessionCallbacks serverSessionCallbacks){
        this.daemonTCP = new DaemonTCP();
        this.mCallbacks = serverSessionCallbacks;
    }

    public void start(int port) throws IOException {
        Socket socket = daemonTCP.start(port);
        passiveRequestManager = new PassiveRequestManager(socket, mCallbacks);
        passiveRequestManager.start();
    }



    public interface ServerSessionCallbacks {
        boolean onConnectionRequest(String deviceIP, int devicePort);

        String onTransmit(String data);
    }

}
