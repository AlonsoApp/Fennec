package com.cloupix.fennec.dao;

/**
 * Created by AlonsoUSA on 01/08/14.
 *
 */
public class ContractFennecDB {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ContractFennecDB() {}


    //<editor-fold desc="Tables">

    public static abstract class Device {

        public static final String TABLE_NAME = "device";
        public static final String COLUMN_NAME_DEVICE_ID = "device_id";
        public static final String COLUMN_NAME_MAC_ADDRESS = "mac_address";
        public static final String COLUMN_NAME_AUTHENTICATION_ID = "authentication_id";
    }

    public static abstract class DeviceCertificate {

        public static final String TABLE_NAME = "device_certificate";
        public static final String COLUMN_NAME_DEVICE_CERTIFICATE_ID = "device_certificate_id";
        public static final String COLUMN_NAME_PUBLIC_KEY = "public_key";
        public static final String COLUMN_NAME_SIGNED = "signed";
        public static final String COLUMN_NAME_CREATION_TIMESTAMP = "creation_timestamp";
        public static final String COLUMN_NAME_DEVICE_ID = "device_id";

    }

    public static abstract class ConnectionType {

        public static final String TABLE_NAME = "connection_type";
        public static final String COLUMN_NAME_COMMUNICATION_TYPE_ID = "connection_type_id";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class Authentication {

        public static final String TABLE_NAME = "authentication";
        public static final String COLUMN_NAME_AUTHENTICATION_ID = "authentication_id";
        public static final String COLUMN_NAME_AUTH_KEY_SHA = "auth_key_sha";
        public static final String COLUMN_NAME_AUTH_KEY = "auth_key";
    }

    public static abstract class Session {

        public static final String TABLE_NAME = "session";
        public static final String COLUMN_NAME_SESSION_ID = "session_id";
        public static final String COLUMN_NAME_START_TIMESTAMP = "start_timestamp";
        public static final String COLUMN_NAME_END_TIMESTAMP = "end_timestamp";

    }

    public static abstract class Connection {

        public static final String TABLE_NAME = "connection";
        public static final String COLUMN_NAME_CONNECTION_ID = "connection_id";
        public static final String COLUMN_NAME_SOURCE_IP = "source_ip";
        public static final String COLUMN_NAME_DESTINATION_IP = "destination_ip";
        public static final String COLUMN_NAME_DESTINATION_PORT = "destination_port";
        public static final String COLUMN_NAME_SESSION_ID = "session_id";
        public static final String COLUMN_NAME_DEVICE_ID = "device_id";
        public static final String COLUMN_NAME_DEVICE_CERTIFICATE_ID = "device_certificate_id";
        public static final String COLUMN_NAME_COMMUNICATION_TYPE_ID = "communication_type_id";
    }

    public static abstract class Security {

        public static final String TABLE_NAME = "security";
        public static final String COLUMN_NAME_SECURITY_ID = "security_id";
        public static final String COLUMN_NAME_CLASS = "class";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_CONNECTION_ID = "connection_id";
    }

    public static abstract class Transmission {

        public static final String TABLE_NAME = "transmission";
        public static final String COLUMN_NAME_TRANSMISSION_ID = "transmission_id";
        public static final String COLUMN_NAME_AUTH_KEY_SHA = "auth_key_sha";
        public static final String COLUMN_NAME_CONTENT_LENGTH = "content_length";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_CONNECTION_ID = "connection_id";
        public static final String COLUMN_NAME_COMMUNICATION_TYPE_ID = "communication_type_id";
    }

    public static abstract class ProtocolVersion {

        public static final String TABLE_NAME = "protocol_version";
        public static final String COLUMN_NAME_PROTOCOL_VERSION_ID = "protocol_version_id";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class TransmissionType {

        public static final String TABLE_NAME = "transmission_type";
        public static final String COLUMN_NAME_TRANSMISSION_TYPE_ID = "transmission_type_id";
        public static final String COLUMN_NAME_NAME = "name";
    }

    //</editor-fold>


    //<editor-fold desc="Statements">

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String BLOB_TYPE = " BLOB";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ", ";

