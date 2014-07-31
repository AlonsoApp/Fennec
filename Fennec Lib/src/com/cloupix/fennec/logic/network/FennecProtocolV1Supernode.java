package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.BlockParser;
import com.cloupix.fennec.business.CipheredContent;
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
        CipheredContent cipheredContent = new CipheredContent();
        try {
            cipheredContent = securityManagerA.cipher(resultBlock.getBytes(R.charset));
        } catch (Exception e) {
            resultBlock = "VERIFY_A_1_RESULT" + SP + (new Status(500)).toString(SP);
            // TODO Esto esta mal porque el Nodo espera un mensaje cifrado, como el casque se ha producido al cifrar
            // no se puede cifrar la respuesta, va a intentar descifrar un mensaje no decifrado y va a considerarlo casque
            sendContent(resultBlock.getBytes(R.charset));
        }

        sendCipheredContent(cipheredContent);

    }

    @Override
    public void authenticate() throws IOException, ProtocolException, AuthenticationException, CommunicationException, NoSuchAlgorithmException {
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("SHA:");
        String sha = lineParser.getNext();

        byte[] authKey = mProtocolCallbacks.validateSha(sha);

        if(authKey!=null){
            Status status = new Status(200);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
            securityManager.setAuthKey(authKey);
        }else{
            Status status = new Status(403);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
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
            writeBytes(LineParser.encodeLine(new String[]{"REGISTER_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
            throw e;
        }

        // Generamos la clave final
        dhBob.generateSharedSecret();

        boolean hasConflict = mProtocolCallbacks.registerDevice(dhBob.getSharedSecret());

        Status status = new Status(200);
        // Si al guardarlo hemos tenido conflicto de SHA le mandamos un 409 para que repita el registro
        if(hasConflict)
            status = new Status(409);

        writeBytes(LineParser.encodeLine(new String[]{"REGISTER_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
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
        writeBytes(LineParser.encodeLine(new String[]{"SECURITY_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
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
    public byte[] transmitRequest(byte[] content) throws Exception {
        String line = LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST"}, SP, CLRF);
        //String line = "TRANSMIT_REQUEST" + CLRF;
        CipheredContent cipheredContent = securityManager.cipher(line.getBytes(R.charset));
        sendCipheredContent(cipheredContent);
        cipheredContent = securityManager.cipher(content);
        sendCipheredContent(cipheredContent);


        /** Recibimos la respuesta y su contenido del NODE SERVICES */
        cipheredContent = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredContent);
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_REQUEST_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        cipheredContent = getCipheredContent();
        content = securityManager.decipher(cipheredContent);


        return content;
    }

    @Override
    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws Exception {
        String line = LineParser.encodeLine(new String[]{"CONNECT_REQUEST", deviceIp, devicePort+""}, SP, CLRF);
        //String line = "CONNECT_REQUEST" + SP + deviceIp + SP + devicePort + CLRF;
        line = line + LineParser.encodeLine(new String[]{"Source-Host:", sourceIp}, SP, CLRF);
        //line = line + "Source-Host:" + SP + sourceIp + CLRF;
        CipheredContent cipheredContent = new CipheredContent();
        try {
            cipheredContent = securityManager.cipher(line.getBytes(R.charset));
        } catch (Exception e) {
            //TODO Mandar un finish y lanzar una excepción
            e.printStackTrace();
        }

        sendCipheredContent(cipheredContent);

        /** Iniciamos espera respuesta del Node Services */

        cipheredContent = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredContent);

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

        System.out.println("--> " + command);
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

        CipheredContent cipheredContent = getCipheredContentNoCommand();

        String strBlock = securityManager.decipherToString(cipheredContent);
        BlockParser blockParser = new BlockParser(strBlock, CLRF, SP);

        LineParser lineParser = blockParser.getNextLineParser();
        String command = lineParser.getNext();

        System.out.println("--> " + command);
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
            CipheredContent cipheredContent = securityManager.cipher(resultLine.getBytes(R.charset));
            sendCipheredContent(cipheredContent);

        }catch (CommunicationException eC){
            status = eC.getStaus();
            // TODO Responder con el disconect

        } catch (Exception e) {
            // TODO Responder con el disconect y alnzar excepcion.
            e.printStackTrace();
        }
    }

    protected void transmitPassive(ActiveRequestManager activeRequestManager) throws Exception {
        CipheredContent cipheredContent = getCipheredContent();
        byte[] content = securityManager.decipher(cipheredContent);


        try{

            content = activeRequestManager.transmitRequest(content);
            // TODO tratar la excepcion y mandar una respuesta acorde

            Status status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"TRANSMIT_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);

            cipheredContent = securityManager.cipher(resultLine.getBytes(R.charset));
            sendCipheredContent(cipheredContent);
            cipheredContent = securityManager.cipher(content);
            sendCipheredContent(cipheredContent);

        }catch (CommunicationException eC){

            // TODO Responder con el disconect

        }
    }
}
