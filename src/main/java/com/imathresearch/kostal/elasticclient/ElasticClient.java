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

import com.imathresearch.kostal.readers.PstReader;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

//import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.imathresearch.kostal.readers.PstMessageParser.emptyField;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ElasticClient {
//    private static Logger logger = Logger.getLogger(ElasticClient.class);
    
    private static PstReader reader;
    private static String urlBase = "http://localhost:8080/kostal/node";
    
    
    public static void main (String[] args) throws ElasticsearchException, IOException
    {
        
        Map<String, List<Map<String, Object>>> contentList = ElasticClient.getFolderContent();
        
        try {
            for (String folderName : contentList.keySet()) {
                for (Map<String, Object> map : contentList.get(folderName)) {
                    sendRequest("POST", "Obra", "pretty", new JSONObject(map).toString());
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static Response esConnection(String method, URL urlObj, String payload) throws IOException, URISyntaxException {
        
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        
        //add request header
        con.setDoOutput(true);
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-charset", "UTF-8");
        
        if (payload != null) {
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
        }
        
//        logger.info("Sending request to URL : " + urlObj.toURI().toString());
//        logger.info("ContentMethod : " + con.getRequestMethod());
//        logger.info("Response Code : " + con.getResponseCode());
//        logger.info("payload : " + payload);
//        logger.info("Content : " + con.getResponseMessage());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        return Response
                .status(con.getResponseCode())
                .entity(response)
                .build();
    }
    
    public static Response sendRequest(String method, String action, String urlParams, String payload) throws Exception {

        String urlApp = "/pst/" + action;
        String url = urlBase + urlApp + "?" + urlParams;
        
        return esConnection(method, new URL(url), payload);
        
    }
    
    public static JSONArray retrieveThread(String threadIndex, String topic) throws Exception {
        
        JSONArray jsonArray = new JSONArray();
        String query = "";
        String payload = "";
        if (emptyField.equals(threadIndex)) {
            query = "threadTopic:\"" + topic + "\" OR conversationTopic:\"" + topic + "\"";
            payload = searchPayload(query);
        } else {
            query = "threadIndex:" + threadIndex.substring(0, 15) + "*";
            payload = searchPayload(query);
        }

        Response resp = sendRequest("GET", "_search", "", payload);
        
        XContentBuilderString entity = new XContentBuilderString(resp.getEntity().toString());
        JSONObject jsonEntity = new JSONObject(entity.camelCase().getValue());
        
        jsonArray = jsonEntity.getJSONObject("hits").getJSONArray("hits");
        jsonArray = sortDateJsonArray(jsonArray, "clientSubmitTime", "asc");
        return jsonArray;
    }
    
    private static JSONArray sortDateJsonArray(JSONArray jsonArray, String field, String order) {
        Map<String, Date> dateMap = new HashMap<String, Date>();
        Map<String, JSONObject> jsonMap = new HashMap<String, JSONObject>();
        
        JSONArray jsonArrayOrdered = new JSONArray();
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = new JSONObject(jsonArray.get(i).toString());
            JSONObject jsonSource = json.getJSONObject("Source");
            Date date = new Date();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                date = dateFormat.parse(jsonSource.get(field).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dateMap.put(json.get("Id").toString(), date);
            jsonMap.put(json.get("Id").toString(), new JSONObject("{\"Source\":" + jsonSource.toString() + "}"));
        }
        dateMap = sortByComparator(dateMap);
        for (String key : dateMap.keySet()) {
            jsonArrayOrdered.put(jsonMap.get(key));
        }
        
        return jsonArrayOrdered;
    }
    
    public static String searchPayload(String query) {
        String sortQuery = "";
        String contentQuery = "";
        String payload = "";
        try {
            sortQuery = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("clientSubmitTime")
                    .field("order", "asc")
                    .endObject()
                    .endObject()
                    .string();
            
          contentQuery = XContentFactory.jsonBuilder()
                      .startObject()
                              .startObject("query_string")
                                  .field("query", query)
                              .endObject()
                      .endObject()
                      .string();
            
          JSONObject payload2 = new JSONObject();
          //payload2.put("sort", new JSONObject(sortQuery));
          payload2.put("query", new JSONObject(contentQuery));
          payload = payload2.toString();
          
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return payload;
    }
    
    private static Map sortByComparator(Map unsortMap) {
        
        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                        return ((Comparable) ((Map.Entry) (o1)).getValue())
                               .compareTo(((Map.Entry) (o2)).getValue());
                }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    private static void loadPstFiles() {
        try {
            reader = new PstReader("Obra.pst");
            
            assertTrue(reader.getFile().exists());
            assertNotNull(reader.getPstFile());
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    private static Map<String, List<Map<String, Object>>> getFolderContent() {
        loadPstFiles();
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
    
}

