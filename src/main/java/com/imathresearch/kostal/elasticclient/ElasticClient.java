/*
* Licensed to scrutmydocs.org (the "Author") under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. Author licenses this
* file to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package com.imathresearch.kostal.elasticclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.json.JSONObject;

import com.imathresearch.kostal.readers.PstReader;
import com.pff.PSTFile;
import com.pff.PSTFolder;


public class ElasticClient {
    
    private static PstReader reader;
    private static Node node;
    private static Client client;
    private static String urlBase = "http://localhost:8080/kostal/node";
    
    public ElasticClient() {
        System.out.println(">>> STARTING ELASTIC CLIENT <<<");
    }
    
    public static void main (String[] args) throws ElasticsearchException, IOException
    {
        
        node = NodeBuilder.nodeBuilder()
                .clusterName("my_cluster")
                .node();
        
        client = node.client();
        
        Map<String, List<Map<String, Object>>> contentList = ElasticClient.getFolderContent();
        String contentJson = ElasticClient.toJson(contentList.get("Principio de las Carpetas personales"));
//        System.out.println(">>>" + contentJson);
        
        try {
            for (Map<String, Object> map : contentList.get("Principio de las Carpetas personales")) {
                sendPost("GPO", "pretty", new JSONObject(map).toString());
            }
            
//            sendPost("GPO", "pretty", contentJson);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private static int esConnection(String method, URL urlObj, String payload) throws IOException, URISyntaxException {
        
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        
        //add request header
        con.setDoOutput(true);
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        
        OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
        osw.write(payload);
        osw.flush();
        osw.close();
        
        int responseCode = con.getResponseCode();
        System.out.println("\nSending request to URL : " + urlObj.toURI().toString());
        System.out.println("ContentMethod : " + con.getRequestMethod());
        System.out.println("Response Code : " + responseCode);
        System.out.println("payload : " + payload);
        System.out.println("Content : " + con.getResponseMessage());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println("Response : " + response.toString());
        
        return responseCode;
    }
    
    private static void sendPost(String action, String urlParams, String payload) throws Exception {

        String urlApp = "/pst/" + action;
        String url = urlBase + urlApp + "?" + urlParams;
        String method = "POST";
        System.out.println(">>>" + url);
        
        int code = esConnection(method, new URL(url), payload);
    }
    
    private static void sendGet() {
        
    }
    
    /*
    private static void sendBulkData(String data) throws Exception {
        String payload = "";
        sendPost("GPO/_bulk", "", payload);
    }*/ 
    
    private static void loadPstFileTest() {
        try {
            reader = new PstReader("GPO.pst");
            
            assertTrue(reader.getFile().exists());
            assertNotNull(reader.getPstFile());
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    private static Map<String, List<Map<String, Object>>> getFolderContent() {
        loadPstFileTest();
        PSTFile pstFile = reader.getPstFile();
        assertNotNull(pstFile);
        
        try {
            pstFile.getRootFolder().getContentCount();
            PSTFolder root = pstFile.getRootFolder();
            return reader.getFolderContent(root);
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }
    
    private static String toJson(List<Map<String, Object>> contentList) {//(Map<String, List<Map<String, Object>>> contentList) {
        System.out.println(contentList.get(0));
        System.out.println(contentList);
//        JSONObject json = null;
//        String jsonText = null;
//        for (Map<String, Object> map : contentList) {
//            json = new JSONObject(map);
//            jsonText = jsonText +"\n"+ json.toString();
//        }
        
        JSONObject jsonText = new JSONObject(contentList);
        System.out.println(jsonText);
        return jsonText.toString();
    }
}

/*import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.wares.NodeServlet;

public class ElasticClient {
    
    private Node node;
    private Client client;
    
    public ElasticClient() {
        System.out.println(">>> STARTING ELASTIC CLIENT <<<");
        startClient();
    }
    
    protected void startClient() {
        try {
        System.out.println(">>> 1 <<<");
        node = NodeBuilder.nodeBuilder()
                .clusterName("my_elastic_client")
                .node();
        
        System.out.println(">>> 2 <<<");
        client = node.client();
        System.out.println(">>> 3 <<<");
        System.out.println(">>> STARTING ELASTIC CLIENT <<<" + client.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void stopClient() {
        node.close();
    }
    
    public Client getInstance() {
        return client;
    }

    public Node getNode() {
        return node;
    }

    public Client getClient() {
        return client;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setClient(Client client) {
        this.client = client;
    }
} */