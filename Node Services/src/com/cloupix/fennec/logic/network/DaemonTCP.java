package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.business.SessionManager;
import com.cloupix.fennec.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class DaemonTCP implements Runnable{

    private ServerSocket serverSocket;
    private int serverTCPPort;

    public DaemonTCP(int serverTCPPort){
        this.serverTCPPort = serverTCPPort;
    }

    public void start(){
        try{
            serverSocket = new ServerSocket(serverTCPPort);

            Log.i("Node Services (" + InetAddress.getLocalHost().getHostAddress() + ":" + serverTCPPort + ") waiting...");

            //TODO Cambiar este while true
            while (true)
            {
                Socket socket = serverSocket.accept();
                Log.i("New conexion (" + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort() + ")");
                //TODO Preguntar si esto esta bien
                synchronized (SessionManager.getInstance()) {
                    SessionManager.getInstance().addSession(socket);
                }
            }
        }catch(Exception e){
            System.err.println("DaemonTCP.start(): " + e);
            e.printStackTrace();
        }
    }

    public void finish(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("finalizar(): " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        start();
    }
}
