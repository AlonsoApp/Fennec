package com.cloupix.fennec.logic.security;

import java.security.*;
import javax.crypto.Cipher;

/**
 * Created by AlonsoUSA on 28/07/14.
 *
 */

public class RSACipher {
    public static byte[] encrypt(byte[] inpBytes, PublicKey key,
                                  String xform) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
    public static byte[] decrypt(byte[] inpBytes, PrivateKey key,
                                  String xform) throws Exception{
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
    public static byte[] encrypt(byte[] inpBytes, PrivateKey key,
                                  String xform) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
    public static byte[] decrypt(byte[] inpBytes, PublicKey key,
                                 String xform) throws Exception{
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    public static KeyPair generateKeyPair(String xform, int bitKeySize) throws NoSuchAlgorithmException {
        // Generate a key-pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(bitKeySize); // 512 is the keysize.
        return kpg.generateKeyPair();
    }

    public static void main(String[] unused) throws Exception {
        //long oTime = System.currentTimeMillis();

        String xform = "RSA/ECB/PKCS1Padding";
        // Generate a key-pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512); // 512 is the keysize.
        KeyPair kp = kpg.generateKeyPair();
        PublicKey pubk = kp.getPublic();
        PrivateKey prvk = kp.getPrivate();

        byte[] dataBytes =
                "J2EE Security for Servlets, EJBs and Web Services".getBytes();

        byte[] encBytes = encrypt(dataBytes, pubk, xform);
        byte[] decBytes = decrypt(encBytes, prvk, xform);

        boolean expected = java.util.Arrays.equals(dataBytes, decBytes);
        System.out.println("Test " + (expected ? "SUCCEEDED!" : "FAILED!"));
        //System.out.println("Tiempo: " + (System.currentTimeMillis()-oTime));
    }
}
