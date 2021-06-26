/**
 * ProductwsImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws;

public class ProductwsImplServiceLocator extends org.apache.axis.client.Service implements ws.ProductwsImplService {

    public ProductwsImplServiceLocator() {
    }


    public ProductwsImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProductwsImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProductwsImplPort
    private java.lang.String ProductwsImplPort_address = "http://localhost:8175/ws/product";

    public java.lang.String getProductwsImplPortAddress() {
        return ProductwsImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProductwsImplPortWSDDServiceName = "ProductwsImplPort";

    public java.lang.String getProductwsImplPortWSDDServiceName() {
        return ProductwsImplPortWSDDServiceName;
    }

    public void setProductwsImplPortWSDDServiceName(java.lang.String name) {
        ProductwsImplPortWSDDServiceName = name;
    }

    public ws.Productws getProductwsImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProductwsImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProductwsImplPort(endpoint);
    }

    public ws.Productws getProductwsImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ws.ProductwsImplPortBindingStub _stub = new ws.ProductwsImplPortBindingStub(portAddress, this);
            _stub.setPortName(getProductwsImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProductwsImplPortEndpointAddress(java.lang.String address) {
        ProductwsImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ws.Productws.class.isAssignableFrom(serviceEndpointInterface)) {
                ws.ProductwsImplPortBindingStub _stub = new ws.ProductwsImplPortBindingStub(new java.net.URL(ProductwsImplPort_address), this);
                _stub.setPortName(getProductwsImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ProductwsImplPort".equals(inputPortName)) {
            return getProductwsImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws/", "ProductwsImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws/", "ProductwsImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProductwsImplPort".equals(portName)) {
            setProductwsImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
