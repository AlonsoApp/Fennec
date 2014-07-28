package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 16/07/14.
 *
 */
public abstract class FennecProtocolV1 extends FennecProtocol {

    public static final String versionName = "FENNEC/1.0";

    public FennecProtocolV1(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) {
        super(socket, dos, br, dis, mCallbacks);
    }

    public static FennecProtocol build(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) {
        switch (R.getInstance().getNodeType()){
            case R.TYPE_LIBRARY: return new FennecProtocolV1Library(socket, dos, br, dis, mCallbacks);
            case R.TYPE_SERVICES: return new FennecProtocolV1Services(socket, dos, br, dis, mCallbacks);
            case R.TYPE_SUPERNODE: return new FennecProtocolV1Supernode(socket, dos, br, dis, mCallbacks);
        }
        return null;
    }

    // Active

    @Override
    public Status disconnect() throws IOException, ProtocolException {
        writeBytes(LineParser.encodeLine(new String[]{"DISCONNECT"}, SP, CLRF));
        //dos.writeBytes("DISCONNECT" + FennecProtocol.CLRF);


        /** Iniciamos espera respuesta del SUPERNODE */
        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("DISCONNECT_RESPONSE");
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        return new Status(statusCode, statusMsg);
    }

    protected void sendCipheredContent(byte[] content) throws IOException {
        writeBytes(LineParser.encodeLine(new String[]{"CIPHERED_MESSAGE"}, SP, CLRF));
        //dos.writeBytes("CIPHERED_MESSAGE" + CLRF);
        sendContentNoCommand(content);
    }

    protected void sendContentNoCommand(byte[] content) throws IOException {
        writeBytes(LineParser.encodeLine(new String[]{"Content-Length:", content.length+""}, SP, CLRF));
        //dos.writeBytes("Content-Length:" + SP + content.length + CLRF);

        br.readLine();
        dos.write(content, 0, content.length);
        dos.flush();
    }

    protected byte[] getCipheredContent() throws IOException, ProtocolException {

        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("CIPHERED_MESSAGE");
        return getContentNoCommand();
    }

    protected byte[] getContentNoCommand() throws IOException, ProtocolException {

        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("Content-Length:");
        int length = lineParser.getNextInt();

        byte[] buffer = new byte[length];
        byte b;
        Status status = new Status(100);
        writeBytes(LineParser.encodeLine(new String[]{"PROCEED", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("PROCEED" + SP + (new Status(100)).toString(SP) + CLRF);
        for(int i=0; i<length; i++)
        {
            b = dis.readByte();
            buffer[i] = b;
        }
        return buffer;
    }


    protected void disconnectPassive(ActiveRequestManager activeRequestManager) throws Exception {

        Status status = activeRequestManager.disconnect();
        // Ahora mismo no hacemos ni caso al status que nos devuelve el otro
        activeRequestManager.close();
        status = new Status(200);
        writeBytes(LineParser.encodeLine(new String[]{"DISCONNECT_RESPONSE", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("DISCONNECT_RESPONSE" + SP + (new Status(200)).toString(SP) + CLRF);
    }
}
