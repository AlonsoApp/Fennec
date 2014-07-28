package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.BlockParser;
import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksServices;
import com.cloupix.fennec.logic.authentication.AuthenticationManager;
import com.cloupix.fennec.logic.security.*;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class FennecProtocolV1Services extends FennecProtocolV1 {

    private ProtocolV1CallbacksServices mProtocolCallbacks;

    public FennecProtocolV1Services(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks protocolCallbacks) {
        super(socket, dos, br, dis, protocolCallbacks);
        this.mProtocolCallbacks = (ProtocolV1CallbacksServices)protocolCallbacks;
    }

    @Override
    public SecurityLevel negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException {

        if(socket.getPort() == R.getInstance().getPortExternal() || socket.getLocalPort() == R.getInstance().getPortExternalListener()){
            // Va/viene al/del supernode
            // Sacamos nosotros nuestra seguridad
            SecurityLevel securityLevel = SecurityLevel.generate();
            securityManager = com.cloupix.fennec.logic.security.SecurityManager.build(securityLevel);



            writeBytes(LineParser.encodeLine(new String[]{"SECURITY"}, SP, CLRF));
            //dos.writeBytes("SECURITY" + CLRF);
            writeBytes(LineParser.encodeLine(new String[]{"Security-Class:", securityLevel.getSecurityClass()}, SP, CLRF));
            //dos.writeBytes("Security-Class:" + SP + securityLevel.getSecurityClass() + CLRF);
            writeBytes(LineParser.encodeLine(new String[]{"Security-Level:", securityLevel.getSecurityLevel()+""}, SP, CLRF));
            //dos.writeBytes("Security-Level:" + SP + securityLevel.getSecurityLevel() + CLRF);

            /** Esperamos respuesta del Supernode */
            String responseLine = br.readLine();

            LineParser lineParser = new LineParser(responseLine, SP);

            lineParser.validateNext("SECURITY_RESULT");

            // Sacamos el status code y el message
            int statusCode = lineParser.getNextInt();
            String statusMsg = lineParser.getNext();
            //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
            if(statusCode != 200)
                throw new CommunicationException(new Status(statusCode, statusMsg));

            return securityLevel;

        }else {
            // Va a la Library
            return null;
        }
    }



    @Override
    public void verify() throws ProtocolException, IOException, CommunicationException, AuthenticationException {
        if(securityManager.getSecurityLevel().getSecurityClass().equals("A")){
            verifyA();
        }
    }

    public void verifyA() throws IOException, ProtocolException, CommunicationException, AuthenticationException {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_0"}, SP, CLRF));
        //dos.writeBytes("VERIFY_A_0" + CLRF);

        /** Esperamos respuesta del Supernode */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);

        lineParser.validateNext("VERIFY_A_0_RESULT");
        byte[] snPubKey = getContentNoCommand();


        try {
            securityManagerA.setPubKey(snPubKey);
        } catch (Exception e) {
            Status status = new Status(400);
            writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_1", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("VERIFY_A_1" + SP + (new Status(400)).toString(SP) + CLRF);
            throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED,
                    "No se ha podido convertir el byte[] de la pubKey a PublicKey");
        }
        Status status = new Status(100);
        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_1", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("VERIFY_A_1" + SP +(new Status(100)).toString(SP) + CLRF);

        /** Esperamos respuesta del Supernode */

        byte[] cipheredResponseBlock = getCipheredContent();


        String responseBlock = null;
        try {
            responseBlock = securityManagerA.decipherWithPublicToString(cipheredResponseBlock);
        } catch (Exception e) {
            throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED,
                    "El descifrado del certificado ha fallado, la validación del supernodo es fallida.");
        }

        BlockParser blockParser = new BlockParser(responseBlock, CLRF, SP);
        lineParser = blockParser.getNextLineParser();
        lineParser.validateNext("VERIFY_A_1_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));


        //TODO Implementar esto
    }

    @Override
    public void authenticate() throws IOException, ProtocolException, AuthenticationException, CommunicationException {

        writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE"}, SP, CLRF));
        //dos.writeBytes("AUTHENTICATE" + CLRF);
        // TODO Sacar el SHA de esto
        String authKey = AuthenticationManager.getAuthKey();

        writeBytes(LineParser.encodeLine(new String[]{"SHA:", authKey}, SP, CLRF));
        //dos.writeBytes("SHA:" + SP + authKey + CLRF);


        /** Iniciamos espera a ver si el Supernode nos conoce */
        String responseLine = br.readLine();
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("AUTHENTICATE_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        if(statusCode == 403)
            throw new AuthenticationException(AuthenticationException.FORBIDDEN, statusMsg);
        else if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));
    }

    @Override
    public void register() throws Exception {
        DHKeyAgreementAlice dhAlice = new DHKeyAgreementAlice(DHKeyAgreementAlice.NO_GENERATE_DH_PARAMS);


        writeBytes(LineParser.encodeLine(new String[]{"REGISTER"}, SP, CLRF));
        //dos.writeBytes("REGISTER" + CLRF);
        sendContentNoCommand(dhAlice.generateAlicePubKeyEnc());

        /** Iniciamos espera a ver si el Supernode nos conoce */
        String responseLine = br.readLine();
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("REGISTER_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        byte[] bobPubKey = getContentNoCommand();

        dhAlice.generateSharedSecret(bobPubKey);

        AuthenticationManager.setAuthKey(dhAlice.getSharedSecretString());
    }

    @Override
    public void connect(String deviceIP, int devicePORT) throws IOException, SessionException {
        String line = LineParser.encodeLine(new String[]{"CONNECT", deviceIP, devicePORT+""}, SP, CLRF);
        //String line = "CONNECT" + SP + deviceIP + SP + devicePORT + CLRF;
        byte[] cipheredLine = securityManager.cipher(line);

        sendCipheredContent(cipheredLine);

        /** Iniciamos espera respuesta del Node Services */
        byte[] cipheredResponseLine = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredResponseLine);

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
    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws IOException, ProtocolException, CommunicationException {

        writeBytes(LineParser.encodeLine(new String[]{"CONNECT_REQUEST", deviceIp, devicePort+""}, SP, CLRF));
        //dos.writeBytes("CONNECT_REQUEST" + SP + deviceIp + SP + devicePort + CLRF);
        writeBytes(LineParser.encodeLine(new String[]{"Source-Host:", sourceIp}, SP, CLRF));
        //dos.writeBytes("Source-Host:" + SP + sourceIp + CLRF);

        /** Iniciamos espera respuesta del Library */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("CONNECT_REQUEST_RESULT");

        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));
    }

    @Override
    public void init() throws Exception {
        if(socket.getLocalPort() != R.getInstance().getPortExternalListener())
            return;
        // Viene del supernodo
        negotiateSecurityLevel();
        verify();

        // TODO Este es el codigo authenticate del activeRequets manager
        if(AuthenticationManager.getAuthKey()==null) {
            try{
                register();
            }catch (CommunicationException eC){
                if(eC.getStaus().getCode() == 409) {
                    // Ha habido un conflicto de sha, repetimos una vez mas el proceso
                    register();
                }
            }
        }
        try{
            authenticate();
        }catch (AuthenticationException eAuth){
            register();
            authenticate();
        }
        //TODO-END------------------------------------------------------------

        String line = LineParser.encodeLine(new String[]{"FINISH_INIT"}, SP, CLRF);
        //String line = "FINISH_INIT" + CLRF;
        sendCipheredContent(line.getBytes());
    }

    @Override
    public byte[] transmit(byte[] content) throws IOException, ProtocolException, CommunicationException {

        String line = LineParser.encodeLine(new String[]{"TRANSMIT"}, SP, CLRF);
        //String line = "TRANSMIT" + CLRF;
        sendCipheredContent(line.getBytes());
        byte[] sContent = securityManager.cipher(content);
        sendCipheredContent(sContent);


        /** Recibimos la respuesta y su contenido del SUPERNODE */
        byte[] response = getCipheredContent();
        String responseLine = securityManager.decipherToString(response);
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        sContent = getCipheredContent();
        content = securityManager.decipher(sContent);


        return content;
    }

    @Override
    public byte[] transmitRequest(byte[] content) throws IOException, ProtocolException, CommunicationException {
        writeBytes(LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST"}, SP, CLRF));
        //dos.writeBytes("TRANSMIT_REQUEST" + CLRF);
        sendContentNoCommand(content);


        /** Recibimos la respuesta y su contenido la LIBRARY */
        String responseLine = br.readLine();
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_REQUEST_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        return getContentNoCommand();
    }

    @Override
    public boolean processCommandLine(String line, ActiveRequestManager activeRequestManager) throws Exception {
        // Si nos mandan un null es que algo ha ido mal y se ha cerrado el socket
        if(line == null)
            return true;
        LineParser lineParser = new LineParser(line, SP);
        String command = lineParser.getNext();

        if (command.equals("CONNECT")) {

            String deviceIp = lineParser.getNext();
            int devicePort = lineParser.getNextInt();

            connectPassive(deviceIp, devicePort, activeRequestManager);

        }else if (command.equals("INIT")){
            init();

        }else if (command.equals("TRANSMIT")){
            transmitPassive(activeRequestManager);

        }else if (command.equals("DISCONNECT")){
            disconnectPassive(activeRequestManager);
            return true;

        }else{
            processCipherCommandLine(line, activeRequestManager);
        }
        return false;
    }


    public void processCipherCommandLine(String line, ActiveRequestManager activeRequestManager) throws Exception {
        byte[] content = getContentNoCommand();

        String strBlock = securityManager.decipherToString(content);
        BlockParser blockParser = new BlockParser(strBlock, CLRF, SP);

        LineParser lineParser = blockParser.getNextLineParser();
        String command = lineParser.getNext();

        if (command.equals("CONNECT_REQUEST")) {

            String deviceIp = lineParser.getNext();
            int devicePort = lineParser.getNextInt();

            lineParser = blockParser.getNextLineParser();
            lineParser.validateNext("Source-Host:");
            String sourceIp = lineParser.getNext();

            connectRequestPassive(deviceIp, devicePort, sourceIp, activeRequestManager);

        }else if (command.equals("TRANSMIT_REQUEST")){
            transmitRequestPassive(activeRequestManager);

        }else{
            throw new ProtocolException(ProtocolException.COMMAND_NOT_RECOGNIZED, "Command Not Recogniced. Recibed \""+ line + "\"");
        }
    }


    protected void connectPassive(String deviceIp, int devicePort, ActiveRequestManager activeRequestManager) throws Exception {
        Status status = new Status(200);
        try{
            activeRequestManager.start(R.getInstance().getSupernodeIp(), R.getInstance().getPortExternal(), mProtocolCallbacks);

            //TODO Meter los try catch para reaccionar en funcion de las excepciones
            activeRequestManager.negotiateSecurityLevel();
            activeRequestManager.verifyTarget();
            activeRequestManager.authenticate();
            activeRequestManager.connect(deviceIp, devicePort);

            status = new Status(200);
            writeBytes(LineParser.encodeLine(new String[]{"CONNECT_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("CONNECT_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);

        }catch (CommunicationException eC){
            status = eC.getStaus();
            // TODO Responder con el disconect

        }
    }

    protected void transmitPassive(ActiveRequestManager activeRequestManager) throws Exception {
        byte[] content = getContentNoCommand();
        try{

            content = activeRequestManager.transmit(content);
            // TODO tratar la excepcion y mandar una respuesta acorde
            Status status = new Status(200);
            writeBytes(LineParser.encodeLine(new String[]{"TRANSMIT_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("TRANSMIT_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);
            sendContentNoCommand(content);

        }catch (CommunicationException eC){

            // TODO Responder con el disconect

        }
    }

    protected void connectRequestPassive(String deviceIp, int devicePort, String sourceIp, ActiveRequestManager activeRequestManager) throws Exception {


        try{
            activeRequestManager.start(R.getInstance().getLocalHostIp(), devicePort, mProtocolCallbacks);

            activeRequestManager.connectRequest(deviceIp, devicePort, sourceIp);

            Status status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"CONNECT_REQUEST_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);
            //String resultLine = "CONNECT_REQUEST_RESULT" + SP + (new Status(200)).toString(SP) + CLRF;

            byte[] cipheredLine = securityManager.cipher(resultLine);
            sendCipheredContent(cipheredLine);

        }catch (CommunicationException eC){
            // TODO Responder con el disconect

        }
    }

    protected void transmitRequestPassive(ActiveRequestManager activeRequestManager) throws Exception {

        byte[] content = getCipheredContent();
        content = securityManager.decipher(content);

        try{

            content = activeRequestManager.transmitRequest(content);


            Status status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);
            //String resultLine = "TRANSMIT_REQUEST_RESULT" + SP + (new Status(200)).toString(SP) + CLRF;

            byte[] cipheredLine = securityManager.cipher(resultLine);
            sendCipheredContent(cipheredLine);
            content = securityManager.cipher(content);
            sendCipheredContent(content);

        }catch (CommunicationException eC){
            // TODO Responder con el disconect

        }
    }
}
