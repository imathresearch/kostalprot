package com.imathresearch.kostal.rest;

import java.io.OutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.common.lucene.search.XBooleanFilter;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentString;
import org.json.JSONArray;
import org.json.JSONObject;

import com.imathresearch.kostal.elasticclient.ElasticClient;
import com.imathresearch.kostal.readers.PstMessageParser;

import static com.imathresearch.kostal.readers.PstMessageParser.emptyField;

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
            
            System.out.println(">>>" + threaded);
            
            if (threaded) {
                // TODO rewrite json to threaded show.
               
                XContentBuilderString entity = new XContentBuilderString(resp.getEntity().toString());
                JSONObject jsonEntity = new JSONObject(entity.camelCase().getValue());
                JSONObject jsonSource = jsonEntity.getJSONObject("Source");
                
                String threadIndex = jsonSource.getString("threadIndex");
                
                String topic = null;
                if (emptyField.equals(jsonSource.getString("threadTopic"))) {
                    topic = jsonSource.getString("conversationTopic");
                } else {
                    topic = jsonSource.getString("threadTopic");
                }
                
                JSONArray xx =ElasticClient.retrieveThread(threadIndex, topic);
                resp = Response
                        .status(resp.getStatus())
                        .entity(xx.toString())
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
            @QueryParam("q") String query
            ) {
        
        Response resp = null;
        try {
            String payload = ElasticClient.searchPayload(query);
            resp = ElasticClient.sendRequest("GET", "_search", "" , payload);
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
            @QueryParam("q") String query
            ) {
        return null;
    }
    
}