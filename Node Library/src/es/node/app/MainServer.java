package es.node.app;

import com.cloupix.fennec.business.ServerSession;

import java.io.IOException;

/**
 * Created by AlonsoUSA on 04/07/14.
 *
 */
public class MainServer {

    private static final int DEVICE_DEMO_PORT = 1179;

    public static void main(String[] args){

        ServerSession serverSession = new ServerSession(new ServerSession.ServerSessionCallbacks() {
            @Override
            public boolean onConnectionRequest(String deviceIP, int devicePort) {
                return true;
            }

            @Override
            public String onTransmit(String data) {

                return data + " q ase";
            }
        });
        try {
            serverSession.start(DEVICE_DEMO_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
