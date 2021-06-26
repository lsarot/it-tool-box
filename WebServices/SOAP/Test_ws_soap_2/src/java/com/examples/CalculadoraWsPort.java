/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.examples;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Leo
 */
@Path("calculadorawsport")
public class CalculadoraWsPort {

    private com.examples_client.CalculadoraWs port;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CalculadoraWsPort
     */
    public CalculadoraWsPort() {
        port = getPort();
    }

    /**
     * Invokes the SOAP method multiply
     * @param a resource URI parameter
     * @param b resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("multiply/")
    public String getMultiply(@QueryParam("a")
            @DefaultValue("0.0") double a, @QueryParam("b")
            @DefaultValue("0.0") double b) {
        try {
            // Call Web Service Operation
            if (port != null) {
                double result = port.multiply(a, b);
                return new java.lang.Double(result).toString();
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     *
     */
    private com.examples_client.CalculadoraWs getPort() {
        try {
            // Call Web Service Operation
            com.examples_client.CalculadoraWs_Service service = new com.examples_client.CalculadoraWs_Service();
            com.examples_client.CalculadoraWs p = service.getCalculadoraWsPort();
            return p;
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }
}
