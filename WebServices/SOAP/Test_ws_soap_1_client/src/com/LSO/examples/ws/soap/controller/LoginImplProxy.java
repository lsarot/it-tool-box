package com.LSO.examples.ws.soap.controller;

public class LoginImplProxy implements com.LSO.examples.ws.soap.controller.LoginImpl {
  private String _endpoint = null;
  private com.LSO.examples.ws.soap.controller.LoginImpl loginImpl = null;
  
  public LoginImplProxy() {
    _initLoginImplProxy();
  }
  
  public LoginImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initLoginImplProxy();
  }
  
  private void _initLoginImplProxy() {
    try {
      loginImpl = (new com.LSO.examples.ws.soap.controller.LoginImplServiceLocator()).getLoginImpl();
      if (loginImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)loginImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)loginImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (loginImpl != null)
      ((javax.xml.rpc.Stub)loginImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.LSO.examples.ws.soap.controller.LoginImpl getLoginImpl() {
    if (loginImpl == null)
      _initLoginImplProxy();
    return loginImpl;
  }
  
  public com.LSO.examples.ws.soap.model.BeanLogin validarLogin(com.LSO.examples.ws.soap.model.BeanLogin b) throws java.rmi.RemoteException{
    if (loginImpl == null)
      _initLoginImplProxy();
    return loginImpl.validarLogin(b);
  }
  
  
}