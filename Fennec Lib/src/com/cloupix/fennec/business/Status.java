package com.cloupix.fennec.business;

/**
 * Created by AlonsoUSA on 17/07/14.
 *
 */
public class Status {

    private int code;
    private String msg;

    public Status(int code){
        this.code = code;
        this.msg = getMsgByCode(code);
    }

    public Status(int code, String msg){
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String toString(String separator) {
        return code + separator + msg;
    }

    private String getMsgByCode(int code){
        switch (code){
            case 100: return "Continue";
            case 200: return "OK";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 406: return "Not Acceptable";
            case 409: return "Conflict";
            case 412: return "Precondition Failed";
            case 500: return "Internal Server Error";
            case 505: return "Fennec Version not supported";
            default:  return "Unknown";
        }
    }
}
