package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.business.exceptions.ProtocolException;
import com.cloupix.fennec.util.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public abstract class SecurityManager {


    protected SecurityLevel securityLevel;

    protected byte[] authKey;
    protected String authKeySha;


    public static SecurityManager build(SecurityLevel securityLevel) throws ProtocolException {

        if(securityLevel.getSecurityClass().equals("A")){
            return new SecurityManagerA(securityLevel);
        }else if(securityLevel.getSecurityClass().equals("B")){
            return new SecurityManagerB(securityLevel);
        }else{
            throw new ProtocolException(ProtocolException.UNKNOWN_SECURITY_LEVEL, "Unknown security level " + securityLevel);
        }
    }

    public abstract CipheredContent cipher(byte[] content) throws Exception;

    public abstract byte[] decipher(CipheredContent cipheredContent) throws Exception;

    public abstract SecurityLevel getSecurityLevel();

    public abstract void setSecurityLevel(SecurityLevel securityLevel);


    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return byteArray2Hex(md.digest(convertme));
    }

    public static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static byte[] hex2ByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    protected byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public void setAuthKey(byte[] authKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        this.authKey = authKey;
        this.authKeySha = SHAsum(authKey);
    }

    public String decipherToString(CipheredContent cipheredContent) throws Exception {
        byte[] content = decipher(cipheredContent);

        return new String(content, R.charset);
    }
}
