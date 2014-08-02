package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.util.R;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

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
        br = new BufferedReader(new InputStreamReader(socket.getInputStream(), R.charset));
        dis = new DataInputStream(socket.getInputStream());

        // TODO if is Node Services and devicePort is Supernode negotiateProtocolVersionSupernode
        protocol = FennecProtocol.negotiateProtocolVersion(socket, dos, br, dis, protocolCallbacks);
    }

    public void init() throws Exception {
        protocol.init();
    }

    public void verifyTarget() throws SessionException, IOException, CertificateEncodingException {
        protocol.verify();
    }

    public void negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException {
        protocol.negotiateSecurityLevel();
    }

    public void authenticate() throws Exception {

        protocol.authenticate();
    }

    public void connect(String deviceIP, int devicePORT) throws Exception {

        protocol.connect(deviceIP, devicePORT);


    }

    public byte[] transmit(byte[] content) throws Exception {

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

    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws Exception {
        protocol.connectRequest(deviceIp, devicePort, sourceIp);
    }

    public byte[] transmitRequest(byte[] content) throws Exception {
        return protocol.transmitRequest(content);
    }
}
