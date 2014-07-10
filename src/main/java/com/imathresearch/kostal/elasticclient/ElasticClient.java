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

import org.elasticsearch.client.Client;
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