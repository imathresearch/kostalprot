/*package com.imathresearch.kostal.servlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EsContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("ServletContextListener destroyed");
    }

    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println(">>>  ELASTIC SEARCH  <<<");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new RunEsOnDeployment(arg0.getServletContext()));
    }

}*/
