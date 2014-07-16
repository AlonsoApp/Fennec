package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 03/07/14.
 *
 */
public class ActiveRequestManager {

    protected Socket socket;
    protected DataOutputStream dos;
    protected BufferedReader br;
    protected String CLRF = "\r\n";


    protected ActiveRequestManager(){

    }

    public int connect(String deviceIP, int devicePORT) throws IOException{
        socket = new Socket(deviceIP, R.PORT_EXTERNAL);

        dos = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


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
}