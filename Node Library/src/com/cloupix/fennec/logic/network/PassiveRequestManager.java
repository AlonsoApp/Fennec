package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.ServerSession;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by AlonsoUSA on 04/07/14.
 *
 */
public class PassiveRequestManager {
    private Socket socket;
    private String CLRF = "\r\n";

    private BufferedReader br;
    private DataOutputStream dos;

    private ActiveRequestManager activeRequestManager;
    private ServerSession.ServerSessionCallbacks serverSessionCallbacks;

    public PassiveRequestManager(Socket socket, ServerSession.ServerSessionCallbacks serverSessionCallbacks) throws IOException {
        this.socket = socket;
        this.serverSessionCallbacks = serverSessionCallbacks;

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

                // Lanzamos evento para notificar intento de conexión
                if(serverSessionCallbacks.onConnectionRequest(deviceIP, Integer.parseInt(devicePORT))){
                    // Aceptada
                    dos.writeBytes("CONNECT.RESULT" + CLRF);
                    dos.writeBytes(Integer.toString(R.CODE_OK) + CLRF);
                    dos.writeBytes("END.CONNECT.RESULT" + CLRF);
                }else{
                    // Rechazada
                    dos.writeBytes("CONNECT.RESULT" + CLRF);
                    dos.writeBytes(Integer.toString(R.CODE_CONNECTION_REFUSED) + CLRF);
                    dos.writeBytes("END.CONNECT.RESULT" + CLRF);
                }

            }else if (line.equals("TRANSMIT")){
                line = br.readLine();

                //Lanzamos evento que envie loq ue se ha transmitido
                String response = serverSessionCallbacks.onTransmit(line);

                // Respondemos
                dos.writeBytes("TRANSMIT.RESULT" + CLRF);
                dos.writeBytes(response + CLRF);
                dos.writeBytes("END.TRANSMIT.RESULT" + CLRF);

            }else if (line.equals("DISCONNECT")){

                dos.writeBytes(Integer.toString(R.CODE_OK) + CLRF);
                terminar = true;
                br.close();
                dos.close();
                socket.close();
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
