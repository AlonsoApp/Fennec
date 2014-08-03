package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.*;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.logic.security.SecurityManagerA;
import com.cloupix.fennec.logic.security.SecurityManagerB;
import com.cloupix.fennec.util.R;

import java.io.*;
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

    protected void sendCipheredContent(CipheredContent cipheredContent) throws IOException {
        writeBytes(LineParser.encodeLine(new String[]{"CIPHERED_MESSAGE"}, SP, CLRF));

        switch (cipheredContent.getCipherClass()){
            case CipheredContent.CLASS_A:
                sendCipheredContentA(cipheredContent);
                break;
            case CipheredContent.CLASS_B:
                sendCipheredContentB(cipheredContent);
                break;
        }

    }

    protected void sendCipheredContentA(CipheredContent cipheredContent) throws IOException {
        sendContentNoCommand(cipheredContent.getFullContent());
    }

    protected void sendCipheredContentB(CipheredContent cipheredContent) throws IOException {
        sendHeader("Content-Length:", cipheredContent.getFullContent().length + "");
        sendHeader("MsgKey-Length:", cipheredContent.getMsgKeyLenght() + "");
        sendHeader("AuthKeySha-Length:", cipheredContent.getAuthKeyShaLength() + "");
        sendContent(cipheredContent.getFullContent());

    }

    protected void sendContentNoCommand(byte[] content) throws IOException {
        sendHeader("Content-Length:", content.length + "");
        sendContent(content);
    }

    protected void sendHeader(String name, String content) throws IOException {
        writeBytes(LineParser.encodeLine(new String[]{name, content}, SP, CLRF));
    }

    protected void sendContent(byte[] content) throws IOException {
        br.readLine();
        dos.write(content, 0, content.length);
        dos.flush();

        System.out.print("Sent " + socket.getPort() + ": ");
        //printBytes(content);
        System.out.println(new String(content, R.charset));
    }

    protected CipheredContent getCipheredContent(int cipherClass) throws IOException, ProtocolException {


        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("CIPHERED_MESSAGE");


        return getCipheredContentNoCommand(cipherClass);
    }

    protected CipheredContent getCipheredContentNoCommand(int cipherClass) throws IOException, ProtocolException {

        CipheredContent cipheredContent = new CipheredContent(cipherClass);
        int length = getHeaderInt("Content-Length:");
        switch (cipherClass){
            case CipheredContent.CLASS_A:
                cipheredContent.setFullContent(getContent(length));
                break;
            case CipheredContent.CLASS_B:
                int msgKeyLength = getHeaderInt("MsgKey-Length:");
                int authKeyShaLeng = getHeaderInt("AuthKeySha-Length:");

                cipheredContent.setMsgKeyLength(msgKeyLength);
                cipheredContent.setAuthKeyShaLength(authKeyShaLeng);
                cipheredContent.setFullContent(getContent(length));
                cipheredContent.split();
                break;

        }
        return cipheredContent;



        /*
        if(securityManager instanceof SecurityManagerA){
            cipheredContent = new CipheredContent();
            cipheredContent.setFullContent(getContent(length));
        }else if(securityManager instanceof SecurityManagerB){
            cipheredContent = new CipheredContent();
            int msgKeyLength = getHeaderInt("MsgKey-Length:");
            int authKeyShaLeng = getHeaderInt("AuthKeySha-Length:");

            cipheredContent.setMsgKeyLength(msgKeyLength);
            cipheredContent.setAuthKeyShaLength(authKeyShaLeng);
            cipheredContent.setFullContent(getContent(length));
            cipheredContent.split();
        }
        return cipheredContent;
        */
    }

    protected byte[] getContentNoCommand() throws IOException, ProtocolException {

        /*
        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("Content-Length:");
        int length = lineParser.getNextInt();
        */

        return getContent(getHeaderInt("Content-Length:"));
    }

    protected String getHeader(String name) throws IOException, ProtocolException {
        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext(name);
        return lineParser.getNext();
    }

    protected int getHeaderInt(String name) throws ProtocolException, IOException {
        String line = br.readLine();
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext(name);
        return lineParser.getNextInt();
    }

    protected byte[] getContent(int length) throws IOException {
        byte[] buffer = new byte[length];
        byte b;
        Status status = new Status(100);
        writeBytes(LineParser.encodeLine(new String[]{"PROCEED", status.getCode()+"", status.getMsg()}, SP, CLRF));
        for(int i=0; i<length; i++)
        {
            b = dis.readByte();
            //buffer[i] = b;
            /** SUPERÑAPA
             * Este if comprueba si el primer byte es 10 (10 en ascii es /n)
             * Este byte se introduce aleatoriamente en las comunicaciones por sockets de Java
             * ¿Motivo? Creo que tiene que ver con la codificación del InputStreamReader
             * Como no he encontrado (por falta de tiempo) una manera de evitar que salga,
             * compruebo el primer byte de cada transmisión, si es 10 lo desprecio y resto 1 a i
             * */

            if(i==0 && b == 10)
                i=-1;
            else
                buffer[i] = b;
        }
        System.out.print("Recibed " + socket.getPort() + ": ");
        System.out.println(new String(buffer, R.charset));
        //printBytes(buffer);
        return buffer;
    }

    public static void printBytes(byte[] bytes){
        System.out.print("[");
        for(byte byt : bytes){
            System.out.print(byt + ",");
        }
        System.out.println("]");
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
