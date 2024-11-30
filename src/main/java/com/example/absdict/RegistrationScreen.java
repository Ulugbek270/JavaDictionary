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
import java.sql.SQLException;

public class RegistrationScreen extends GridPane {
    private MainApp mainApp;

    public RegistrationScreen(MainApp mainApp) {
        this.mainApp = mainApp;

        // Set padding, gaps, and background color
        setPadding(new Insets(40));
        setVgap(20);
        setHgap(30);
        setStyle("-fx-background-color: #f4f4f9;");

        // Title styling
        Text title = new Text("Create Account");
        title.setFont(Font.font("Arial", 28));
        title.setFill(Color.web("#3A3A3A"));

        // Email input field
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", 16));
        TextField emailField = new TextField();
        emailField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
        emailField.setPrefWidth(300);

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

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;");
        registerButton.setPrefWidth(300);
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = userField.getText();
            String password = passField.getText();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
            } else {
                // Save user in the database
                if (saveUser(email, username, password)) {
                    System.out.println("User registered successfully.");
                    // Immediately log in and redirect to the main page
                    mainApp.showDictionaryScreen(username);
                } else {
                    System.out.println("Error: Registration failed.");
                }
            }
        });

        // Login button to switch to login screen
        Button loginButton = new Button("Already have an account? Log In");
        loginButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;");
        loginButton.setPrefWidth(300);
        loginButton.setOnAction(e -> mainApp.showLoginScreen());

        // Add components to the GridPane
        add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);

        add(emailLabel, 0, 1);
        add(emailField, 1, 1);

        add(userLabel, 0, 2);
        add(userField, 1, 2);

        add(passLabel, 0, 3);
        add(passField, 1, 3);

        add(registerButton, 1, 4);
        add(loginButton, 1, 5);

        // Center content
        setAlignment(Pos.CENTER);
        GridPane.setHalignment(emailLabel, HPos.RIGHT);
        GridPane.setHalignment(userLabel, HPos.RIGHT);
        GridPane.setHalignment(passLabel, HPos.RIGHT);
    }

    private boolean saveUser(String email, String username, String password) {
        String dbUrl = "jdbc:sqlite:users.db"; // Ensure this path is writable or absolute

        // SQL to create users table if it doesn't exist
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL,
                username TEXT NOT NULL,
                password TEXT NOT NULL
            );
            """;

        // SQL to insert a new user
        String insertUserQuery = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            // Log connection info
            System.out.println("Connected to SQLite database: " + dbUrl);

            try (PreparedStatement createStmt = conn.prepareStatement(createTableQuery)) {
                // Create table if not exists
                createStmt.execute();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {
                // Insert user data
                insertStmt.setString(1, email);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
            }

            return true; // Registration success
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error accessing database: " + dbUrl);
            return false; // Registration failed
        }
    }
}


