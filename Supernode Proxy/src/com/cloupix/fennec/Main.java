package com.cloupix.fennec;

import com.cloupix.fennec.logic.network.DaemonTCP;
import com.cloupix.fennec.util.Log;
import com.cloupix.fennec.util.R;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Main {

    public static void main(String[] args){

        loadConfig();

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

    private static void loadConfig(){
        R.build(R.TYPE_SUPERNODE);
    }
}
