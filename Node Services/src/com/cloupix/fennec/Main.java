package com.cloupix.fennec;

import com.cloupix.fennec.logic.ConfigHandler;
import com.cloupix.fennec.logic.Logic;
import com.cloupix.fennec.logic.network.DaemonTCP;
import com.cloupix.fennec.util.Log;
import com.cloupix.fennec.util.R;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

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

        R.getInstance().setAuthKey(new Logic().getAuthKey());

        loadConfigXml();
    }

    private static void loadConfigXml(){
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            ConfigHandler handler = new ConfigHandler();
            saxParser.parse(new File(R.getInstance().getConfigPath()), handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
