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

public class RegistrationScreen extends GridPane {
    private MainApp mainApp;

    public RegistrationScreen(MainApp mainApp) {
        this.mainApp = mainApp;

        // Set padding, gaps, and background color for a larger window
        setPadding(new Insets(40));  // Increased padding for a larger layout
        setVgap(20);  // Increased vertical gap between elements
        setHgap(30);  // Increased horizontal gap between elements
        setStyle("-fx-background-color: #f4f4f9;");

        // Title styling
        Text title = new Text("Create Account");
        title.setFont(Font.font("Arial", 28));  // Increased font size for better visibility
        title.setFill(Color.web("#3A3A3A"));

        // Email input field
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", 16));  // Slightly larger font for labels
        TextField emailField = new TextField();
        emailField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");  // Larger padding
        emailField.setPrefWidth(300);  // Increased width for input fields

        // Username input field
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", 16));  // Slightly larger font for labels
        TextField userField = new TextField();
        userField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");  // Larger padding
        userField.setPrefWidth(300);  // Increased width for input fields

        // Password input field
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", 16));  // Slightly larger font for labels
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");  // Larger padding
        passField.setPrefWidth(300);  // Increased width for input fields

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;");  // Larger button font
        registerButton.setPrefWidth(300);  // Increased width for the button
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = userField.getText();
            String password = passField.getText();
            // Here you would save the user to a database or authentication system
            mainApp.showDictionaryScreen(username);
        });

        // Add components to the GridPane with alignment
        add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);

        add(emailLabel, 0, 1);
        add(emailField, 1, 1);

        add(userLabel, 0, 2);
        add(userField, 1, 2);

        add(passLabel, 0, 3);
        add(passField, 1, 3);

        add(registerButton, 1, 4);

        // Align the button to the center
        GridPane.setHalignment(registerButton, HPos.CENTER);

        // Center all content in the middle of the screen
        setAlignment(Pos.CENTER);

        // Optional: Align labels to the right if you prefer
        GridPane.setHalignment(emailLabel, HPos.RIGHT);
        GridPane.setHalignment(userLabel, HPos.RIGHT);
        GridPane.setHalignment(passLabel, HPos.RIGHT);
    }
}

//package com.example.absdict;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.geometry.HPos;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.scene.paint.Color;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class RegistrationScreen extends GridPane {
//    private MainApp mainApp;
//
//    // Database file path
//    private static final String DATABASE_URL = "jdbc:sqlite:users.db";
//
//    public RegistrationScreen(MainApp mainApp) {
//        this.mainApp = mainApp;
//
//        // Set padding, gaps, and background color
//        setPadding(new Insets(40));
//        setVgap(20);
//        setHgap(30);
//        setStyle("-fx-background-color: #f4f4f9;");
//
//        // Title styling
//        Text title = new Text("Create Account");
//        title.setFont(Font.font("Arial", 28));
//        title.setFill(Color.web("#3A3A3A"));
//
//        // Email input field
//        Label emailLabel = new Label("Email:");
//        emailLabel.setFont(Font.font("Arial", 16));
//        TextField emailField = new TextField();
//        emailField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
//        emailField.setPrefWidth(300);
//
//        // Username input field
//        Label userLabel = new Label("Username:");
//        userLabel.setFont(Font.font("Arial", 16));
//        TextField userField = new TextField();
//        userField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
//        userField.setPrefWidth(300);
//
//        // Password input field
//        Label passLabel = new Label("Password:");
//        passLabel.setFont(Font.font("Arial", 16));
//        PasswordField passField = new PasswordField();
//        passField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;");
//        passField.setPrefWidth(300);
//
//        // Error message label
//        Label errorMessage = new Label();
//        errorMessage.setTextFill(Color.RED);
//        errorMessage.setVisible(false);
//
//        // Register button
//        Button registerButton = new Button("Register");
//        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;");
//        registerButton.setPrefWidth(300);
//        registerButton.setOnAction(e -> {
//            String email = emailField.getText().trim();
//            String username = userField.getText().trim();
//            String password = passField.getText().trim();
//
//            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
//                errorMessage.setText("All fields are required.");
//                errorMessage.setVisible(true);
//            } else if (!saveUserToDatabase(email, username, password)) {
//                errorMessage.setText("Error: Could not save user to database.");
//                errorMessage.setVisible(true);
//            } else {
//                errorMessage.setVisible(false);
//                mainApp.showDictionaryScreen(username);
//            }
//        });
//
//        // Add components to the GridPane with alignment
//        add(title, 0, 0, 2, 1);
//        GridPane.setHalignment(title, HPos.CENTER);
//
//        add(emailLabel, 0, 1);
//        add(emailField, 1, 1);
//
//        add(userLabel, 0, 2);
//        add(userField, 1, 2);
//
//        add(passLabel, 0, 3);
//        add(passField, 1, 3);
//
//        add(errorMessage, 1, 4);
//
//        add(registerButton, 1, 5);
//        GridPane.setHalignment(registerButton, HPos.CENTER);
//
//        // Center all content in the middle of the screen
//        setAlignment(Pos.CENTER);
//
//        // Align labels to the right
//        GridPane.setHalignment(emailLabel, HPos.RIGHT);
//        GridPane.setHalignment(userLabel, HPos.RIGHT);
//        GridPane.setHalignment(passLabel, HPos.RIGHT);
//
//        // Create the database table if it doesn't exist
//        createDatabaseTable();
//    }
//
//    // Create the database table
//    private void createDatabaseTable() {
//        String createTableSQL = """
//                CREATE TABLE IF NOT EXISTS users (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    email TEXT NOT NULL UNIQUE,
//                    username TEXT NOT NULL UNIQUE,
//                    password TEXT NOT NULL
//                );
//                """;
//
//        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
//             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
//            stmt.execute();
//        } catch (SQLException e) {
//            System.out.println("Error creating database table: " + e.getMessage());
//        }
//    }
//
//    // Save user details to the database
//    private boolean saveUserToDatabase(String email, String username, String password) {
//        String insertSQL = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
//
//        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
//             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
//            stmt.setString(1, email);
//            stmt.setString(2, username);
//            stmt.setString(3, password);
//            stmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Error saving user to database: " + e.getMessage());
//            return false;
//        }
//    }
//}
