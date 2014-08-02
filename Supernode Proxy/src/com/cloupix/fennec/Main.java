package com.cloupix.fennec;

import com.cloupix.fennec.logic.Logic;
import com.cloupix.fennec.logic.network.DaemonTCP;
import com.cloupix.fennec.util.Log;
import com.cloupix.fennec.util.R;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Main {

    public static void main(String[] args){

        try {
            loadConfig();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        try{
            int port = Integer.parseInt(args[0]);
            DaemonTCP daemonTCP = new DaemonTCP(port);
            //internalDaemonTCP.run();
            new Thread(daemonTCP).start();

        }catch(Exception e){
            Log.e(Main.class.getName(), "Exception");
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws Exception {
        R.build(R.TYPE_SUPERNODE);
        // Cargamos nuestras claves y nuestro certificado
        Logic logic = new Logic();
        KeyPair keyPair = logic.getSupernodeKeyPair();
        if(keyPair==null)
            throw new Exception("No ha sido posible obtener las claves del certificado de este supernodo");
        Certificate certSigned = logic.getSupernodeCert();
        if(certSigned == null)
            throw new Exception("Es necesario un certificado firmado por la CA Cloupix para poner en funcionamiento el supernodo");

        R.getInstance().setKeyPair(keyPair);
        R.getInstance().setSignedCertificate(certSigned);


    }
}
