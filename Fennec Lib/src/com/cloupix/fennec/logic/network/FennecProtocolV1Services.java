package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.BlockParser;
import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.business.LineParser;
import com.cloupix.fennec.business.Status;
import com.cloupix.fennec.business.exceptions.AuthenticationException;
import com.cloupix.fennec.business.exceptions.CommunicationException;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.business.interfaces.ProtocolCallbacks;
import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksServices;
import com.cloupix.fennec.logic.security.*;
import com.cloupix.fennec.logic.security.SecurityManager;
import com.cloupix.fennec.util.R;

import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

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
            SecurityLevel securityLevel = SecurityLevel.generate(mProtocolCallbacks.getProfile());
            securityManager = SecurityManager.build(securityLevel);



            writeBytes(LineParser.encodeLine(new String[]{"SECURITY"}, SP, CLRF));
            writeBytes(LineParser.encodeLine(new String[]{"Security-Class:", securityLevel.getSecurityClass()}, SP, CLRF));
            writeBytes(LineParser.encodeLine(new String[]{"Security-Level:", securityLevel.getSecurityLevel() + ""}, SP, CLRF));

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
        if(securityManager instanceof SecurityManagerA){
            verifyA();
        }

    }

    public void verifyA() throws IOException, ProtocolException, CommunicationException, AuthenticationException {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_0"}, SP, CLRF));

        /** Esperamos respuesta del Supernode */
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);

        lineParser.validateNext("VERIFY_A_0_RESULT");
        byte[] content = getContentNoCommand();

        try {
            // Convertimos ese byte[] en un Certificate
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(content);
            Certificate cert = certFactory.generateCertificate(in);
            PublicKey snPubKey = mProtocolCallbacks.verifyCert(cert);
            securityManagerA.setPubKey(snPubKey);
        } catch (Exception e) {
            Status status = new Status(400);
            writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_1", status.getCode() + "", status.getMsg()}, SP, CLRF));
            //dos.writeBytes("VERIFY_A_1" + SP + (new Status(400)).toString(SP) + CLRF);
            throw new AuthenticationException(AuthenticationException.AUTH_PROCESS_FAILED,
                    "No se ha podido convertir el byte[] de la pubKey a PublicKey");
        }
        Status status = new Status(100);
        writeBytes(LineParser.encodeLine(new String[]{"VERIFY_A_1", status.getCode() + "", status.getMsg()}, SP, CLRF));

        /** Esperamos respuesta del Supernode */

        CipheredContent cipheredContent = getCipheredContent();


        String responseBlock = null;
        try {
            responseBlock = securityManagerA.decipherWithPublicToString(cipheredContent);
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
    public void authenticate() throws Exception {
        if(mProtocolCallbacks.getAuthKey()==null) {
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
            authenticatePure();
        }catch (AuthenticationException eAuth){
            register();
            authenticatePure();
        }

    }

    public void authenticatePure() throws Exception {
        if(securityManager instanceof SecurityManagerA)
            authenticateA();
        else if(securityManager instanceof SecurityManagerB)
            authenticateB();
    }

    public void authenticateA() throws Exception {
        SecurityManagerA securityManagerA = (SecurityManagerA) securityManager;

        /** Mandamos el commando cifrado con su priv */

        writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_A_0"}, SP, CLRF));

        KeyPair keyPair = mProtocolCallbacks.getKeyPair();

        /** Si el nivel es 0 sacamos nuestro certificado firmado y se lo mandamos cifraco con su publica */
        byte[] content;
        if(securityManager.getSecurityLevel().getSecurityLevel() == 0){
            content = mProtocolCallbacks.getSignedCert().getEncoded();
        }else{
            // Si no es 0 simplemente le mandamos nuestra pub cifraca con su pub
            content = keyPair.getPublic().getEncoded();
        }

        sendContentNoCommand(content);


        /** Esperamos respuesta del Supernode */

        //cipheredContent = getCipheredContent();
        String responseLine = br.readLine();

        LineParser lineParser = new LineParser(responseLine, SP);

        lineParser.validateNext("AUTHENTICATE_A_0_RESULT");
        // Sacamos el status code y el message
        int statusCode = lineParser.getNextInt();
        String statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 100)
            throw new CommunicationException(new Status(statusCode, statusMsg));

        securityManagerA.setPrivKey(keyPair.getPrivate());


        /** Mandamos nuestro authKey cifracon con nuestra privada y con su publica */
        String line = LineParser.encodeLine(new String[]{"AUTHENTICATE_A_1"}, SP, CLRF);

        CipheredContent cipheredContent = securityManager.cipher(line.getBytes(R.charset));
        sendCipheredContent(cipheredContent);

        byte[] authKey = mProtocolCallbacks.getAuthKey();
        cipheredContent = securityManagerA.cipherWithPrivate(authKey);
        cipheredContent = securityManager.cipher(cipheredContent.getFullContent());

        sendCipheredContent(cipheredContent);

        /** Esperamos respuesta (sin cifrar) del Supernode */
        responseLine = br.readLine();

        lineParser = new LineParser(responseLine, SP);

        lineParser.validateNext("AUTHENTICATE_A_1_RESULT");
        // Sacamos el status code y el message
        statusCode = lineParser.getNextInt();
        statusMsg = lineParser.getNext();
        //Si el stausCode no es el esperado o uno de los esperados lanzamos excepción
        if(statusCode != 200)
            throw new CommunicationException(new Status(statusCode, statusMsg));

    }

    public void authenticateB()  throws ProtocolException, IOException, AuthenticationException, CommunicationException, NoSuchAlgorithmException  {
        writeBytes(LineParser.encodeLine(new String[]{"AUTHENTICATE_B"}, SP, CLRF));

        byte[] authKey = mProtocolCallbacks.getAuthKey();

        String strAuthKeySha = SecurityManager.SHAsum(authKey);

        //String strAutKey = SecurityManager.byteArray2Hex(authKey);

        writeBytes(LineParser.encodeLine(new String[]{"SHA:", strAuthKeySha}, SP, CLRF));


        /** Iniciamos espera a ver si el Supernode nos conoce */
        String responseLine = br.readLine();
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("AUTHENTICATE_B_RESULT");
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

        mProtocolCallbacks.storeAuthKey(dhAlice.getSharedSecret());
        // Guardamos la key para cifrar y descifrar mas tarde
        securityManager.setAuthKey(dhAlice.getSharedSecret());

    }

    @Override
    public void connect(String deviceIP, int devicePORT) throws Exception {
        String line = LineParser.encodeLine(new String[]{"CONNECT", deviceIP, devicePORT+""}, SP, CLRF);
        CipheredContent cipheredLine = securityManager.cipher(line.getBytes(R.charset));

        sendCipheredContent(cipheredLine);

        /** Iniciamos espera respuesta del Node Services */
        CipheredContent cipheredContent = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredContent);

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

        writeBytes(LineParser.encodeLine(new String[]{"CONNECT_REQUEST", deviceIp, devicePort + ""}, SP, CLRF));
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

        authenticate();

        String line = LineParser.encodeLine(new String[]{"FINISH_INIT"}, SP, CLRF);
        //String line = "FINISH_INIT" + CLRF;

        CipheredContent cipheredContent = securityManager.cipher(line.getBytes(R.charset));

        sendCipheredContent(cipheredContent);
    }

    @Override
    public byte[] transmit(byte[] content) throws Exception {

        String line = LineParser.encodeLine(new String[]{"TRANSMIT"}, SP, CLRF);
        CipheredContent cipheredContent = securityManager.cipher(line.getBytes(R.charset));

        sendCipheredContent(cipheredContent);
        cipheredContent = securityManager.cipher(content);
        sendCipheredContent(cipheredContent);


        /** Recibimos la respuesta y su contenido del SUPERNODE */
        cipheredContent = getCipheredContent();
        String responseLine = securityManager.decipherToString(cipheredContent);
        LineParser lineParser = new LineParser(responseLine, SP);
        lineParser.validateNext("TRANSMIT_RESULT");
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

        System.out.println("--> " + command);
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
        CipheredContent cipheredContent = getCipheredContentNoCommand();

        String strBlock = securityManager.decipherToString(cipheredContent);
        BlockParser blockParser = new BlockParser(strBlock, CLRF, SP);

        LineParser lineParser = blockParser.getNextLineParser();
        String command = lineParser.getNext();

        System.out.println("--> " + command);
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
            writeBytes(LineParser.encodeLine(new String[]{"CONNECT_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));

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
            writeBytes(LineParser.encodeLine(new String[]{"TRANSMIT_RESULT", status.getCode() + "", status.getMsg()}, SP, CLRF));
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

            CipheredContent cipheredContent = securityManager.cipher(resultLine.getBytes(R.charset));
            sendCipheredContent(cipheredContent);

        }catch (CommunicationException eC){
            // TODO Responder con el disconect

        }
    }

    protected void transmitRequestPassive(ActiveRequestManager activeRequestManager) throws Exception {

        CipheredContent cipheredContent = getCipheredContent();
        byte[] content = securityManager.decipher(cipheredContent);

        try{

            content = activeRequestManager.transmitRequest(content);


            Status status = new Status(200);
            String resultLine = LineParser.encodeLine(new String[]{"TRANSMIT_REQUEST_RESULT", status.getCode()+"", status.getMsg()}, SP, CLRF);

            cipheredContent = securityManager.cipher(resultLine.getBytes(R.charset));
            sendCipheredContent(cipheredContent);
            cipheredContent = securityManager.cipher(content);
            sendCipheredContent(cipheredContent);

        }catch (CommunicationException eC){
            // TODO Responder con el disconect

        }
    }
}
