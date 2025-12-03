package com.banking.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================");
        System.out.println("ONLINE BANKING APPLICATION STARTED");
        System.out.println("=================================");
        System.err.println("ONLINE BANKING APPLICATION STARTED (STDERR)");

        // Initialize Database
        com.banking.util.DatabaseInitializer.initialize();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ONLINE BANKING APPLICATION STOPPED");
    }
}
