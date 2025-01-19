package com.hahn.erms;

import com.hahn.erms.ui.swing.mvc.controller.MainController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.awt.EventQueue;
@EnableJpaAuditing
@SpringBootApplication
public class ErmsSystemApplication {

    public static void main(String[] args) {
        // Disable headless mode for Swing application
        System.setProperty("java.awt.headless", "false");

        // Configure and start Spring application
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ErmsSystemApplication.class);
        builder.headless(false)
                .properties("spring.main.allow-bean-definition-overriding=true");

        ConfigurableApplicationContext context = builder.run(args);

        // Initialize Swing UI on EDT
        EventQueue.invokeLater(() -> {
            try {
                MainController mainController = context.getBean(MainController.class);
                mainController.initialize();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}