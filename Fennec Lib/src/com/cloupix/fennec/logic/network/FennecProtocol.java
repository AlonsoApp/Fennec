package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.logic.ExceptionManager;
import com.cloupix.fennec.logic.security.SecurityLevel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 16/07/14.
 *
 */
public abstract class FennecProtocol {

    public static final String SP = " ";
    public static final String CLRF = "\r\n";
    public static final String PRELUDE = "_/_";


    protected Socket socket;
    protected DataOutputStream dos;
    protected BufferedReader br;
    protected DataInputStream dis;

    protected com.cloupix.fennec.logic.security.SecurityManager securityManager;


    protected FennecProtocol(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks){
        this.socket = socket;
        this.dos = dos;
        this.br = br;
        this.dis = dis;
    }

    public static FennecProtocol build(String version, Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) throws ProtocolException {

        if(version.equals(FennecProtocolV1.versionName)){
            return FennecProtocolV1.build(socket, dos, br, dis, mCallbacks);
        }

        throw new ProtocolException(ProtocolException.UNKNOWN_VERSION, "The version "+ version + "is not recognized");
    }

    public static String maxVersionString(){
        return FennecProtocolV1.versionName;
    }

    // Active

    public static FennecProtocol negotiateProtocolVersion(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) throws IOException, SessionException {

        writeBytes(dos, LineParser.encodeLine(new String[]{"HELLO", FennecProtocol.maxVersionString()}, SP, CLRF));
        //dos.writeBytes("HELLO" + SP + FennecProtocol.maxVersionString() + CLRF);

        /** Iniciamos espera respuesta del Node Services */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);

        String command = lineParser.getNext();

        if(command.equals("HELLO_RESULT")){
            //Sacamos la versión
            String version = lineParser.getNext();

            // Sacamos el status code y el message
            int statusCode = lineParser.getNextInt();
            String statusMsg = lineParser.getNext();
            //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción. Si no creamos el protocol de esa versión
            if(statusCode != 200)
                ExceptionManager.throwByStatusCode(statusCode, statusMsg);
            else
                return FennecProtocol.build(version, socket, dos, br, dis, mCallbacks);
        }
        return null;
    }

    public abstract SecurityLevel negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException;

    public abstract void verify() throws SessionException, IOException;

    public abstract void authenticate() throws IOException, ProtocolException, AuthenticationException, CommunicationException;

    public abstract void register() throws Exception;

    public abstract void connect(String deviceIP, int devicePORT) throws IOException, SessionException;

    public abstract byte[] transmit(byte[] content) throws IOException, ProtocolException, CommunicationException;

    public abstract Status disconnect() throws IOException, ProtocolException;

    public abstract void connectRequest(String deviceIp, int devicePort, String sourceIp) throws IOException, ProtocolException, CommunicationException;

    // Passive

    public abstract void init() throws Exception;

    public static FennecProtocol negotiateProtocolVersionPassive(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks mCallbacks) throws IOException, SessionException {

        FennecProtocol protocol = null;
        String line = br.readLine();

        LineParser lineParser = new LineParser(line, SP);

        String command = lineParser.getNext();

        if(command.equals("HELLO")){
            // Sacamos la versión propuesta por el cliente
            String version = lineParser.getNext();

            Status status;
            try{
                protocol = FennecProtocol.build(version,socket, dos, br, dis, mCallbacks);
                status = new Status(200);
            }catch (ProtocolException eP){
                status = new Status(505);
            }catch (Exception ex){
                status = new Status(500);
            }

            writeBytes(dos, LineParser.encodeLine(new String[]{"HELLO_RESULT", version, status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("HELLO_RESULT" + SP + version + FennecProtocol.SP + status.toString(SP) + CLRF);
            return protocol;
        }else{
            throw new ProtocolException(ProtocolException.BAD_IMPLEMENTED, "Command HELLO expected. Received \"" +
                    command + "\"");
        }
    }

    public abstract boolean processCommandLine(String line, ActiveRequestManager activeRequestManager) throws Exception;

    public abstract byte[] transmitRequest(byte[] content) throws IOException, ProtocolException, CommunicationException;





    protected static void writeBytes(DataOutputStream dos, String str) throws IOException {
        dos.writeBytes(str);
        dos.flush();
    }

    protected void writeBytes(String str) throws IOException {
        dos.writeBytes(str);
        dos.flush();
    }
}
