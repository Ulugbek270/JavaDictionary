package com.example.absdict;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private String username;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showRegistrationScreen();  // Показать экран регистрации по умолчанию
    }

    // Показать экран регистрации
    private void showRegistrationScreen() {
        RegistrationScreen registrationScreen = new RegistrationScreen(this);
        Scene scene = new Scene(registrationScreen, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dictionary Registration");
        primaryStage.show();
    }

    // Показать экран с логином
    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);  // Создаем экран логина
        Scene scene = new Scene(loginScreen, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dictionary Login");
    }

    // Показать основной экран словаря
    public void showDictionaryScreen(String username) {
        this.username = username;
        DictionaryScreen dictionaryScreen = new DictionaryScreen(this, username);
        Scene scene = new Scene(dictionaryScreen, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gonzo Dictionary");
    }

    public static void main(String[] args) {
        launch(args);
    }
}