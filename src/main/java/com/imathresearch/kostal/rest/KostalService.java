package com.imathresearch.kostal.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
public class KostalService {
    
    @GET
    @Path("/")
    public KostalService getServiceInfo() {
        return new KostalService();
    }
    
    @GET
    @Path("/messages")
    public Response getMessages(
            @QueryParam("grouped") @DefaultValue("false") boolean grouped
            ) {
        String response = "Hello World";
        return Response.status(200).entity(response).build();
    }
    
    @GET
    @Path("/messages/{id}")
    public Response getMessageById(
            @PathParam("id") String id,
            @QueryParam("grouped") @DefaultValue("false") boolean grouped
            ) {
        return null;
    }

    
    @GET
    @Path("/search")
    public Response search(
            @PathParam("eSearchQuery") String query
            ) {
        return null;
    }
}