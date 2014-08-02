package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.business.CipheredContentB;
import com.cloupix.fennec.util.R;
import com.sun.deploy.util.ArrayUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by AlonsoUSA on 28/07/14.
 *
 */
public class AES {

    public static void main(String[] args) {


        try {
            SecurityManager securityManager = new SecurityManagerB(new SecurityLevel("B", 0));
            securityManager.setAuthKey("ThisIsASecretKey".getBytes(R.charset));
            CipheredContent cipheredContent = securityManager.cipher("Hola!".getBytes(R.charset));


            CipheredContentB cipheredContentB = new CipheredContentB();
            int msgKeyLength = ((CipheredContentB)cipheredContent).getMsgKeyLenght();
            int authKeyShaLeng = ((CipheredContentB)cipheredContent).getAuthKeyShaLength();

            cipheredContentB.setMsgKeyLength(msgKeyLength);
            cipheredContentB.setAuthKeyShaLength(authKeyShaLeng);
            cipheredContentB.setFullContent(cipheredContent.getFullContent());
            cipheredContentB.split();


            byte[] result = securityManager.decipher(cipheredContentB);
            System.out.println(new String(result, R.charset));




        } catch (Exception e) {
            e.printStackTrace();
        }

        /*



        try {
            String key = "26daa744bc5d5c3901911f472c3d59a1dfa03d68";
            byte[] ciphertext = cipher(key, "1234567890123456".getBytes());
            System.out.println("decrypted value:" + new String(decipher(key, ciphertext), "utf-8"));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
    }

    public static byte[] cipher(String key, byte[] value) throws GeneralSecurityException {

        byte[] raw = SecurityManager.hex2ByteArray(key);


        if (raw.length != 32) {
            throw new IllegalArgumentException("Invalid key size.");
        }


        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(Arrays.copyOfRange(raw, 0, 16)));
        return cipher.doFinal(value);
    }

    public static byte[] decipher(String key, byte[] encrypted) throws GeneralSecurityException {

        byte[] raw = SecurityManager.hex2ByteArray(key);

        if (raw.length != 32) {
            throw new IllegalArgumentException("Invalid key size.");
        }

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(Arrays.copyOfRange(raw, 0, 16)));
        return cipher.doFinal(encrypted);

    }
}
