package com.cloupix.fennec.business;

import com.cloupix.fennec.business.interfaces.ProtocolV1CallbacksServices;
import com.cloupix.fennec.logic.network.PassiveRequestManager;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Session implements Runnable, ProtocolV1CallbacksServices{

    private Socket sourceSocket;
    private PassiveRequestManager passiveRequestManager = null;

    public Session(Socket sourceSocket) throws IOException {
        this.sourceSocket = sourceSocket;
        this.passiveRequestManager = new PassiveRequestManager(sourceSocket, this);
    }

    @Override
    public void run() {

        //TODO Con un return aqui es sufuciente para terminar el thread? o hay que hacer Thread.stop()
        if(!isAlive())
            return;
        try{
            passiveRequestManager.start();
        }catch (Exception e){
            //TODO Catchear bien todas las excepciones
            e.printStackTrace();
        }finally {
            //TODO Cerrar todo por si acaso
            //requestManager.finish();
        }
    }

    public boolean isAlive() {
        //TODO Currarse más este método
        return !sourceSocket.isClosed();
    }
}
