/*package com.imathresearch.kostal.servlet;

import javax.servlet.ServletContext;

import com.imathresearch.kostal.elasticclient.ElasticClient;


public class RunEsOnDeployment implements Runnable {

    ServletContext myServletContext; // in case you need the servelet context

    RunEsOnDeployment(ServletContext sc) {
           myServletContext = sc;
    }

    public void run() {
       new ElasticClient();
       Thread th = new Thread();
        try {
            th.start();
            th.sleep(1000 * 30);
            System.out.println("Cleanup ES.");

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted! " + e);
        }
    }
}*/