    public static final String SQL_CREATE_TABLE_DEVICE =
            "CREATE TABLE " + Device.TABLE_NAME + " (" +
                    Device.COLUMN_NAME_DEVICE_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Device.COLUMN_NAME_MAC_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    Device.COLUMN_NAME_AUTHENTICATION_ID + INTEGER_TYPE + COMMA_SEP +

                    " FOREIGN KEY ("+ Device.COLUMN_NAME_AUTHENTICATION_ID+")" +
                    " REFERENCES "+ Authentication.TABLE_NAME+" ("+ Authentication.COLUMN_NAME_AUTHENTICATION_ID+")" +
                    " );";

    public static final String SQL_CREATE_TABLE_DEVICE_CERTIFICATE =
            "CREATE TABLE " + DeviceCertificate.TABLE_NAME + " (" +
                    DeviceCertificate.COLUMN_NAME_DEVICE_CERTIFICATE_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    DeviceCertificate.COLUMN_NAME_PUBLIC_KEY + TEXT_TYPE + COMMA_SEP +
                    DeviceCertificate.COLUMN_NAME_SIGNED + INTEGER_TYPE + COMMA_SEP +
                    DeviceCertificate.COLUMN_NAME_CREATION_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    DeviceCertificate.COLUMN_NAME_DEVICE_ID + INTEGER_TYPE + COMMA_SEP +

                    " FOREIGN KEY ("+ DeviceCertificate.COLUMN_NAME_DEVICE_ID+")" +
                    " REFERENCES "+ Device.TABLE_NAME+" ("+ Device.COLUMN_NAME_DEVICE_ID+")" +
                    " );";

    public static final String SQL_CREATE_TABLE_CONNECTION_TYPE =
            "CREATE TABLE " + ConnectionType.TABLE_NAME + " (" +
                    ConnectionType.COLUMN_NAME_COMMUNICATION_TYPE_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ConnectionType.COLUMN_NAME_NAME + TEXT_TYPE +
                    " );";

    public static final String SQL_CREATE_TABLE_AUTHENTICATION =
            "CREATE TABLE " + Authentication.TABLE_NAME + " (" +
                    Authentication.COLUMN_NAME_AUTHENTICATION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Authentication.COLUMN_NAME_AUTH_KEY_SHA + TEXT_TYPE + COMMA_SEP +
                    Authentication.COLUMN_NAME_AUTH_KEY + BLOB_TYPE +
                    " );";

    public static final String SQL_CREATE_TABLE_SESSION =
            "CREATE TABLE " + Session.TABLE_NAME + " (" +
                    Session.COLUMN_NAME_SESSION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Session.COLUMN_NAME_START_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    Session.COLUMN_NAME_END_TIMESTAMP + INTEGER_TYPE +
                    " );";

    public static final String SQL_CREATE_TABLE_CONNECTION =
            "CREATE TABLE " + Connection.TABLE_NAME + " (" +
                    Connection.COLUMN_NAME_CONNECTION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Connection.COLUMN_NAME_SOURCE_IP + TEXT_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_DESTINATION_IP + TEXT_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_DESTINATION_PORT + INTEGER_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_SESSION_ID + INTEGER_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_DEVICE_ID + INTEGER_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_DEVICE_CERTIFICATE_ID + INTEGER_TYPE + COMMA_SEP +
                    Connection.COLUMN_NAME_COMMUNICATION_TYPE_ID + INTEGER_TYPE + COMMA_SEP +
                    " FOREIGN KEY ("+ Connection.COLUMN_NAME_SESSION_ID+")" +
                    " REFERENCES "+ Session.TABLE_NAME+" ("+ Session.COLUMN_NAME_SESSION_ID+")" +
                    " FOREIGN KEY ("+ Connection.COLUMN_NAME_DEVICE_ID+")" +
                    " REFERENCES "+ Device.TABLE_NAME+" ("+ Device.COLUMN_NAME_DEVICE_ID+")" +
                    " FOREIGN KEY ("+ Connection.COLUMN_NAME_DEVICE_CERTIFICATE_ID+")" +
                    " REFERENCES "+ DeviceCertificate.TABLE_NAME+" ("+ DeviceCertificate.COLUMN_NAME_DEVICE_CERTIFICATE_ID+")" +
                    " FOREIGN KEY ("+ Connection.COLUMN_NAME_COMMUNICATION_TYPE_ID+")" +
                    " REFERENCES "+ ConnectionType.TABLE_NAME+" ("+ ConnectionType.COLUMN_NAME_COMMUNICATION_TYPE_ID+")" +
                    " );";

