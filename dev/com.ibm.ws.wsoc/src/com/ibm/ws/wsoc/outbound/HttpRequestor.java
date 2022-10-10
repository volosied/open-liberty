package com.ibm.ws.wsoc.outbound;

import java.io.IOException;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.ws.wsoc.ParametersOfInterest;
import com.ibm.wsspi.genericbnf.exception.MessageSentException;

public interface HttpRequestor {

    ClientTransportAccess getClientTransportAccess();

    void connect() throws Exception;

    void sendRequest() throws IOException, MessageSentException ;

    void sendRequest(ParametersOfInterest poi) throws IOException, MessageSentException;

    WsByteBuffer completeResponse() throws IOException;

    void closeConnection(IOException ioe);
}
