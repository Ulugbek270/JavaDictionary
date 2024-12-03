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
    private MainApp mainApp; // Reference to the main application to switch screens

    // Constructor for the RegistrationScreen class
    public RegistrationScreen(MainApp mainApp) {
        this.mainApp = mainApp; // Store the main application reference

        // Set padding, gaps, and background color for the layout
        setPadding(new Insets(40)); // Padding around the entire grid
        setVgap(20); // Vertical gap between rows
        setHgap(30); // Horizontal gap between columns
        setStyle("-fx-background-color: #f4f4f9;"); // Set the background color

        // Title styling
        Text title = new Text("Create Account"); // Create a text label for the title
        title.setFont(Font.font("Arial", 28)); // Set font size and style for the title
        title.setFill(Color.web("#3A3A3A")); // Set the color for the title text

        // Email input field
        Label emailLabel = new Label("Email:"); // Label for email input
        emailLabel.setFont(Font.font("Arial", 16)); // Set font for the label
        TextField emailField = new TextField(); // Text field for entering the email
        emailField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;"); // Style for the input field
        emailField.setPrefWidth(300); // Set the preferred width of the field

        // Username input field
        Label userLabel = new Label("Username:"); // Label for username input
        userLabel.setFont(Font.font("Arial", 16)); // Set font for the label
        TextField userField = new TextField(); // Text field for entering the username
        userField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;"); // Style for the input field
        userField.setPrefWidth(300); // Set the preferred width of the field

        // Password input field
        Label passLabel = new Label("Password:"); // Label for password input
        passLabel.setFont(Font.font("Arial", 16)); // Set font for the label
        PasswordField passField = new PasswordField(); // Password field to hide user input
        passField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;"); // Style for the input field
        passField.setPrefWidth(300); // Set the preferred width of the field

        // Register button to submit the form
        Button registerButton = new Button("Register"); // Create the Register button
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;"); // Style the button
        registerButton.setPrefWidth(300); // Set the preferred width of the button
        registerButton.setOnAction(e -> {
            String email = emailField.getText(); // Get the text from email field
            String username = userField.getText(); // Get the text from username field
            String password = passField.getText(); // Get the text from password field

            // Check if any field is empty
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required."); // Show error message in console
            } else {
                // Save the user in the database and check if registration is successful
                if (saveUser(email, username, password)) {
                    System.out.println("User registered successfully."); // Show success message
                    // Immediately log in and switch to the main screen
                    mainApp.showDictionaryScreen(username);
                } else {
                    System.out.println("Error: Registration failed."); // Show error message if registration fails
                }
            }
        });

        // Login button to switch to the login screen
        Button loginButton = new Button("Already have an account? Log In"); // Create the login button
        loginButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;"); // Style the button
        loginButton.setPrefWidth(300); // Set the preferred width of the button
        loginButton.setOnAction(e -> mainApp.showLoginScreen()); // Switch to login screen when clicked

        // Add components to the GridPane at specific locations
        add(title, 0, 0, 2, 1); // Add title, spanning 2 columns
        GridPane.setHalignment(title, HPos.CENTER); // Center the title

        add(emailLabel, 0, 1); // Add email label
        add(emailField, 1, 1); // Add email input field

        add(userLabel, 0, 2); // Add username label
        add(userField, 1, 2); // Add username input field

        add(passLabel, 0, 3); // Add password label
        add(passField, 1, 3); // Add password input field

        add(registerButton, 1, 4); // Add register button
        add(loginButton, 1, 5); // Add login button

        // Center the content in the GridPane
        setAlignment(Pos.CENTER);
        GridPane.setHalignment(emailLabel, HPos.RIGHT); // Align email label to the right
        GridPane.setHalignment(userLabel, HPos.RIGHT); // Align username label to the right
        GridPane.setHalignment(passLabel, HPos.RIGHT); // Align password label to the right
    }

    // Method to save user information to the database
    private boolean saveUser(String email, String username, String password) {
        String dbUrl = "jdbc:sqlite:users.db"; // Database URL, SQLite database file

        // SQL query to create the users table if it doesn't already exist
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL,
                username TEXT NOT NULL,
                password TEXT NOT NULL
            );
            """;

        // SQL query to insert a new user into the database
        String insertUserQuery = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl)) { // Establish connection to the database
            // Log connection info for debugging
            System.out.println("Connected to SQLite database: " + dbUrl);

            // Create the table if it doesn't exist
            try (PreparedStatement createStmt = conn.prepareStatement(createTableQuery)) {
                createStmt.execute(); // Execute the statement to create the table
            }

            // Insert the user data into the table
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, email); // Set email parameter
                insertStmt.setString(2, username); // Set username parameter
                insertStmt.setString(3, password); // Set password parameter
                insertStmt.executeUpdate(); // Execute the insert statement
            }

            return true; // Return true if the user was successfully saved
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details if there's a database issue
            System.out.println("Error accessing database: " + dbUrl); // Log the error
            return false; // Return false if there was an error
        }
    }
}



