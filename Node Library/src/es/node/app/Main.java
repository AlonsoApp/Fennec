package es.node.app;

import com.cloupix.fennec.business.Session;
import com.cloupix.fennec.util.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class Main {

    private static final String DEVICE_DEMO_IP = "127.0.0.1";
    private static final int DEVICE_DEMO_PORT = 1179;

    public static void main(String[] args){


        long oTime = System.currentTimeMillis();
        long iTime = 0;

        Session session = new Session();
        String result = "Error";
        try {
            session.connect(DEVICE_DEMO_IP, DEVICE_DEMO_PORT);
            System.out.println("Connected :)");
            System.out.println();
            String msg = "";
            do{
                System.out.print("Brah: ");
                msg = leerCadena();

                byte[] rawResult = session.transmit(msg.getBytes(R.charset));
                //System.out.println("Transmited :)");
                result = new String(rawResult, "utf-8");
                System.out.println("Respuesta: " + result);
            }while (!msg.equals("quit"));

            session.disconnect();

            System.out.println();
            System.out.println("Disconnected :)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        iTime = System.currentTimeMillis();

        System.out.println("Tiempo: " + (iTime - oTime) + "ms");
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