    public static final String SQL_CREATE_TABLE_SECURITY =
            "CREATE TABLE " + Security.TABLE_NAME + " (" +
                    Security.COLUMN_NAME_SECURITY_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Security.COLUMN_NAME_CLASS + TEXT_TYPE + COMMA_SEP +
                    Security.COLUMN_NAME_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    Security.COLUMN_NAME_CONNECTION_ID + INTEGER_TYPE + COMMA_SEP +
                    " FOREIGN KEY ("+ Security.COLUMN_NAME_CONNECTION_ID+")" +
                    " REFERENCES "+ Connection.TABLE_NAME+" ("+ Connection.COLUMN_NAME_CONNECTION_ID+")" +
                    " );";

    public static final String SQL_CREATE_TABLE_TRANSMISSION =
            "CREATE TABLE " + Transmission.TABLE_NAME + " (" +
                    Transmission.COLUMN_NAME_TRANSMISSION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    Transmission.COLUMN_NAME_AUTH_KEY_SHA + TEXT_TYPE + COMMA_SEP +
                    Transmission.COLUMN_NAME_CONTENT_LENGTH + INTEGER_TYPE + COMMA_SEP +
                    Transmission.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    Transmission.COLUMN_NAME_CONNECTION_ID + INTEGER_TYPE + COMMA_SEP +
                    Transmission.COLUMN_NAME_COMMUNICATION_TYPE_ID + INTEGER_TYPE + COMMA_SEP +
                    " FOREIGN KEY ("+ Transmission.COLUMN_NAME_CONNECTION_ID+")" +
                    " REFERENCES "+ Connection.TABLE_NAME+" ("+ Connection.COLUMN_NAME_CONNECTION_ID+")" +
                    " FOREIGN KEY ("+ Transmission.COLUMN_NAME_COMMUNICATION_TYPE_ID+")" +
                    " REFERENCES "+ ConnectionType.TABLE_NAME+" ("+ ConnectionType.COLUMN_NAME_COMMUNICATION_TYPE_ID+")" +
                    " );";

    public static final String SQL_CREATE_TABLE_PROTOCOL_VERSION =
            "CREATE TABLE " + ProtocolVersion.TABLE_NAME + " (" +
                    ProtocolVersion.COLUMN_NAME_PROTOCOL_VERSION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ProtocolVersion.COLUMN_NAME_NAME + TEXT_TYPE +
                    " );";

    public static final String SQL_CREATE_TABLE_TRANSMISSION_TYPE =
            "CREATE TABLE " + TransmissionType.TABLE_NAME + " (" +
                    TransmissionType.COLUMN_NAME_TRANSMISSION_TYPE_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TransmissionType.COLUMN_NAME_NAME + TEXT_TYPE +
                    " );";


    public static final String SQL_DELETE_TABLE_DEVICE =
            "DROP TABLE IF EXISTS " + Device.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_DEVICE_CERTIFICATE =
            "DROP TABLE IF EXISTS " + DeviceCertificate.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_COMMUNICATION_TYPE =
            "DROP TABLE IF EXISTS " + ConnectionType.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_AUTHENTICATION =
            "DROP TABLE IF EXISTS " + Authentication.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_SESSION =
            "DROP TABLE IF EXISTS " + Session.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_CONNECTION =
            "DROP TABLE IF EXISTS " + Connection.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_SECURITY =
            "DROP TABLE IF EXISTS " + Security.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_TRANSMISSION =
            "DROP TABLE IF EXISTS " + Transmission.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_PROTOCOL_VERSION =
            "DROP TABLE IF EXISTS " + ProtocolVersion.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_TRANSMISSION_TYPE =
            "DROP TABLE IF EXISTS " + TransmissionType.TABLE_NAME;




    //</editor-fold>
}
