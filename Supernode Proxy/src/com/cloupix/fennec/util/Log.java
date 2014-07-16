package com.cloupix.fennec.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by AlonsoUSA on 01/07/14.
 *
 */
public class Log {

    private static final String UNKNOWN_CLASS = "UNKNOWN";

    public static void i(String msg){
        Logger.getLogger(UNKNOWN_CLASS).log(Level.INFO, msg);
    }

    public static void i(String className, String msg){
        Logger.getLogger(className).log(Level.INFO, msg);
    }

    public static void e(String msg){
        Logger.getLogger(UNKNOWN_CLASS).log(Level.SEVERE, msg);
    }

    public static void e(String className, String msg){
        Logger.getLogger(className).log(Level.SEVERE, msg);
    }

}
