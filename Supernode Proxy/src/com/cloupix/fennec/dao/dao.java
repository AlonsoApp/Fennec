package com.cloupix.fennec.dao;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.sql.*;

/**
 * Created by AlonsoUSA on 01/08/14.
 *
 */
public class Dao {

    public static void main( String args[] ) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // --- Secure ---
            String selectStatement = "SELECT * FROM User WHERE userId = ? ";
            PreparedStatement prepStmt = c.prepareStatement(selectStatement);
            prepStmt.setString(1, "");
            ResultSet rs = prepStmt.executeQuery();
            // --- End-Secure ---

            String keystoreFilename = "my.keystore";

            char[] password = "password".toCharArray();
            String alias = "alias";

            FileInputStream fIn = new FileInputStream(keystoreFilename);
            KeyStore keystore = KeyStore.getInstance("JKS");

            keystore.load(fIn, password);

            Key key = keystore.getKey(alias, "password".toCharArray());
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);

                // Get public key
                PublicKey publicKey = cert.getPublicKey();

                // Return a key pair
                new KeyPair(publicKey, (PrivateKey) key);
            }



            stmt = c.createStatement();
            String sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                    "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                    "VALUES (2, 'Allen', 25, 'Texas', 15000.00 );";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                    "VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                    "VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
}
