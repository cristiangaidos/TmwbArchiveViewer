/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ubs.tmwbarchiveviewer;

/**
 *
 * @author CristianGaidos
 */
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import javafx.concurrent.Worker;

public class SwingBrowser extends JFrame {

    private final JFXPanel jfxPanel = new JFXPanel();

    public SwingBrowser() {
        setTitle("Swing + JavaScript Integration");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the JavaFX panel to your Swing layout
        add(jfxPanel, BorderLayout.CENTER);

        // JavaFX components must be initialized on the JavaFX Application Thread
        Platform.runLater(this::createScene);
    }

    private void createScene() {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine(); // Use one consistent variable name

        // 1. Only load the actual file you need
        engine.load(getClass().getResource("/graphView.html").toExternalForm());
        
        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                System.out.println("JS CONSOLE: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });
        
        // 2. Set up the listener BEFORE or right after loading
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {

                // Your fixed JSON string
                String jsonGraphData = "{\"nodes\":[{\"type\":\"condition\",\"longDescription\":\"\",\"dimensionname\":\"safekeepingAccMain\",\"id\":\"e7d9875e-464a-4149-bf34-4ee2cba30d5f\",\"text\":\"Account Main\",\"h\":110,\"w\":180}]}";
                String safeJson = jsonGraphData.replace("\\", "\\\\").replace("\"", "\\\"");
                
                try {
                    // 3. Call the function defined in graphView.html
                    engine.executeScript("renderGraph('" + safeJson + "')");
                } catch (Exception e) {
                    System.err.println("JS Execution failed: " + e.getMessage());
                }
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Failed to load graphView.html. Check the file path!");
            }
        });

        jfxPanel.setScene(new Scene(webView));
    }

    public static void main(String[] args) {
        // Swing UI must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SwingBrowser browser = new SwingBrowser();
            browser.setVisible(true);
        });
    }
}
