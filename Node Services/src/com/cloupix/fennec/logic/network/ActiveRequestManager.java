package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 03/07/14.
 *
 */
public abstract class ActiveRequestManager {

    protected Socket socket;
    protected DataOutputStream dos;
    protected BufferedReader br;
    protected String CLRF = "\r\n";


    protected ActiveRequestManager(){

    }

    public int connect(String deviceIP, int devicePORT) throws IOException{
        dos.writeBytes("CONNECT" + CLRF);
        String line = deviceIP + ";" + devicePORT + CLRF;
        dos.writeBytes(line);

        /** Iniciamos espera respuesta del SUPERNODE */
        int statusCode = 600;
        String responseLine = br.readLine();
        if(responseLine.equals("CONNECT.RESULT")){
            responseLine = br.readLine();
            statusCode = Integer.parseInt(responseLine.substring(0, 3));
        }
        br.readLine();


        return statusCode;
    }

    public String transmit(String msg) throws IOException{

        dos.writeBytes("TRANSMIT" + CLRF);
        String line = msg + CLRF;
        dos.writeBytes(line);

        /** Iniciamos espera respuesta del SUPERNODE */
        String responseLine = br.readLine();
        if(responseLine.equals("TRANSMIT.RESULT")){
            responseLine = br.readLine();
        }
        br.readLine();

        return responseLine;
    }

    public int disconnect() throws IOException{
        dos.writeBytes("DISCONNECT" + CLRF);


        /** Iniciamos espera respuesta del SUPERNODE */

        int statusCode = Integer.parseInt(br.readLine());
        if(statusCode == 200){
            dos.close();
            br.close();
            socket.close();
        }

        return statusCode;
    }

    public static ActiveRequestManager build(int port){

        /**
         * Si viene de internal hay que crear una external
         * Si viene de external hay que crear una inetrnal
         */

        switch (port){
            case R.PORT_INTERNAL: return new ExternalActiveRequestManager();
            case R.PORT_EXTERNAL: return new InternalActiveRequestManager();
            case R.PORT_INTERNAL_FAKE: return new ExternalActiveRequestManager();
            case R.PORT_EXTERNAL_FAKE: return new InternalActiveRequestManager();
            //TODO Ojo con retornar null aqui
            default: return null;
        }
    }
}
