package com.hahn.erms;

import com.hahn.erms.ui.swing.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class EmployeeRecordsManagementSystemApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = new SpringApplicationBuilder(
                EmployeeRecordsManagementSystemApplication.class)
                .headless(false)
                .run(args);

        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });
    }
}