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


