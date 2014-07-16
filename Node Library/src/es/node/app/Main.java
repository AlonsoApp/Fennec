package es.node.app;

import com.cloupix.fennec.business.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Main {

    private static final String DEVICE_DEMO_IP = "127.0.0.1";
    private static final int DEVICE_DEMO_PORT = 1179;

    public static void main(String[] args){
        System.out.print("Brah: ");
        String msg = leerCadena();
        long oTime = System.currentTimeMillis();
        long iTime = 0;

        Session session = new Session();
        String result = "Error";
        try {
            session.connect(DEVICE_DEMO_IP, DEVICE_DEMO_PORT);
            result = session.transmit(msg);
            session.disconnect();
            iTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result + " " + (iTime - oTime));
    }

    public static String leerCadena()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String cad="";
        boolean b;
        do
        {
            try
            {
                cad=br.readLine();
                b=false;
            }
            catch (Exception e)
            {
                b=true;
                System.out.println("Error de entrada-Repetir ");
            }

        }
        while (b);
        return cad;
    }

}
