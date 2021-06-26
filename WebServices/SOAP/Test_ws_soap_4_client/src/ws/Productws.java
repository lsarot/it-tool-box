/**
 * Productws.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws;

public interface Productws extends java.rmi.Remote {
    public ws.Product getProduct() throws java.rmi.RemoteException;
    public ws.Product[] getAll() throws java.rmi.RemoteException;
}
