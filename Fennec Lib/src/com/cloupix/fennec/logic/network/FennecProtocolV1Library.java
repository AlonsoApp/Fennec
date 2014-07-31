package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksLibrary;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.logic.security.SecurityLevel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class FennecProtocolV1Library extends FennecProtocolV1 {

    private ProtocolV1CallbacksLibrary mCallbacks;


    public FennecProtocolV1Library(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) {
        super(socket, dos, br, dis, mCallbacks);
        this.mCallbacks = (ProtocolV1CallbacksLibrary) mCallbacks;
    }

    @Override
    public void verify() throws ProtocolException, IOException, CommunicationException {}

    @Override
    public void authenticate() throws IOException, ProtocolException, AuthenticationException, CommunicationException {}

    @Override
    public void register() throws Exception {}

    @Override
    public SecurityLevel negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException {
        return null;
    }


    @Override
    public void connect(String deviceIP, int devicePORT) throws IOException, SessionException {
        writeBytes(LineParser.encodeLine(new String[]{"CONNECT", deviceIP, devicePORT+""}, SP, CLRF));

        /** Iniciamos espera respuesta del Node Services */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("CONNECT_RESULT");

        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));
    }

    @Override
    public byte[] transmit(byte[] content) throws IOException, ProtocolException, CommunicationException {
        writeBytes(LineParser.encodeLine(new String[]{"TRANSMIT"}, SP, CLRF));
        //dos.writeBytes("TRANSMIT" + CLRF);
        sendContentNoCommand(content);


        /** Iniciamos espera respuesta del SUPERNODE */
        String responseLine = br.readLine();
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));
        return getContentNoCommand();
    }

    @Override
    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws IOException, ProtocolException, CommunicationException {

    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public boolean processCommandLine(String line, ActiveRequestManager activeRequestManager) throws Exception {
        LineParser lineParser = new LineParser(line, SP);
        String command = lineParser.getNext();

        if (command.equals("CONNECT_REQUEST")) {

            String deviceIp = lineParser.getNext();
            int devicePort = lineParser.getNextInt();

            connectRequestPassive(deviceIp, devicePort);

        }else if (command.equals("DISCONNECT")){
            disconnectPassive(activeRequestManager);
            return true;

        }else if (command.equals("TRANSMIT_REQUEST")){

            transmitRequestPassive();

        }else{
            throw new ProtocolException(ProtocolException.COMMAND_NOT_RECOGNIZED, "Command Not Recogniced. Recibed \""+ line + "\"");
        }
        return false;

    }

    @Override
    public byte[] transmitRequest(byte[] content) throws IOException, ProtocolException, CommunicationException {
        return new byte[0];
    }


    public byte[] transmitRequestPassive() throws IOException, ProtocolException, CommunicationException {
        byte[] content = getContentNoCommand();
        content = mCallbacks.onTransmitRequest(content);

        Status status = new Status(200);
        writeBytes(LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("TRANSMIT_REQUEST_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);
        sendContentNoCommand(content);

        return new byte[0];
    }

    protected void connectRequestPassive(String deviceIp, int devicePort) throws Exception {
        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("Source-Host:");
        String sourceIp = lineParser.getNext();
        // Preguntamos
        Status status = mCallbacks.onConnectionRequest(sourceIp);
        writeBytes(LineParser.encodeLine(new String[]{"CONNECT_REQUEST_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("CONNECT_REQUEST_RESULT" + SP + status.toString(SP) + CLRF);
    }

    @Override
    protected void disconnectPassive(ActiveRequestManager activeRequestManager) throws Exception {

        Status status = new Status(200);
        writeBytes(LineParser.encodeLine(new String[]{"DISCONNECT_RESPONSE", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("DISCONNECT_RESPONSE" + SP + (new Status(200)).toString(SP) + CLRF);
    }
}
