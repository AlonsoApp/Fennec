package com.cloupix.fennec.logic;

import com.cloupix.fennec.business.CertificateInfo;
import com.cloupix.fennec.business.KeystoreInfo;
import com.cloupix.fennec.business.Supernode;
import com.cloupix.fennec.logic.security.SecurityLevel;
import com.cloupix.fennec.util.R;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by AlonsoUSA on 04/08/14.
 *
 */
public class ConfigHandler extends DefaultHandler {

    private KeystoreInfo keystoreInfo;
    private ArrayList<Supernode> supernodeList = null;


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("Security")) {
            String sClass = attributes.getValue("class");
            int sLevel = Integer.parseInt(attributes.getValue("level"));

            R.getInstance().setSecurityLevel(new SecurityLevel(sClass, sLevel));

        } else if (qName.equalsIgnoreCase("Keystore")) {
            String password = attributes.getValue("password");
            keystoreInfo = new KeystoreInfo(password);

        } else if (qName.equalsIgnoreCase("Certificate")) {

            String alias = attributes.getValue("alias");
            String password = attributes.getValue("password");
            String signed = attributes.getValue("signed");

            keystoreInfo.addCertificateInfo(new CertificateInfo(alias, password, Boolean.parseBoolean(signed)));

        } else if (qName.equalsIgnoreCase("Supernode-list")) {
            String fixed = attributes.getValue("fixed");

            R.getInstance().setFixedSupernodeList(Boolean.parseBoolean(fixed));

            this.supernodeList = new ArrayList<Supernode>();

        } else if (qName.equalsIgnoreCase("Supernode")) {
            String ip = attributes.getValue("ip");
            supernodeList.add(new Supernode(ip));
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Supernode-list")) {
            R.getInstance().setSupernodeList(supernodeList);
        } else if(qName.equalsIgnoreCase("Keystore")) {
            R.getInstance().setKeystoreInfo(keystoreInfo);
        }
    }
}
