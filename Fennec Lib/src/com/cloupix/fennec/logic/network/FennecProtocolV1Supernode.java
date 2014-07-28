package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.BlockParser;
import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.security.DHKeyAgreementBob;
import com.cloupix.fennec.logic.security.SecurityLevel;
import com.cloupix.fennec.logic.security.SecurityManagerA;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by AlonsoUSA on 19/07/14.
 *
 */
public class FennecProtocolV1Supernode extends FennecProtocolV1 {

    private ProtocolV1CallbacksSupernode mProtocolCallbacks;

    public FennecProtocolV1Supernode(Socket socket, DataOutputStream dos, BufferedReader br, DataInputStream dis, ProtocolCallbacks protocolCallbacks) {
        super(socket, dos, br, dis, protocolCallbacks);
        this.mProtocolCallbacks = (ProtocolV1CallbacksSupernode) protocolCallbacks;
    }

    @Override
    public void verify() throws SessionException, IOException {
        if(securityManager.getSecurityLevel().getSecurityClass().equals("A")){
            verifyA();
        }
    }

    public void verifyA() throws IOException, SessionException {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_0_RESULT"}, SP, CLRF));
        //dos.writeBytes("VERIFY_A_0_RESULT" + CLRF);

        PublicKey pubKey = null;
        PrivateKey privKey = null;
        try {
            pubKey = mProtocolCallbacks.getCertPublicKey();
            privKey = mProtocolCallbacks.getCertPrivateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new SessionException("Error al obtener la clave publica y la privada");
        }

        sendContentNoCommand(pubKey.getEncoded());

        securityManagerA.setPubKey(pubKey);
        securityManagerA.setPrivKey(privKey);

        /** Esperamos respuesta del Supernode */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);

