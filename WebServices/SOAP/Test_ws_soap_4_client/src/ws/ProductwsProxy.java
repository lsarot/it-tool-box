package ws;

public class ProductwsProxy implements ws.Productws {
  private String _endpoint = null;
  private ws.Productws productws = null;
  
  public ProductwsProxy() {
    _initProductwsProxy();
  }
  
  public ProductwsProxy(String endpoint) {
    _endpoint = endpoint;
    _initProductwsProxy();
  }
  
  private void _initProductwsProxy() {
    try {
      productws = (new ws.ProductwsImplServiceLocator()).getProductwsImplPort();
      if (productws != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)productws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)productws)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (productws != null)
      ((javax.xml.rpc.Stub)productws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ws.Productws getProductws() {
    if (productws == null)
      _initProductwsProxy();
    return productws;
  }
  
  public ws.Product getProduct() throws java.rmi.RemoteException{
    if (productws == null)
      _initProductwsProxy();
    return productws.getProduct();
  }
  
  public ws.Product[] getAll() throws java.rmi.RemoteException{
    if (productws == null)
      _initProductwsProxy();
    return productws.getAll();
  }
  
  
}