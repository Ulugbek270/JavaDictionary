package com.example.absdict;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage; // Main window of the application
    private String username; // Stores the username of the logged-in user

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Initialize the primary window
        showRegistrationScreen();  // Show the registration screen by default
    }

    // Show the registration screen where users can sign up
    private void showRegistrationScreen() {
        RegistrationScreen registrationScreen = new RegistrationScreen(this); // Create registration screen
        Scene scene = new Scene(registrationScreen, 700, 500); // Create scene for registration screen with size
        primaryStage.setScene(scene); // Set the scene to primary window
        primaryStage.setTitle("Dictionary Registration"); // Set title for the registration window
        primaryStage.show(); // Show the registration window
    }

    // Show the login screen where users can log in
    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);  // Create login screen
        Scene scene = new Scene(loginScreen, 700, 500); // Create scene for login screen with size
        primaryStage.setScene(scene); // Set the scene to primary window
        primaryStage.setTitle("Dictionary Login"); // Set title for the login window
    }

    // Show the dictionary screen after the user logs in
    public void showDictionaryScreen(String username) {
        this.username = username;
        DictionaryScreen dictionaryScreen = new DictionaryScreen( username);
        Scene scene = new Scene(dictionaryScreen, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gonzo Dictionary");
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
