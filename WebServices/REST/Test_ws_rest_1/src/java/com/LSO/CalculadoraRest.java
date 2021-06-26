/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LSO;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Leo
 */
@Path("calculadora")
public class CalculadoraRest {
    
    @GET
    @Path("suma")
    public int suma(@QueryParam("a") @DefaultValue("10") int a, @QueryParam("b") @DefaultValue("10") int b) {
        return a+b;
    }
    
}
