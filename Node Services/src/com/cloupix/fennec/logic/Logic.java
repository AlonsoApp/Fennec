package com.cloupix.fennec.logic;

import com.cloupix.fennec.util.R;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

/**
 * Created by AlonsoUSA on 01/08/14.
 *
 */
public class Logic {

    private static final String KEYSTORE_PASSWORD = "demopassword";
    private static final String CERT_PASSWORD = "demopassword";




    public byte[] validateSha(String sha) {
        // TODO Validar
        // TODO devolver el authKey propio de ese sha
        return null;
    }

    public boolean registerDevice(byte[] authKey) {
        // TODO Guardar en BBDD
        try {
            String sha = com.cloupix.fennec.logic.security.SecurityManager.SHAsum(authKey);
            //map.put(sha, authKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }


    // KEYSTORE STUFF

    public KeyPair getSupernodeKeyPair() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        KeyPair keyPair = R.getInstance().getKeyPair();
        if(keyPair!=null)
            return keyPair;


        FileInputStream is = new FileInputStream(R.getInstance().getKeystorePath());

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, KEYSTORE_PASSWORD.toCharArray());

        String alias = "com.cloupix.fennec";

        Key key = keystore.getKey(alias, CERT_PASSWORD.toCharArray());
        if (key instanceof PrivateKey) {
            // Get certificate of public key
            Certificate cert = keystore.getCertificate(alias);



            // Get public key
            PublicKey publicKey = cert.getPublicKey();

            // Return a key pair
            return new KeyPair(publicKey, (PrivateKey) key);
        }
        return null;
    }

    public Certificate getSupernodeCert() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        Certificate cert = R.getInstance().getSignedCertificate();
        if(cert!=null)
            return cert;


        FileInputStream is = new FileInputStream(R.getInstance().getKeystorePath());

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, KEYSTORE_PASSWORD.toCharArray());

        String alias = "signed";

        return keystore.getCertificate(alias);
    }

    public PublicKey getCertPublicKey() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        return getSupernodeKeyPair().getPublic();
    }

    public PrivateKey getCertPrivateKey() throws NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        return getSupernodeKeyPair().getPrivate();
    }

    public PublicKey verifyNodeCert(Certificate cert) {
        InputStream inStream = null;
        try {

            inStream = new FileInputStream(R.getInstance().getRootcaPath());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate ca = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();


            // Verifing by public key
            cert.verify(ca.getPublicKey());
            return cert.getPublicKey();


        } catch (Exception ex) {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
