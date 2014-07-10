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

/*package com.imathresearch.kostal.elasticclient;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticClientTest {
    
    private static ElasticClient eClient;
    
    @BeforeClass
    public static void startUp() {
        eClient = new ElasticClient();
    }
    
    @AfterClass
    public static void stopDown() {
        eClient.stopClient();
    }
    
    @Test
    public void clientTest() throws ElasticsearchException, IOException {
        Client client = eClient.getClient();
        
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                                .field("user", "kimchy")
                                .field("postDate", new Date())
                                .field("message", "trying out Elasticsearch")
                            .endObject()
                          )
                .execute()
                .actionGet();
        
        System.out.println(">>>" + response.getIndex());
        
        GetResponse response2 = client.prepareGet("twitter", "tweet", "1")
                .execute()
                .actionGet();
        
        System.out.println(">>>" + response2.getSourceAsString());
    }
}*/