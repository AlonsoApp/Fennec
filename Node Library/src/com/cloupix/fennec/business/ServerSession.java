package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksLibrary;
import com.cloupix.fennec.logic.network.DaemonTCP;
import com.cloupix.fennec.logic.network.PassiveRequestManager;

import java.net.Socket;

/**
 * Created by AlonsoUSA on 03/07/14.
 *
 */
public class ServerSession implements ProtocolV1CallbacksLibrary{

    private DaemonTCP daemonTCP;
    private ServerSessionCallbacks mCallbacks;
    private PassiveRequestManager passiveRequestManager;

    public ServerSession(ServerSessionCallbacks serverSessionCallbacks){
        this.daemonTCP = new DaemonTCP();
        this.mCallbacks = serverSessionCallbacks;
    }

    public void start(int port) throws Exception {
        Socket socket = daemonTCP.start(port);
        passiveRequestManager = new PassiveRequestManager(socket, this);
        passiveRequestManager.start();
    }


    public interface ServerSessionCallbacks {
        boolean onConnectionRequest(String deviceIP);

        byte[] onTransmit(byte[] data);
    }

    @Override
    public Status onConnectionRequest(String sourceIp) {
        return mCallbacks.onConnectionRequest(sourceIp) ? new Status(200): new Status(401);
    }

    @Override
    public byte[] onTransmitRequest(byte[] content) {
        return mCallbacks.onTransmit(content);
    }

}
