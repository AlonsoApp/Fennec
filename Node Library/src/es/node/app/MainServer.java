package es.node.app;

import com.cloupix.fennec.business.ServerSession;
import com.cloupix.fennec.util.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 04/07/14.
 *
 */
public class MainServer {

    private static final int DEVICE_DEMO_PORT = 1179;

    public static void main(String[] args){

        loadConfig();

        ServerSession serverSession = new ServerSession(new ServerSession.ServerSessionCallbacks() {
            @Override
            public boolean onConnectionRequest(String deviceIP) {
                return true;
            }

            @Override
            public byte[] onTransmit(byte[] data) {

                try {
                    return (new String(data, "utf-8") + " usa Fennec u k ase").getBytes();
                } catch (UnsupportedEncodingException e) {
                    return "Fallo al decofificar string".getBytes();
                }
            }
        });
        try {
            serverSession.start(DEVICE_DEMO_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void loadConfig(){
        R.build(R.TYPE_LIBRARY);
    }
}
