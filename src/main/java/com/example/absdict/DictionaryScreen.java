package com.example.absdict;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DictionaryScreen extends BorderPane {
    private ListView<HBox> searchResults;  // ListView for search results
    private ListView<String> favoriteWords; // ListView for favorite words
    private String username; // Current user's username
    public String currentSearchWord; // Store the current search word

    public DictionaryScreen(MainApp mainApp, String username) {
        this.username = username;

        // Top bar with search field and username
        VBox topBarContainer = new VBox();
        topBarContainer.setPadding(new Insets(10, 20, 10, 20));
        topBarContainer.setSpacing(10);

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #4CAF50;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search a word...");
        searchField.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-padding: 5px;");
        searchField.setPrefWidth(250);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-weight: bold;");
        searchButton.setPrefWidth(100);

        Label userLabel = new Label("Welcome, " + username);
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setStyle("-fx-text-fill: white;");

        Button favoritesButton = new Button("Favorites");
        favoritesButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-weight: bold;");
        favoritesButton.setPrefWidth(100);

        topBar.getChildren().addAll(favoritesButton, searchField, searchButton, userLabel);

        // Label to display the searched word
        Label searchedWordLabel = new Label();
        searchedWordLabel.setFont(Font.font("Arial", 16));
        searchedWordLabel.setStyle("-fx-text-fill: #000000;");

        topBarContainer.getChildren().addAll(topBar, searchedWordLabel);

        setTop(topBarContainer);

        // Search results list
        searchResults = new ListView<>();
        searchResults.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

        searchButton.setOnAction(e -> {
            currentSearchWord = searchField.getText(); // Save the search word
            performSearch(currentSearchWord);         // Perform the search
        });

        setCenter(searchResults);

        // Favorites list
        favoriteWords = new ListView<>();
        favoriteWords.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");
        favoritesButton.setOnAction(e -> toggleFavoritesView());
    }

    private void performSearch(String query) {
        searchResults.getItems().clear(); // Clear the previous results

        // Call search_db to get the word's meaning
        String definition = search_db(query);

        if (definition != null) {
            // Create the result UI
            HBox resultBox = new HBox(10);
            resultBox.setPadding(new Insets(10));
            resultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

            Label definitionLabel = new Label(definition);
            definitionLabel.setFont(Font.font("Arial", 14));
            definitionLabel.setStyle("-fx-text-fill: #333333;");

            resultBox.getChildren().add(definitionLabel);
            searchResults.getItems().add(resultBox); // Add the result box to the list view
        } else {
            // If no results found, show a message
            HBox noResultBox = new HBox();
            noResultBox.setPadding(new Insets(10));
            noResultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

            Label noResultLabel = new Label("No results found for: " + query);
            noResultLabel.setFont(Font.font("Arial", 14));
            noResultLabel.setStyle("-fx-text-fill: #ff0000;");

            noResultBox.getChildren().add(noResultLabel);
            searchResults.getItems().add(noResultBox); // Add the HBox to the list view
        }
    }

    private void addWordToFavorites(String word) {
        if (!favoriteWords.getItems().contains(word)) {
            favoriteWords.getItems().add(word);
        }
    }

    private void toggleFavoritesView() {
        if (getCenter() == searchResults) {
            setCenter(favoriteWords);
        } else {
            setCenter(searchResults);
        }
    }


    public static String search_db(String wordToSearch) {

        String url = "jdbc:sqlite:dict.db";

        // SQL query to fetch the word's definition
        String sql = "SELECT definition FROM words WHERE LOWER(word) = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the query parameter (convert input to lowercase for case-insensitive match)
            pstmt.setString(1, wordToSearch.toLowerCase());

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // Return the definition if the word is found
            if (rs.next()) {
                return rs.getString("definition");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print any exception details
        }

        return null; // Return null if no result is found
    }



}

