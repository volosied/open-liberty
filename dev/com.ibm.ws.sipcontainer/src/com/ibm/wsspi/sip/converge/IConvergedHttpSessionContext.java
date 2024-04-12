package com.ibm.wsspi.sip.converge;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.sip.ConvergedHttpSession;
import com.ibm.wsspi.session.ISession;

public interface IConvergedHttpSessionContext {
    public Object createSessionObject(ISession isess, ServletContext servCtx);
    public String getSipBaseUrlForEncoding(String contextPath, String relativePath, String scheme);
    public HttpSession getHttpSessionById(String sessId);
}
