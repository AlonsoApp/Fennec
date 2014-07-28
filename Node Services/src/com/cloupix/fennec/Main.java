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
            int portInternal = Integer.parseInt(args[0]);
            DaemonTCP internalDaemonTCP = new DaemonTCP(portInternal);
            //internalDaemonTCP.run();
            new Thread(internalDaemonTCP).start();

            int portExternal = Integer.parseInt(args[1]);
            DaemonTCP externalDaemonTCP = new DaemonTCP(portExternal);
            //externalDaemonTCP.run();
            new Thread(externalDaemonTCP).start();

        }catch(Exception e){
            Log.e(Main.class.getName(), "Exception");
            e.printStackTrace();
        }
    }


    private static void loadConfig(){
        R.build(R.TYPE_SERVICES);
    }
}
