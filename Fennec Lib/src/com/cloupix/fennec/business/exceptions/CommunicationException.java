package com.cloupix.fennec.business.exceptions;

import com.cloupix.fennec.business.Status;

/**
 * Created by AlonsoUSA on 18/07/14.
 *
 */
public class CommunicationException extends SessionException{

    private Status staus;

    public CommunicationException(Status staus) {
        this.staus = staus;
    }

    public Status getStaus() {
        return staus;
    }

    public void setStaus(Status staus) {
        this.staus = staus;
    }
}
