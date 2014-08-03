package com.cloupix.fennec.business;

import java.util.HashMap;

/**
 * Created by AlonsoUSA on 03/08/14.
 *
 */
public class AnalyticSession {
    private long idSession;
    private long startTimestamp;
    private long endTimestamp;

    private HashMap<Integer, AnalyticConnection> map;

    public AnalyticSession() {
        this.map = new HashMap<Integer, AnalyticConnection>();
    }


    public long getIdSession() {
        return idSession;
    }

    public void setIdSession(long idSession) {
        this.idSession = idSession;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public HashMap<Integer, AnalyticConnection> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, AnalyticConnection> map) {
        this.map = map;
    }

    public AnalyticConnection getConnection(int port){
        return this.map.get(port);
    }

    public void addConnection(int port, AnalyticConnection connection){
        map.put(port, connection);
    }
}
