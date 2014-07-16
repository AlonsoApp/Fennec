package com.cloupix.fennec.logic.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class PassiveRequestManager {


    private Socket socket;
    private String CLRF = "\r\n";

    private BufferedReader br;
    private DataOutputStream dos;

    private ActiveRequestManager activeRequestManager;

    public PassiveRequestManager(Socket socket) throws IOException {
        this.socket = socket;

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    public void start() throws IOException {
        boolean terminar = false;
        while (!terminar){
            String line = br.readLine();
            if(line.equals("CONNECT")){
                //TODO Crear el otro communication con la ip que recibas

                line = br.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");

                String deviceIP = desLimpiarStr(st.nextToken().trim());
                String devicePORT = desLimpiarStr(st.nextToken().trim());


                //Creamos un RequestManager
                activeRequestManager = ActiveRequestManager.build(socket.getLocalPort());
                //TODO Cambiar statusCode por algún método que contemple mensaje
                int statusCode = activeRequestManager.connect(deviceIP, Integer.parseInt(devicePORT));


                // Respondemos
                dos.writeBytes("CONNECT.RESULT" + CLRF);
                dos.writeBytes(Integer.toString(statusCode) + CLRF);
                dos.writeBytes("END.CONNECT.RESULT" + CLRF);

            }else if (line.equals("TRANSMIT")){
                line = br.readLine();

                //TODO Cambiar response por algún objeto que conteme el statuscode, el data y el mensaje del status
                String response = activeRequestManager.transmit(line);

                // Respondemos
                dos.writeBytes("TRANSMIT.RESULT" + CLRF);
                dos.writeBytes(response + CLRF);
                dos.writeBytes("END.TRANSMIT.RESULT" + CLRF);

            }else if (line.equals("DISCONNECT")){

                int statusCode = activeRequestManager.disconnect();
                if(statusCode==200){
                    dos.writeBytes(statusCode + CLRF);
                    terminar = true;
                    br.close();
                    dos.close();
                    socket.close();
                }
            }else{
                //TODO: Comando no reconocido, a priori se devuelve un "400;Comando no rreconocido"
                dos.writeBytes(400 + ";" + CLRF);
            }
        }
    }

    private String desLimpiarStr(String str){
        String strLimpio = str.replace("%01", ";");
        strLimpio = strLimpio.replaceAll("%02", CLRF);
        if(strLimpio.equals("%03"))
            strLimpio = "";
        return strLimpio;
    }
}