        lineParser.validateNext("VERIFY_A_1");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 100)
            throw new CommunicationException(new Status(statusCode, statusMsg));


        String resultBlock = "VERIFY_A_1_RESULT" + SP + (new Status(200)).toString(SP);
        byte[] cipheredResultBlock = new byte[0];
        try {
            cipheredResultBlock = securityManagerA.cipherWithPrivate(resultBlock.getBytes());
        } catch (Exception e) {
            resultBlock = "VERIFY_A_1_RESULT" + SP + (new Status(500)).toString(SP);
            // TODO Esto esta mal porque el Nodo espera un mensaje cifrado, como el casque se ha producido al cifrar
            // no se puede cifrar la respuesta, va a intentar descifrar un mensaje no decifrado y va a considerarlo casque
            sendCipheredContent(resultBlock.getBytes());
        }

        sendCipheredContent(cipheredResultBlock);

    }

    @Override
    public void authenticate() throws IOException, ProtocolException, AuthenticationException, CommunicationException {
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("SHA:");
        String sha = lineParser.getNext();

        boolean validateOk = mProtocolCallbacks.validateSha(sha);

        if(validateOk){
            Status status = new Status(200);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("AUTHENTICATE_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);
        }else{
            Status status = new Status(403);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("AUTHENTICATE_RESULT" + SP + (new Status(403)).toString(SP) + CLRF);
        }
    }

    @Override
    public void register() throws Exception {

        byte[] alicePubKey = getContentNoCommand();


        byte[] bobPubKey = null;
        DHKeyAgreementBob dhBob = new DHKeyAgreementBob();
        try{
             bobPubKey = dhBob.generateBobPubKeyEnc(alicePubKey);
        }catch (Exception e){
            Status status = new Status(412);
            writeBytes(LineParser.encodeLine(new String[]{"REGISTER_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("REGISTER_RESULT" + SP + (new Status(412)).toString(SP) + CLRF);
            throw e;
        }

        // Generamos la clave final
        dhBob.generateSharedSecret();

        //TODO Registrar ese authkey junto con su SHA y datos

        Status status = new Status(200);
        writeBytes(LineParser.encodeLine(new String[]{"REGISTER_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("REGISTER_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);
        sendContentNoCommand(bobPubKey);

    }

    @Override
    public SecurityLevel negotiateSecurityLevel() throws ProtocolException, IOException, CommunicationException {
        SecurityLevel securityLevel = null;
        String line = br.readLine();

        LineParser lineParser = new LineParser(line, SP);

        lineParser.validateNext("Security-Class:");
        String secClass = lineParser.getNext();

        line = br.readLine();
        lineParser = new LineParser(line, SP);
        lineParser.validateNext("Security-Level:");

        int secLevel = lineParser.getNextInt();
        try{
            // Sacamos nosotros nuestra seguridad
            securityLevel = new SecurityLevel(secClass, secLevel);
            securityManager = com.cloupix.fennec.logic.security.SecurityManager.build(securityLevel);
        }catch (ProtocolException eP){
            // TODO return 406

            return null;
        }
        Status status = new Status(200);
        writeBytes(LineParser.encodeLine(new String[]{"SECURITY_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF));
        //dos.writeBytes("SECURITY_RESULT" + SP + (new Status(200)).toString(SP) + CLRF);
        return securityLevel;
    }

    @Override
    public void connect(String deviceIP, int devicePORT) throws IOException, SessionException {

    }

    @Override
    public byte[] transmit(byte[] content) throws IOException, ProtocolException, CommunicationException {
        return new byte[0];
    }

    @Override
    public byte[] transmitRequest(byte[] content) throws IOException, ProtocolException, CommunicationException {
        String line = LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST"}, SP, CLRF);
        //String line = "TRANSMIT_REQUEST" + CLRF;
        sendCipheredContent(line.getBytes());
        byte[] sContent = securityManager.cipher(content);
        sendCipheredContent(sContent);


        /** Recibimos la respuesta y su contenido del NODE SERVICES */
        byte[] response = getCipheredContent();
        String responseLine = securityManager.decipherToString(response);
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_REQUEST_RESULT");
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
    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws IOException, ProtocolException, CommunicationException {
        String line = LineParser.encodeLine(new String[]{"CONNECT_REQUEST", deviceIp, devicePort+""}, SP, CLRF);
        //String line = "CONNECT_REQUEST" + SP + deviceIp + SP + devicePort + CLRF;
        line = line + LineParser.encodeLine(new String[]{"Source-Host:", sourceIp}, SP, CLRF);
        //line = line + "Source-Host:" + SP + sourceIp + CLRF;
        byte[] cipheredLine = securityManager.cipher(line);

        sendCipheredContent(cipheredLine);

        /** Iniciamos espera respuesta del Node Services */

        byte[] cipheredResponseLine = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredResponseLine);

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
        writeBytes(LineParser.encodeLine(new String[]{"INIT"}, SP, CLRF));
        //dos.writeBytes("INIT" + CLRF);
        boolean finish = false;
        while (!finish){
            String line = br.readLine();
            finish = processCommandLine(line, null);
        }
    }

    @Override
    public boolean processCommandLine(String line, ActiveRequestManager activeRequestManager) throws Exception {
        // Si nos mandan un null es que algo ha ido mal y se ha cerrado el socket
        if(line == null)
            return true;
        LineParser lineParser = new LineParser(line, SP);
        String command = lineParser.getNext();

        if(command.equals("VERIFY_A_0")){

            verify();

        }else if(command.equals("SECURITY")){

            negotiateSecurityLevel();

        }else if(command.equals("AUTHENTICATE")){

            authenticate();

        }else if(command.equals("REGISTER")){

            register();

        }else if (command.equals("DISCONNECT")){
            disconnectPassive(activeRequestManager);
            return true;

        }else if (command.equals("CIPHERED_MESSAGE")){
            return processCipherCommandLine(activeRequestManager);
        }else{
            throw new ProtocolException(ProtocolException.COMMAND_NOT_RECOGNIZED, "Command Not Recogniced. Recibed \""+ line + "\"");
        }
        return false;
    }

    public boolean processCipherCommandLine(ActiveRequestManager activeRequestManager) throws Exception {

        byte[] content = getContentNoCommand();

        String strBlock = securityManager.decipherToString(content);
        BlockParser blockParser = new BlockParser(strBlock, CLRF, SP);

        LineParser lineParser = blockParser.getNextLineParser();
        String command = lineParser.getNext();

        if (command.equals("CONNECT")) {

            String deviceIp = lineParser.getNext();
            int devicePort = lineParser.getNextInt();

            connectPassive(deviceIp, devicePort, activeRequestManager);

        }else if (command.equals("FINISH_INIT")){
            return true;

        }else if (command.equals("TRANSMIT")){
            transmitPassive(activeRequestManager);

        }else{
            throw new ProtocolException(ProtocolException.COMMAND_NOT_RECOGNIZED, "Command Not Recogniced. Recibed \"CIPHERED_MESSAGE\"");
        }
        return false;
    }


    protected void connectPassive(String deviceIp, int devicePort, ActiveRequestManager activeRequestManager) throws IOException, SessionException {
        Status status = new Status(200);
        try{
            activeRequestManager.start(R.getInstance().getLocalHostIp(), R.getInstance().getPortExternal(), mProtocolCallbacks);

            //TODO Meter los try catch para reaccionar en funcion de las excepciones
            // Metodo bloqueante que se encarga de que el Node services haga to_do lo que tenga que hacer
            try {
                activeRequestManager.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            activeRequestManager.connectRequest(deviceIp, devicePort, socket.getInetAddress().toString());

            status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"CONNECT_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);
            //String resultLine = "CONNECT_RESULT" + SP + (new Status(200)).toString(SP) + CLRF;
            byte[] cipheredLine = securityManager.cipher(resultLine);
            sendCipheredContent(cipheredLine);

        }catch (CommunicationException eC){
            status = eC.getStaus();
            // TODO Responder con el disconect

        }
    }

    protected void transmitPassive(ActiveRequestManager activeRequestManager) throws Exception {
        byte[] content = getCipheredContent();
        content = securityManager.decipher(content);


        try{

            content = activeRequestManager.transmitRequest(content);
            // TODO tratar la excepcion y mandar una respuesta acorde

            Status status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"TRANSMIT_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);
            //String resultLine = "TRANSMIT_RESULT" + SP + (new Status(200)).toString(SP) + CLRF;

            byte[] cipheredLine = securityManager.cipher(resultLine);
            sendCipheredContent(cipheredLine);
            content = securityManager.cipher(content);
            sendCipheredContent(content);

        }catch (CommunicationException eC){

            // TODO Responder con el disconect

        }
    }
}
