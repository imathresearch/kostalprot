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
import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
//import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.imathresearch.kostal.readers.PstMessageParser.emptyField;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ElasticClient {
//    private static Logger logger = Logger.getLogger(ElasticClient.class);
    
//    private static PstReader reader;
    private static String urlBase = "http://localhost:8080/kostal/node";
    private static String conAu = "a29zdGFsOi5rMHN0NGwhUHIwdA==";
    
    
    public static void main (String[] args) throws ElasticsearchException, IOException
    {
        List<File> fileList = availablePst(PstReader.pathName);
        for (File file : fileList) {
            Map<String, List<Map<String, Object>>> contentList = ElasticClient.getFolderContent(file.getName());
            
            try {
                for (String folderName : contentList.keySet()) {
                    for (Map<String, Object> map : contentList.get(folderName)) {
                        String fileWoutExt = FilenameUtils.removeExtension(file.getName());
                        sendRequest("POST",fileWoutExt, "pretty", new JSONObject(map).toString());
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        String encoding = conAu;
        con.setRequestProperty("Authorization", "Basic " + encoding);
        
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
    
    public static List<File> availablePst(String path) {
        File dir = new File(path);
        String[] extensions = new String[] { "pst", "PST" };
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
        for (File file : files) {
                System.out.println("file: " + file.getName());
        }
        return files;
    }
    
    public static JSONArray retrieveThread(String threadIndex, String topic, String pstName) throws Exception {
        
        JSONArray jsonArray = new JSONArray();
        String query = "";
        String payload = "";
        if (emptyField.equals(threadIndex)) {
            query = "conversationTopic:\"" + topic + "\"";
            payload = searchPayload(query);
//            return jsonArray;
        } else {
            query = "threadIndex:" + threadIndex.substring(0, 15).replace("/", "\\/") + "*";
            payload = searchPayload(query);
        }

        pstName = (pstName == null) ? "" : pstName;
        Response resp = sendRequest("GET", pstName + "/_search", "size=1000", payload);
        
        JSONObject jsonEntity = new JSONObject(resp.getEntity().toString());
        
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
            JSONObject jsonSource = json.getJSONObject("_source");
            Date date = new Date();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                date = dateFormat.parse(jsonSource.get(field).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dateMap.put(json.get("_id").toString(), date);
//            jsonMap.put(json.get("_id").toString(), new JSONObject("{\"_source\":" + jsonSource.toString() + "}"));
            jsonMap.put(json.get("_id").toString(),json.put("_source", jsonSource));
        }
        dateMap = sortByComparator(dateMap);
        for (String key : dateMap.keySet()) {
            jsonArrayOrdered.put(jsonMap.get(key));
        }
        
        return jsonArrayOrdered;
    }
    
    public static String searchPayload(String query) {
//        String sortQuery = "";
        String contentQuery = "";
        String payload = "";
        try {
//            sortQuery = XContentFactory.jsonBuilder()
//                    .startObject()
//                    .startObject("clientSubmitTime")
//                    .field("order", "asc")
//                    .endObject()
//                    .endObject()
//                    .string();
            
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
//          System.out.println(">>>>>>>" + payload);
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
    
//    private static PstReader loadPstFiles(String fileName) throws FileNotFoundException, PSTException, IOException {
////        try {
////            PstReader reader = new PstReader(fileName);
//            
////            assertTrue(reader.getFile().exists());
////            assertNotNull(reader.getPstFile());
////            
////        } catch (Exception e) {
////            fail(e.getMessage());
////        }
//        return reader;
//    }
    
    private static Map<String, List<Map<String, Object>>> getFolderContent(String fileName) {
        try {
//            PstReader reader = loadPstFiles(fileName);
            PstReader reader = new PstReader(fileName);
            PSTFile pstFile = reader.getPstFile();
            pstFile.getRootFolder().getContentCount();
            PSTFolder root = pstFile.getRootFolder();
            return reader.getFolderContent(root);
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }
    
    public static List<JSONArray> retrieveResultsThreaded(JSONArray json) throws Exception {
        List<JSONArray> jsonThreadedList = new ArrayList<JSONArray>();
        List<String> alreadyRetrievedIds = new ArrayList<String>();
//        System.out.println(">>>> JSON LENGTH: " + json.length());
        for (int i = 0; i < json.length(); i++) {
            JSONArray jsonArray = retrieveResultsThreaded(json.getJSONObject(i));
            List<String> notUsedIds = notUsedIds(jsonArray, alreadyRetrievedIds);
            
            JSONArray filteredArray = new JSONArray();
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject js = jsonArray.getJSONObject(j);
                for (String id : notUsedIds) {
                    if (id.equals(js.get("_id"))) {
                        filteredArray.put(js);
                    }
                }
            }
            
            if (!"[]".equals(filteredArray.toString()))
            {
                jsonThreadedList.add(filteredArray);
            }
            
            alreadyRetrievedIds.addAll(notUsedIds);
//            System.out.println(">>> _IDS: " + alreadyRetrievedIds);
        }
        return jsonThreadedList;
    }
    
    private static List<String> notUsedIds(JSONArray jsonArray, List<String> alreadyRetrievedIds) {
        List<String> notUsedIds = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            if(!alreadyRetrievedIds.contains(json.getString("_id"))) {
                notUsedIds.add(json.getString("_id"));
            }
        }
        
        return notUsedIds;
    }

    public static JSONArray retrieveResultsThreaded(JSONObject json) throws Exception {
        JSONObject jsonEntity = new JSONObject(json.toString());
 
        JSONObject jsonSource = jsonEntity.getJSONObject("_source");
        String threadIndex = jsonSource.getString("threadIndex");
        
        String topic = null;
        if (emptyField.equals(jsonSource.getString("threadTopic"))) {
            topic = jsonSource.getString("conversationTopic");
        } else {
            topic = jsonSource.getString("threadTopic");
        }
        topic = topic.replace("\"","\\\"");
        JSONArray jsonThreaded = retrieveThread(threadIndex, topic, jsonEntity.getString("_type"));
        return jsonThreaded;
    }
    
    public static List<String> esIndexMappings() {
        Response resp = null; 
        try {
           resp = sendRequest("GET", "_mapping", "", null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject entityJson = new JSONObject(resp.getEntity().toString());
        JSONObject mappingsJson = entityJson.getJSONObject("pst").getJSONObject("mappings");
        
        List<String> typeList = new ArrayList<String>();
        Iterator keys = mappingsJson.keys();
        while (keys.hasNext()) {
            String type = keys.next().toString();
            typeList.add(type);
        } 
        
        return typeList;
    }
    
    public static Response formatResults(Response resp, boolean threaded) throws Exception {
        JSONObject jsonEntity = new JSONObject(resp.getEntity().toString());
        JSONArray jsonArray = jsonEntity.getJSONObject("hits").getJSONArray("hits");
        if (threaded) {
            List<JSONArray> jsonThreadedList = ElasticClient.retrieveResultsThreaded(jsonArray);
//            System.out.println(">>>" + jsonThreadedList.toString());
            JSONArray jsonThreadsFormated = threadsFormatedOnPst(jsonThreadedList);
//            System.out.println(">>>" + jsonThreadsFormated.toString());
            resp = Response
                    .status(resp.getStatus())
                    .entity(jsonThreadsFormated.toString())
                    .build();
        }
        
        return resp;
    }


    private static JSONArray threadsFormatedOnPst(List<JSONArray> jsonThreadedList) throws IOException {
        
        List<String> pstList = esIndexMappings();
        Map<String, List<JSONObject>> pstMap = new HashMap<String, List<JSONObject>>();
        // Initialization of a map structure
        for (String pstName : pstList) {
            pstMap.put(pstName, new ArrayList<JSONObject>());
        }
        
        // Walk through the thread list
        XContentBuilder thrdsBuilder = XContentFactory.jsonBuilder();
        
        for (JSONArray thread : jsonThreadedList) { // Look into all THREADS
            String pstName = "";
            
            long thrdAttSize = 0;
            long thrdSize = 0;
           
            String messTopic = "";
            
            String firstMess = "";
            String lastMess = "";
            thrdsBuilder = XContentFactory.jsonBuilder();
            thrdsBuilder.startObject();
            for (int i = 0; i < thread.length(); i++) { // Look into MESSEAGES
                JSONObject mess = thread.getJSONObject(i);
                
                pstName = mess.getString("_type");
                JSONObject jsonSource = mess.getJSONObject("_source");
                
                messTopic = jsonSource.getString("conversationTopic");
                String messTopic2 = jsonSource.getString("threadTopic");
                Long messAttSize = jsonSource.getLong("attachmentSize");
                Long messSize = jsonSource.getLong("messageSize");
                String messTime = jsonSource.getString("clientSubmitTime");
                
                thrdAttSize = thrdAttSize + messAttSize;
                thrdSize = thrdSize + messSize;
                
//                List<JSONObject> thrList = pstMap.get(pstName);
//                System.out.println(">>>" + i);
//                System.out.println(">>>" + messTime);
                if (i == 0) {
                    firstMess = messTime;
                    lastMess = messTime;
                } else {
                    lastMess = messTime;
                }
//                System.out.println(">>>" + firstMess);
//                System.out.println(">>>" + lastMess);
//                System.out.println(">>>----");
                
//                JSONArray thrArray = thrMap.get(intId);
                
            }
            
            thrdsBuilder.field("topic", messTopic);
            thrdsBuilder.field("messNum", thread.length());
            thrdsBuilder.field("thrdSize", thrdSize);
            thrdsBuilder.field("thrdAttSize", thrdAttSize);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                Date firstDate = dateFormat.parse(firstMess);
                Date lastDate = dateFormat.parse(lastMess);
//                System.out.println(">>>" + firstDate);
//                System.out.println(">>>" + lastDate);
//                System.out.println(">>>------------");
                thrdsBuilder.field("thrdTime", lastDate.getTime() - firstDate.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            thrdsBuilder.endObject();
            String thrdsString = thrdsBuilder.string();
            pstMap.get(pstName).add(new JSONObject(thrdsString));
        }
//        System.out.println(">>>" + pstMap);
        
        XContentBuilder pstContent = XContentFactory.jsonBuilder();
//        pstContent.startObject();
        pstContent.startArray();//"_psts");
        for (String pstName : pstList) {
            pstContent.startObject();
            pstContent.field("name", pstName);
            pstContent.rawField("_thrds",pstMap.get(pstName).toString().getBytes());
            int messNum = 0;
            long messSize = 0;
            double thrdTime = 0;
            List<JSONObject> jsonThrdList = pstMap.get(pstName);
            if (!jsonThrdList.isEmpty()) {
                for (JSONObject thrd : jsonThrdList) {
                    messNum = messNum + thrd.getInt("messNum");
                    messSize = messSize + thrd.getLong("thrdSize");
                    thrdTime = thrdTime + thrd.getLong("thrdTime");
                }
                int thrdNum = jsonThrdList.size();
                double thrdSizeMean = messSize / thrdNum;
                double thrdTimeMean = thrdTime / thrdNum;
                pstContent.field("messNum", messNum);
                pstContent.field("messSize", messSize);
                pstContent.field("thrdNum", thrdNum);
                pstContent.field("thrdSizeMean", thrdSizeMean);
                pstContent.field("thrdTimeMean", thrdTimeMean);
            }
            pstContent.endObject();
        }
        pstContent.endArray();
//        pstContent.endObject();

        
        return new JSONArray(pstContent.string());
    }
    
}

