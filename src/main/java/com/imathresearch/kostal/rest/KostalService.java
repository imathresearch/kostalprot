package com.imathresearch.kostal.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
            @QueryParam("threaded") @DefaultValue("true") boolean threaded,
            @QueryParam("pst") @DefaultValue("") String pst,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("from") @DefaultValue("0") int from
            ) {
        
        Response resp = null;
        try {
            resp = ElasticClient.sendRequest(
                    "GET", 
                    "_search", 
                    "pretty=true&q=*&size=" + size + "&from=" + from,
                    null);
            if (threaded) {
                // TODO rewrite json to threaded show.
            }
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
            @QueryParam("threaded") @DefaultValue("true") boolean threaded,
            @QueryParam("pst") @DefaultValue("") String pst
            ) {
        Response resp = null;
        try {
            resp = ElasticClient.sendRequest(
                    "GET", 
                    "Obra/" + id, 
                    "",
                    null);
            
            if (threaded) {

                JSONObject json = new JSONObject(resp.getEntity().toString());
                JSONArray jsonThreaded = ElasticClient.retrieveResultsThreaded(json);
                resp = Response
                        .status(resp.getStatus())
                        .entity(jsonThreaded.toString())
                        .build();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return resp;
    }

    @GET
    @Path("/search")
    public Response searchAll(
            @QueryParam("q") @DefaultValue("*") String query,
            @QueryParam("threaded") @DefaultValue("true") boolean threaded
            ) {
        
        Response resp = null;
        try {
            String payload = ElasticClient.searchPayload(query);
            resp = ElasticClient.sendRequest("GET", "_search", "" , payload);
            resp = ElasticClient.formatResults(resp,threaded);
            
//            JSONObject jsonEntity = new JSONObject(resp.getEntity().toString());
//            JSONArray jsonArray = jsonEntity.getJSONObject("hits").getJSONArray("hits");
//            if (threaded) {
//                List<JSONArray> jsonThreadedList = ElasticClient.retrieveResultsThreaded(jsonArray);
//                resp = Response
//                        .status(resp.getStatus())
//                        .entity(jsonThreadedList.toString())
//                        .build();
//            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return resp;
    }
    
    @GET
    @Path("/search/{pst}")
    public Response searchPst(
            @PathParam("pst") String pst,
            @QueryParam("q") @DefaultValue("*") String query,
            @QueryParam("threaded") @DefaultValue("true") boolean threaded
            ) {

        Response resp = null;
        try {
            String payload = ElasticClient.searchPayload(query);
            resp = ElasticClient.sendRequest("GET", pst + "/_search", "" , payload);
            resp = ElasticClient.formatResults(resp,threaded);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return resp;
    }
    
    @GET
    @Path("/mapping")
    public Response mapping() {
        List<String> typeList = ElasticClient.esIndexMappings();
        JSONArray typeJson = new JSONArray(typeList.toString());
        return Response
                .status(Response.Status.OK)
                .entity(typeJson.toString())
                .build();
    }
    
}