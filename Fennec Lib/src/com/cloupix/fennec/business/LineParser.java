package com.cloupix.fennec.business;

import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.logic.network.FennecProtocol;

import java.util.StringTokenizer;

/**
 * Created by AlonsoUSA on 16/07/14.
 *
 */
public class LineParser {

    private StringTokenizer st;

    public LineParser(String line, String delim){
        this.st = new StringTokenizer(line, delim);
    }

    public String getNext() throws ProtocolException {
        if(st.hasMoreElements())
            return decode(st.nextToken().trim());
        else
            throw new ProtocolException(ProtocolException.BAD_IMPLEMENTED, "getNext() executed in an empty StringTokenizer");
    }

    public int getNextInt() throws ProtocolException {
        String next = "";
        try {
            next = getNext();
            return Integer.parseInt(next);
        }catch (ProtocolException eP){
            throw eP;
        }catch (Exception e){
            throw new ProtocolException(ProtocolException.BAD_IMPLEMENTED, "Parse int failed obtained: \"" + next + "\"");
        }
    }

    public void validateNext(String string) throws ProtocolException {
        String command = getNext();
        if(!decode(command).equals(string))
            throw new ProtocolException(ProtocolException.BAD_IMPLEMENTED,
                    "Command espected " + string + " obtained " + command);
    }

    private static String encode(String str){
        String strLimpio = str.replaceAll(FennecProtocol.SP, "%01");
        strLimpio = strLimpio.replaceAll(FennecProtocol.CLRF, "%02");
        if(strLimpio.equals(""))
            strLimpio = "%03";
        return strLimpio;
    }

    public static String decode(String str){
        String strLimpio = str.contains(FennecProtocol.PRELUDE) ? str.substring(str.indexOf(FennecProtocol.PRELUDE)+3):str;
        strLimpio = strLimpio.replaceAll("%01", FennecProtocol.SP);
        strLimpio = strLimpio.replaceAll("%02", FennecProtocol.CLRF);
        if(strLimpio.equals("%03"))
            strLimpio = "";
        return strLimpio;
    }

    public static String encodeLine(String[] a, String delim, String blockDelim){
        String result = FennecProtocol.PRELUDE;
        // Si nos pasan un array vacio casca
        if(a.length>0)
            result = result + encode(a[0]);
        for(int i =1; i<a.length; i++){
            result = result + delim + encode(a[i]);
        }
        return result + blockDelim;
    }
}
