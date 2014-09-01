package com.cloupix.fennec.logic;

import com.cloupix.fennec.business.CertificateInfo;
import com.cloupix.fennec.business.KeystoreInfo;
import com.cloupix.fennec.util.R;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by AlonsoUSA on 01/08/14.
 *
 */
public class Logic {

    public void storeAuthKey(byte[] authKey) throws IOException {
        FileOutputStream fos = new FileOutputStream(R.getInstance().getAuthKeyPath());
        fos.write(authKey);
        fos.close();
    }

    public byte[] getAuthKey(){
        File db = new File(R.getInstance().getAuthKeyPath());
        if(!db.exists())
            return null;

        try {
            return Files.readAllBytes(Paths.get(R.getInstance().getAuthKeyPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // KEYSTORE STUFF

    public KeyPair getKeyPair() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        KeyPair keyPair = R.getInstance().getKeyPair();
        if(keyPair!=null)
            return keyPair;

        KeystoreInfo keystoreInfo = R.getInstance().getKeystoreInfo();

        FileInputStream is = new FileInputStream(R.getInstance().getKeystorePath());

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, keystoreInfo.getPassword().toCharArray());


        CertificateInfo certInfo = keystoreInfo.getCertificateInfo(false);


        Key key = keystore.getKey(certInfo.getAlias(), certInfo.getPassword().toCharArray());
        if (key instanceof PrivateKey) {
            // Get certificate of public key
            Certificate cert = keystore.getCertificate(certInfo.getAlias());



            // Get public key
            PublicKey publicKey = cert.getPublicKey();

            // Return a key pair
            return new KeyPair(publicKey, (PrivateKey) key);
        }
        return null;
    }

    public Certificate getCert() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        Certificate cert = R.getInstance().getSignedCertificate();
        if(cert!=null)
            return cert;

        KeystoreInfo keystoreInfo = R.getInstance().getKeystoreInfo();

        FileInputStream is = new FileInputStream(R.getInstance().getKeystorePath());

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, keystoreInfo.getPassword().toCharArray());

        CertificateInfo certInfo = keystoreInfo.getCertificateInfo(true);
        if(certInfo == null)
            return null;

        return keystore.getCertificate(certInfo.getAlias());
    }

    public PublicKey verifyCert(Certificate cert) {
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
