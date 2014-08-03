package com.cloupix.fennec.business;

import com.cloupix.fennec.business.exceptions.ProtocolException;

import java.util.StringTokenizer;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class BlockParser {
    private StringTokenizer st;
    private String lineDelim;

    public BlockParser(String block, String blockDelim, String lineDelim){
        this.st = new StringTokenizer(block, blockDelim);
        this.lineDelim = lineDelim;
    }


    public String getNextLine() throws ProtocolException {
        if(st.hasMoreElements())
            return st.nextToken().trim();
        else
            throw new ProtocolException(ProtocolException.BAD_IMPLEMENTED, "getNext() executed in an empty StringTokenizer");
    }

    public LineParser getNextLineParser() throws ProtocolException {
        return new LineParser(getNextLine(), lineDelim);
    }
}
