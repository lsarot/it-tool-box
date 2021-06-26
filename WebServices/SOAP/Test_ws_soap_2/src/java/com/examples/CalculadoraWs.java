
package com.examples;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Leo
 */
@WebService(serviceName = "CalculadoraWs")
public class CalculadoraWs {

    @WebMethod(operationName = "multiply")
    public double multiply(@WebParam(name = "a") double a, @WebParam(name = "b") double b) {
        return a * b;
    }
}