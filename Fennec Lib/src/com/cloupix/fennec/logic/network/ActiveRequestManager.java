package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.logic.authentication.AuthenticationManager;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 04/07/14.
 *
 */
public class ActiveRequestManager {

    private FennecProtocol protocol;

    private Socket socket;
    private DataOutputStream dos;
    private BufferedReader br;
    private DataInputStream dis;


    public ActiveRequestManager() {

    }

    public void start(String deviceIP, int devicePORT, ProtocolCallbacks protocolCallbacks) throws IOException, SessionException {
        socket = new Socket(deviceIP, devicePORT);

        dos = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dis = new DataInputStream(socket.getInputStream());

        // TODO if is Node Services and devicePort is Supernode negotiateProtocolVersionSupernode
        protocol = FennecProtocol.negotiateProtocolVersion(socket, dos, br, dis, protocolCallbacks);
    }

    public void init() throws Exception {
        protocol.init();
    }

    public void verifyTarget() throws SessionException, IOException {
        protocol.verify();
    }

    public void negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException {
        protocol.negotiateSecurityLevel();
    }

    public void authenticate() throws Exception {
        if(AuthenticationManager.getAuthKey()==null) {
            try{
                protocol.register();
            }catch (CommunicationException eC){
                if(eC.getStaus().getCode() == 409) {
                    // Ha habido un conflicto de sha, repetimos una vez mas el proceso
                    protocol.register();
                }
            }
        }

        try{
            protocol.authenticate();
        }catch (AuthenticationException eAuth){
            protocol.register();
            protocol.authenticate();
        }
    }

    public void connect(String deviceIP, int devicePORT) throws IOException, SessionException {

        protocol.connect(deviceIP, devicePORT);


    }

    public byte[] transmit(byte[] content) throws IOException, CommunicationException, ProtocolException {

        return protocol.transmit(content);
    }

    public Status disconnect() throws IOException, ProtocolException {
        return protocol.disconnect();
    }

    public void close() throws IOException {
        br.close();
        dos.close();
        dis.close();
        socket.close();
    }

    //TODO Implementar bien este método
    public void sendError(){

    }

    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws ProtocolException, IOException, CommunicationException {
        protocol.connectRequest(deviceIp, devicePort, sourceIp);
    }

    public byte[] transmitRequest(byte[] content) throws ProtocolException, IOException, CommunicationException {
        return protocol.transmitRequest(content);
    }
}
