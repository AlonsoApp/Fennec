package com.cloupix.fennec.logic.network;



import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;

import java.io.*;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 04/07/14.
 *
 */
public class PassiveRequestManager {
    private FennecProtocol protocol;

    private Socket socket;

    private BufferedReader br;
    private DataOutputStream dos;
    private DataInputStream dis;

    private ProtocolCallbacks mProtocolCallbacks;

    private ActiveRequestManager activeRequestManager;

    public PassiveRequestManager(Socket socket, ProtocolCallbacks protocolCallbacks) throws IOException {
        this.socket = socket;

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());

        mProtocolCallbacks = protocolCallbacks;

        activeRequestManager = new ActiveRequestManager();
    }

    public void start() throws Exception {

        /** LIBRARY */

        protocol = FennecProtocol.negotiateProtocolVersionPassive(socket, dos, br, dis, mProtocolCallbacks);


        boolean finish = false;
        while (!finish){
            String line = br.readLine();
            finish = protocol.processCommandLine(line, activeRequestManager);
        }
        close();
    }

    private void close() throws IOException {
        br.close();
        dos.close();
        dis.close();
        socket.close();
    }
}
