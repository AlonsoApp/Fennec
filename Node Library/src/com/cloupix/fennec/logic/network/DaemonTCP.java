package com.cloupix.fennec.logic.network;

import com.cloupix.fennec.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class DaemonTCP {

    private ServerSocket serverSocket;

    public Socket start(int serverTCPPort) throws IOException {
        serverSocket = new ServerSocket(serverTCPPort);

        Log.i("Node Services (" + InetAddress.getLocalHost().getHostAddress() + ":" + serverTCPPort + ") waiting...");


        Socket socket = serverSocket.accept();
        Log.i("New conexion (" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ")");
        return socket;
    }

    public void finish(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("finalizar(): " + e);
            e.printStackTrace();
        }
    }
}
