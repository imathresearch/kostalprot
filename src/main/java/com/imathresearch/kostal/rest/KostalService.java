package com.imathresearch.kostal.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.imathresearch.kostal.elasticclient.ElasticClient;

@Path("/")
public class KostalService {
    
    @GET
    @Path("/")
    public KostalService getServiceInfo() {
        return new KostalService();
    }
    
    @GET
    @Path("/messages")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMessages(
            @QueryParam("grouped") @DefaultValue("false") boolean grouped
            ) {
        
        Response resp = null;
        try {
            resp = ElasticClient.sendRequest("GET", "_search", "pretty=true&q=*", null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resp;
    }
    
    @GET
    @Path("/messages/{id}")
    public Response getMessageById(
            @PathParam("id") String id,
            @QueryParam("grouped") @DefaultValue("false") boolean grouped
            ) {
        Response resp = null;
        try {
            resp = ElasticClient.sendRequest("GET", "Obra/" + id, "pretty", null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resp;
    }

    
    @GET
    @Path("/search")
    public Response search(
            @QueryParam("q") String query
            ) {
        Response resp = null;
        try {
            resp = ElasticClient.sendRequest("GET", "_search", "pretty=true&q=" + query, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resp;
    }
}