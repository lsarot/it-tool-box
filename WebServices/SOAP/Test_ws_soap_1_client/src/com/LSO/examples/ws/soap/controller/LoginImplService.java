/**
 * LoginImplService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.LSO.examples.ws.soap.controller;

public interface LoginImplService extends javax.xml.rpc.Service {
    public java.lang.String getLoginImplAddress();

    public com.LSO.examples.ws.soap.controller.LoginImpl getLoginImpl() throws javax.xml.rpc.ServiceException;

    public com.LSO.examples.ws.soap.controller.LoginImpl getLoginImpl(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
