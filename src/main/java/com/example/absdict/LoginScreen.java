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
    private MainApp mainApp; // Reference to the main application to switch screens

    public LoginScreen(MainApp mainApp) {
        this.mainApp = mainApp; // Initialize mainApp

        // Set padding, gaps, and background color for the layout
        setPadding(new Insets(40)); // Padding around the screen
        setVgap(20); // Vertical gap between elements
        setHgap(30); // Horizontal gap between elements
        setStyle("-fx-background-color: #f4f4f9;"); // Background color of the layout

        // Title of the screen
        Text title = new Text("Login");
        title.setFont(Font.font("Arial", 28)); // Set font and size for title
        title.setFill(Color.web("#3A3A3A")); // Set color for the title text

        // Username input field
        Label userLabel = new Label("Username:"); // Label for username input
        userLabel.setFont(Font.font("Arial", 16)); // Set font for the label
        TextField userField = new TextField(); // Text field for username input
        userField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;"); // Style for input field
        userField.setPrefWidth(300); // Set preferred width of the input field

        // Password input field
        Label passLabel = new Label("Password:"); // Label for password input
        passLabel.setFont(Font.font("Arial", 16)); // Set font for the label
        PasswordField passField = new PasswordField(); // Password field for secure input
        passField.setStyle("-fx-border-radius: 10px; -fx-padding: 8px;"); // Style for input field
        passField.setPrefWidth(300); // Set preferred width of the input field

        // Login button
        Button loginButton = new Button("Log In"); // Button to submit login
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 10px;"); // Style for button
        loginButton.setPrefWidth(300); // Set preferred width for button

        // Button action handler for login button
        loginButton.setOnAction(e -> {
            String username = userField.getText(); // Get text from username input field
            String password = passField.getText(); // Get text from password input field

            // Check if either username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username and password are required."); // Print error to console
            } else {
                // Try to authenticate the user with the entered credentials
                if (authenticateUser(username, password)) {
                    System.out.println("Login successful."); // If successful, print success message
                    mainApp.showDictionaryScreen(username); // Show the dictionary screen
                } else {
                    System.out.println("Invalid username or password."); // If authentication fails, print error message
                }
            }
        });

        // Add components to the GridPane layout
        add(title, 0, 0, 2, 1); // Add the title to the first row (spanning 2 columns)
        GridPane.setHalignment(title, HPos.CENTER); // Center-align the title

        add(userLabel, 0, 1); // Add the username label to row 1
        add(userField, 1, 1); // Add the username input field to row 1 (second column)

        add(passLabel, 0, 2); // Add the password label to row 2
        add(passField, 1, 2); // Add the password input field to row 2 (second column)

        add(loginButton, 1, 3); // Add the login button to row 3 (second column)
        GridPane.setHalignment(loginButton, HPos.CENTER); // Center-align the login button

        // Center the entire content inside the GridPane
        setAlignment(Pos.CENTER);

        // Right-align the labels for username and password fields
        GridPane.setHalignment(userLabel, HPos.RIGHT);
        GridPane.setHalignment(passLabel, HPos.RIGHT);
    }

    // Method to authenticate the user by checking credentials in the database
    private boolean authenticateUser(String username, String password) {
        String dbUrl = "jdbc:sqlite:users.db"; // Database URL (SQLite database)

        // SQL query to select user based on username and password
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl); // Connect to the database
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username); // Set the username parameter in the query
            stmt.setString(2, password); // Set the password parameter in the query

            // Execute the query and check if any results are returned
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a row is found, return true (valid credentials)
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print any database-related errors
        }
        return false; // If no matching user is found, return false (invalid credentials)
    }
}
