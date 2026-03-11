/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ubs.tmwbarchiveviewer;

/**
 *
 * @author CristianGaidos
 */
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
        WebEngine webEngine = webView.getEngine();

        // Sample HTML with JavaScript
        String html = "<html>" +
                      "<body style='background: #f0f0f0; font-family: sans-serif; text-align: center;'>" +
                      "<h1>JavaFX WebView in Swing</h1>" +
                      "<button onclick='showAlert()'>Click Me for JS Alert</button>" +
                      "<script>" +
                      "  function showAlert() { alert('Hello from JavaScript inside Swing!'); }" +
                      "</script>" +
                      "</body>" +
                      "</html>";

        webEngine.loadContent(html);
        
        WebEngine engine = webView.getEngine();
        engine.load(getClass().getResource("/graphView.html").toExternalForm());

         engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
    if (newState == Worker.State.SUCCEEDED) {
        // 1. Get your graph JSON from a local file or database
        String jsonGraphData = "{ \"nodes\": [{ \"type:\"condition\",\"longDescription\":\"\",\"dimensionname\":\"safekeepingAccMain\",\"id\":\"e7d9875e-464a-4149-bf34-4ee2cba30d5f\",\"text\":\"Account Main\",\"h\":110,\"w\":180} ] }";
        
        // 2. Inject it into the JS function we defined in step 1
        engine.executeScript("renderGraph('" + jsonGraphData + "')");
    }
});
        
        
        
        // Set the JavaFX Scene into the JFXPanel
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