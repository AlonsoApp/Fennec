package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.*;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.exceptions.SessionException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksSupernode;
import com.cloupix.fennec.logic.security.*;
import com.cloupix.fennec.logic.security.SecurityManager;
import com.cloupix.fennec.util.R;

import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;

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
    public void verify() throws SessionException, IOException, CertificateEncodingException {
        if(securityManager.getSecurityLevel().getSecurityClass().equals("A")){
            verifyA();
        }
    }

    public void verifyA() throws IOException, SessionException, CertificateEncodingException {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        byte[] content = mProtocolCallbacks.getSignedCert().getEncoded();

        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_0_RESULT"}, SP, CLRF));

        sendContentNoCommand(content);

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


        KeyPair keyPair = mProtocolCallbacks.getKeyPair();
        if(keyPair == null)
            throw new SessionException("Error al obtener la clave publica y la privada");

        securityManagerA.setPrivKey(keyPair.getPrivate());


        String line = "VERIFY_A_1_RESULT" + SP + (new Status(200)).toString(SP);
        CipheredContent cipheredContent;
        try {
            cipheredContent = securityManagerA.cipherWithPrivate(line.getBytes(R.charset));
            sendCipheredContent(cipheredContent);
        } catch (Exception e) {
            line = "VERIFY_A_1_RESULT" + SP + (new Status(500)).toString(SP);
            writeBytes(line);
            throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED, "Error al cifrar con privKey");
            // TODO Sigue estando el fallo de que si sale bien se envia cifrada la respuesta y si sale mal no. Lo va a descifrar y dará casque
        }


    }

    @Override
    public void authenticate() throws Exception {
        if(securityManager.getSecurityLevel().getSecurityClass().equals("A") &&
                securityManager.getSecurityLevel().getSecurityLevel()<2)
            authenticateA();
        else
            authenticateB();
    }

    public void authenticateA() throws Exception {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        byte[] content = getContentNoCommand();

        /** Si el nivel es 0 verificamos el cert signed que nos ha mandando */
        if(securityManager.getSecurityLevel().getSecurityLevel() == 0){
            // Convertimos ese byte[] en un Certificate
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(content);
            Certificate cert = certFactory.generateCertificate(in);
            PublicKey nodePubKey = mProtocolCallbacks.verifyCert(cert);

            if(nodePubKey==null){
                //Verificacion erronea
                Status status = new Status(401);
                String line = LineParser.encodeLine(new String[]{"AUTHENTICATE_A_0_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF);
                writeBytes(line);
                throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED, "La verificación del certificado del nodo ha dado como resultado negativo");
            }

            // Guardamos el pubKey para cifrar mas adelante
            securityManagerA.setPubKey(nodePubKey);
        }else{
            // Si no, simplemente guardamos su publica
            try{
                securityManagerA.setPubKey(content);
            }catch (Exception e){
                Status status = new Status(401);
                String line = LineParser.encodeLine(new String[]{"AUTHENTICATE_A_0_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF);
                writeBytes(line);
                throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED, "Fallo al decodificar clave publica del nodo");
            }
        }



        /** Respondemos con un continue */
        Status status = new Status(100);
        String line = LineParser.encodeLine(new String[]{"AUTHENTICATE_A_0_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF);
        writeBytes(line);


        /** Recibimos el auth cifrado con la privada dle nodo y cifrado con nuestra publica */
        CipheredContent cipheredContent = getCipheredContent(CipheredContent.CLASS_A);
        line = securityManagerA.decipherWithPrivateToString(cipheredContent);
        LineParser lineParser = new LineParser(line, SP);
        lineParser.validateNext("AUTHENTICATE_A_1");
        cipheredContent = getCipheredContent(CipheredContent.CLASS_A);
        content = securityManagerA.decipherWithPrivate(cipheredContent);


        byte[] authKey = securityManagerA.decipherWithPublic(new CipheredContent(content));

        String sha = SecurityManager.SHAsum(authKey);
        authKey = mProtocolCallbacks.validateSha(sha);

        if(authKey==null){
            status = new Status(403);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_A_1_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
        }else{
            securityManager.setAuthKey(authKey);

            status = new Status(200);
            line = LineParser.encodeLine(new String[]{"AUTHENTICATE_A_1_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF);
            writeBytes(line);

            /** Analytics */
            if(securityManager.getSecurityLevel().getSecurityLevel() == 0){
                mProtocolCallbacks.authenticateAnalytic(socket.getLocalPort(), authKey, com.cloupix.fennec.logic.security.SecurityManager.byteArray2Hex(securityManagerA.getPubKey().getEncoded()), true);
            }else{
                mProtocolCallbacks.authenticateAnalytic(socket.getLocalPort(), authKey, com.cloupix.fennec.logic.security.SecurityManager.byteArray2Hex(securityManagerA.getPubKey().getEncoded()), false);
            }
        }
    }

    public void authenticateB() throws IOException, ProtocolException, AuthenticationException, CommunicationException, NoSuchAlgorithmException {
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("SHA:");
        String sha = lineParser.getNext();

        byte[] authKey = mProtocolCallbacks.validateSha(sha);

        if(authKey!=null){
            Status status = new Status(200);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_B_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
            securityManager.setAuthKey(authKey);
        }else{
            Status status = new Status(403);
            writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_B_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
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

        boolean hasConflict = mProtocolCallbacks.registerDevice(socket.getLocalPort(), dhBob.getSharedSecret());

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

        /** Analytics */
        mProtocolCallbacks.negotiateSecurityLevelAnalytic(socket.getLocalPort(), securityLevel);


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
        cipheredContent = getCipheredContent(CipheredContent.CLASS_B);
        String responseLine = securityManager.decipherToString(cipheredContent);
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_REQUEST_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        cipheredContent = getCipheredContent(CipheredContent.CLASS_B);
        content = securityManager.decipher(cipheredContent);


        return content;
    }

    @Override
    public void connectRequest(String deviceIp, int devicePort, String sourceIp) throws Exception {
        String line = LineParser.encodeLine(new String[]{"CONNECT_REQUEST", deviceIp, devicePort+""}, SP, CLRF);
        //String line = "CONNECT_REQUEST" + SP + deviceIp + SP + devicePort + CLRF;
        line = line + LineParser.encodeLine(new String[]{"Source-Host:", sourceIp}, SP, CLRF);
        //line = line + "Source-Host:" + SP + sourceIp + CLRF;
        CipheredContent cipheredContent = new CipheredContent(CipheredContent.CLASS_B);
        try {
            cipheredContent = securityManager.cipher(line.getBytes(R.charset));
        } catch (Exception e) {
            //TODO Mandar un finish y lanzar una excepción
            e.printStackTrace();
        }

        sendCipheredContent(cipheredContent);

        /** Iniciamos espera respuesta del Node Services */

        cipheredContent = getCipheredContent(CipheredContent.CLASS_B);
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

        }else if(command.equals("AUTHENTICATE_A_0")){

            authenticate();

        }else if(command.equals("AUTHENTICATE_B")){

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

        CipheredContent cipheredContent = getCipheredContentNoCommand(CipheredContent.CLASS_B);

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
        CipheredContent cipheredContent = getCipheredContent(CipheredContent.CLASS_B);
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
