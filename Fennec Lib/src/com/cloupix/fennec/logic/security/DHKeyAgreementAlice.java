package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.util.R;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by AlonsoUSA on 22/07/14.
 *
 */
public class DHKeyAgreementAlice {

    public static final int GENERATE_DH_PARAMS = 1;
    public static final int NO_GENERATE_DH_PARAMS = 2;


    private DHParameterSpec dhSkipParamSpec;

    private KeyAgreement aliceKeyAgree;

    private byte[] alicePubKeyEnc;
    private byte[] bobPubKeyEnc;

    private byte[] sharedSecret;

    public DHKeyAgreementAlice(int mode) throws Exception {
        init(mode);
    }

    private void init(int mode)  throws Exception  {

        switch (mode){

            case GENERATE_DH_PARAMS:

                // Some central authority creates new DH parameters
                System.out.println
                        ("Creating Diffie-Hellman parameters (takes VERY long) ...");
                AlgorithmParameterGenerator paramGen
                        = AlgorithmParameterGenerator.getInstance("DH");
                paramGen.init(512);
                AlgorithmParameters params = paramGen.generateParameters();
                dhSkipParamSpec = (DHParameterSpec)params.getParameterSpec
                        (DHParameterSpec.class);

                break;
            case NO_GENERATE_DH_PARAMS:

                // use some pre-generated, default DH parameters
                System.out.println("Using SKIP Diffie-Hellman parameters");
                dhSkipParamSpec = new DHParameterSpec(skip512Modulus,
                        skip512Base);

                break;
        }
    }


    public byte[] generateAlicePubKeyEnc() throws Exception{
        /*
         * Alice creates her own DH key pair, using the DH parameters from
         * above
         */
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(dhSkipParamSpec);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());

        // Alice encodes her public key, and sends it over to Bob.
        alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
        return alicePubKeyEnc;
    }


    public void generateSharedSecret(byte[] bobPubKeyEnc) throws Exception {
        this.bobPubKeyEnc = bobPubKeyEnc;
        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
        System.out.println("ALICE: Execute PHASE1 ...");
        aliceKeyAgree.doPhase(bobPubKey, true);
        sharedSecret = aliceKeyAgree.generateSecret();
    }



    /**
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /**
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }


    // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private static final byte skip1024ModulusBytes[] = {
            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
            (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
            (byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
            (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B,
            (byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D,
            (byte)0x45, (byte)0x1D, (byte)0x0F, (byte)0x7C,
            (byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C,
            (byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6,
            (byte)0xF3, (byte)0xC9, (byte)0x23, (byte)0xC0,
            (byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B,
            (byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB,
            (byte)0x55, (byte)0x8C, (byte)0xB8, (byte)0x5D,
            (byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD,
            (byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43,
            (byte)0xA3, (byte)0x1D, (byte)0x18, (byte)0x6C,
            (byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C,
            (byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C,
            (byte)0xE1, (byte)0xB1, (byte)0x29, (byte)0x40,
            (byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C,
            (byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72,
            (byte)0xD6, (byte)0x86, (byte)0xC4, (byte)0x03,
            (byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29,
            (byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C,
            (byte)0xD9, (byte)0x96, (byte)0x9F, (byte)0xAB,
            (byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B,
            (byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08,
            (byte)0x3D, (byte)0x66, (byte)0xA4, (byte)0x5D,
            (byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C,
            (byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22,
            (byte)0x19, (byte)0x26, (byte)0xBA, (byte)0xAB,
            (byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55,
            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7
    };

    private static final byte skip512ModulusBytes[] = {
            (byte)0xFA, (byte)0xF7, (byte)0x2D, (byte)0x97,
            (byte)0x66, (byte)0x5C, (byte)0x47, (byte)0x66,
            (byte)0xB9, (byte)0xBB, (byte)0x3C, (byte)0x33,
            (byte)0x75, (byte)0xCC, (byte)0x54, (byte)0xE0,
            (byte)0x71, (byte)0x12, (byte)0x1F, (byte)0x90,
            (byte)0xB4, (byte)0xAA, (byte)0x94, (byte)0x4C,
            (byte)0xB8, (byte)0x8E, (byte)0x4B, (byte)0xEE,
            (byte)0x64, (byte)0xF9, (byte)0xD3, (byte)0xF8,
            (byte)0x71, (byte)0xDF, (byte)0xB9, (byte)0xA7,
            (byte)0x05, (byte)0x55, (byte)0xDF, (byte)0xCE,
            (byte)0x39, (byte)0x19, (byte)0x3D, (byte)0x1B,
            (byte)0xEB, (byte)0xD5, (byte)0xFA, (byte)0x63,
            (byte)0x01, (byte)0x52, (byte)0x2E, (byte)0x01,
            (byte)0x7B, (byte)0x05, (byte)0x33, (byte)0x5F,
            (byte)0xF5, (byte)0x81, (byte)0x6A, (byte)0xF9,
            (byte)0xC8, (byte)0x65, (byte)0xC7, (byte)0x65
    };

    // The SKIP 1024 bit modulus
    private static final BigInteger skip1024Modulus
            = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);

    // The SKIP 512 bit modulus
    private static final BigInteger skip512Modulus
            = new BigInteger(1, skip512ModulusBytes);

    // The base used with the SKIP 512 bit modulus
    private static final BigInteger skip512Base = BigInteger.valueOf(2);

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public String getSharedSecretString(){
        return toHexString(sharedSecret);
    }
}
