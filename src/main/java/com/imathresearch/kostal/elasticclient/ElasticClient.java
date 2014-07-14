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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;

import org.json.JSONObject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ElasticClient {
    
    private static PstReader reader;
    private static String urlBase = "http://localhost:8080/kostal/node";
    
    
    public static void main (String[] args) throws ElasticsearchException, IOException
    {
        
        Map<String, List<Map<String, Object>>> contentList = ElasticClient.getFolderContent();
        
        try {
            for (String folderName : contentList.keySet()) {
                for (Map<String, Object> map : contentList.get(folderName)) {
                    sendRequest("POST", "GPO", "pretty", new JSONObject(map).toString());
                }
            }
            
        } catch (Exception e) {
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
    
    public static void sendRequest(String method, String action, String urlParams, String payload) throws Exception {

        String urlApp = "/pst/" + action;
        String url = urlBase + urlApp + "?" + urlParams;
        
        int code = esConnection(method, new URL(url), payload);
    }
    
    private static void loadPstFiles() {
        try {
            reader = new PstReader("GPO.pst");
            
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

