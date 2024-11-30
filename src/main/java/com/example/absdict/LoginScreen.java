package com.example.absdict;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends GridPane {
    private MainApp mainApp;

    public LoginScreen(MainApp mainApp) {
        this.mainApp = mainApp;

        // Set padding, gaps, and background color
        setPadding(new Insets(40));
        setVgap(20);
        setHgap(30);
        setStyle("-fx-background-color: #f4f4f9;");

        // Title styling
        Text title = new Text("Login");
        title.setFont(Font.font("Arial", 28));
        title.setFill(Color.web("#3A3A3A"));

        // Username input field
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", 16));
        TextField userField = new TextField();
        userField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
        userField.setPrefWidth(300);

        // Password input field
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", 16));
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
        passField.setPrefWidth(300);

        // Login button
        Button loginButton = new Button("Log In");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;");
        loginButton.setPrefWidth(300);
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username and password are required.");
            } else {
                if (authenticateUser(username, password)) {
                    System.out.println("Login successful.");
                    mainApp.showDictionaryScreen(username);
                } else {
                    System.out.println("Invalid username or password.");
                }
            }
        });

        // Add components to the GridPane
        add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);

        add(userLabel, 0, 1);
        add(userField, 1, 1);

        add(passLabel, 0, 2);
        add(passField, 1, 2);

        add(loginButton, 1, 3);
        GridPane.setHalignment(loginButton, HPos.CENTER);

        // Center content
        setAlignment(Pos.CENTER);
        GridPane.setHalignment(userLabel, HPos.RIGHT);
        GridPane.setHalignment(passLabel, HPos.RIGHT);
    }

    private boolean authenticateUser(String username, String password) {
        String dbUrl = "jdbc:sqlite:users.db";

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a row is returned, credentials are valid
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Authentication failed
    }
}
