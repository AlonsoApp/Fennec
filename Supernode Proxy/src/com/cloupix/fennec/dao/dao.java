package com.cloupix.fennec.dao;

import com.cloupix.fennec.business.AnalyticAuthentication;

import java.sql.*;

/**
 * Created by AlonsoUSA on 01/08/14.
 *
 */
public class Dao {

    private static Connection con;
    private static final String dataSource = "Supernode Proxy/data/supernode.db";
    private static final String username = "root";
    private static final String password = "demopassword";
    private static final String driver = "org.sqlite.JDBC";
    private static final String protocol = "jdbc:sqlite";

    public void open(){
        try{
            Class.forName(driver);
            String url = protocol + ":" + dataSource;
            con = DriverManager.getConnection(url);
            con.setAutoCommit(false);
        }
        catch (ClassNotFoundException clasEx) {
            clasEx.printStackTrace();
        }
        catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }

    public void close(){
        if(con!=null)
        {
            try{
                if(!con.isClosed())
                    con.close();
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void commitTransaccion() throws SQLException{
        con.commit();
    }

    public void rollbackTransaccion() throws SQLException{
        con.rollback();
    }

    public void createDatabase() throws SQLException {
        open();

        Statement stmt = con.createStatement();
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_AUTHENTICATION);

        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_CONNECTION_TYPE);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_TRANSMISSION_TYPE);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_DEVICE);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_DEVICE_CERTIFICATE);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_PROTOCOL_VERSION);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_SESSION);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_CONNECTION);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_SECURITY);
        stmt.executeUpdate(ContractFennecDB.SQL_CREATE_TABLE_TRANSMISSION);


        stmt.close();
        commitTransaccion();
        close();
    }

    public AnalyticAuthentication getAuthenticationBySha(String sha) throws SQLException {
        String selectStatement = "SELECT * FROM " + ContractFennecDB.Authentication.TABLE_NAME +
                " WHERE " + ContractFennecDB.Authentication.COLUMN_NAME_AUTH_KEY_SHA + " = ? ";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);
        prepStmt.setString(1, sha);
        ResultSet rs = prepStmt.executeQuery();

        AnalyticAuthentication authentication = null;
        while (rs.next())
        {
            authentication = new AnalyticAuthentication();
            authentication.setIdAuthentication(rs.getLong(1));
            authentication.setAuthKeySha(rs.getString(2));
            authentication.setAuthKey(rs.getBytes(3));
        }
        rs.close();
        prepStmt.close();


        return authentication;
    }

    public void insertAuthentication(AnalyticAuthentication analyticAuthentication) throws SQLException {
        String insertQuery = "INSERT INTO " + ContractFennecDB.Authentication.TABLE_NAME +
                " ("+ContractFennecDB.Authentication.COLUMN_NAME_AUTH_KEY_SHA+", "+ ContractFennecDB.Authentication.COLUMN_NAME_AUTH_KEY +
                ") VALUES (?, ?)";
        PreparedStatement prepStmt = con.prepareStatement(insertQuery);
        prepStmt.setString(1, analyticAuthentication.getAuthKeySha());
        prepStmt.setBytes(2, analyticAuthentication.getAuthKey());

        prepStmt.executeUpdate();
        prepStmt.close();
    }
}
