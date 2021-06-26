/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lso.test_ws_rest_3;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Leo
 */
@Path("products")
public class Products {
    
    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        List<Producto> lista = new ArrayList<Producto>();
        lista.add(new Producto("Leche", 10));
        lista.add(new Producto("Carne", 22));
        lista.add(new Producto("Pollo", 15));
        
        return Response.ok(lista).build();
    }
    
}
