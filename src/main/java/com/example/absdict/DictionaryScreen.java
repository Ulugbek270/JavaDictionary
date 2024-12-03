package com.example.absdict;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.sql.*;


public class DictionaryScreen extends BorderPane {
    private ListView<HBox> searchResults; // ListView for search results
    private ListView<String> favoriteWords; // ListView for favorite words
    private String username; // Current user's username
    private String currentSearchWord; // Store the current search word
    private Connection connection; // Database connection for favorites

    // Constructor to initialize the screen with the user's username
    public DictionaryScreen(String username) {
        this.username = username;

        // Initialize database connection
        setupDatabase();

        // Top bar with search field and username
        VBox topBarContainer = new VBox();
        topBarContainer.setPadding(new Insets(10, 20, 10, 20));
        topBarContainer.setSpacing(10);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10); // Adds spacing between elements

        topBar.setStyle("-fx-background-color: #4CAF50;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search a word...");
        searchField.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-padding: 5px;");
        searchField.setPrefWidth(250);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px;");
        searchButton.setPrefWidth(100);

        Label userLabel = new Label("Welcome, " + username);
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setStyle("-fx-text-fill: white;");

        Button favoritesButton = new Button("Favorites");
        favoritesButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-border-radius: 5px;");
        favoritesButton.setPrefWidth(100);

        topBar.getChildren().addAll(favoritesButton, searchField, searchButton, userLabel);

        Label searchedWordLabel = new Label();
        searchedWordLabel.setFont(Font.font("Arial", 16));
        searchedWordLabel.setStyle("-fx-text-fill: #000000;");

        topBarContainer.getChildren().addAll(topBar, searchedWordLabel);
        setTop(topBarContainer);

        // Search results list view
        searchResults = new ListView<>();
        searchResults.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px;");

        // Setup search button actions
        searchButton.setOnAction(e -> {
            currentSearchWord = searchField.getText().trim();
            if (!currentSearchWord.isEmpty()) {
                searchButton.setDisable(true);  // Disable the search button while searching
                performSearch(currentSearchWord, searchButton);
            }
        });

        setCenter(searchResults);

        // Favorites list view
        favoriteWords = new ListView<>();
        favoriteWords.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px;");
        favoritesButton.setOnAction(e -> toggleFavoritesView());
    }

    // Set up the SQLite database to store user favorites
    private void setupDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:favorites.db");
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "word TEXT NOT NULL, " +
                    "context TEXT NOT NULL, " +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(username, word))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Perform the search for the word and display results
    private void performSearch(String query, Button searchButton) {
        searchResults.getItems().clear(); // Clear previous results

        String definition = search_db(query); // Fetch the definition using the provided search_db method

        if (definition != null && !definition.isEmpty()) {
            HBox resultBox = new HBox(10);
            resultBox.setPadding(new Insets(10));
            resultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px;");

            Label definitionLabel = new Label(definition);
            definitionLabel.setFont(Font.font("Arial", 14));
            definitionLabel.setStyle("-fx-text-fill: #333333;");

            Button favoriteButton = new Button("Add to Favorites");
            favoriteButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px;");
            favoriteButton.setOnAction(e -> saveToFavorites(query, definition, favoriteButton));

            resultBox.getChildren().addAll(definitionLabel, favoriteButton);
            searchResults.getItems().add(resultBox);
        } else {
            HBox noResultBox = new HBox();
            noResultBox.setPadding(new Insets(10));
            noResultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px;");
            Label noResultLabel = new Label("No results found for: " + query);
            noResultBox.getChildren().add(noResultLabel);
            searchResults.getItems().add(noResultBox);
        }
        Platform.runLater(() -> searchButton.setDisable(false));  // Re-enable the button
    }

    // The provided search_db method to fetch word definition from the dict.db SQLite database
    public static String search_db(String wordToSearch) {
        String url = "jdbc:sqlite:dict.db";
        String sql = "SELECT definition FROM words WHERE LOWER(word) = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wordToSearch.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("definition");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save a word to the favorites list from the favorite button
    private void saveToFavorites(String word, String context, Button favoriteButton) {
        new Thread(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT OR IGNORE INTO favorites (username, word, context) VALUES (?, ?, ?)");
                stmt.setString(1, username);
                stmt.setString(2, word);
                stmt.setString(3, context);
                stmt.executeUpdate();

                Platform.runLater(() -> favoriteButton.setText("Added"));
                favoriteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px;");

            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Failed to save the word to favorites.");
                    alert.show();
                });
            }
        }).start();
    }

    // Toggle between the search results and favorites list
    private void updateFavoritesList() {
        favoriteWords.getItems().clear();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT word, context, date FROM favorites WHERE username = ? ORDER BY date DESC");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            int count = 1;
            while (rs.next()) {
                String word = rs.getString("word");
                String context = rs.getString("context");
                String date = rs.getString("date");

                String formattedItem = String.format(
                        "%d. %s (Added: %s)\n   Context:\n   %s",
                        count, word, date, context.replace("\n", "\n   ")
                );
                favoriteWords.getItems().add(formattedItem);
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleFavoritesView() {
        if (getCenter() == searchResults) {
            updateFavoritesList();
            setCenter(favoriteWords);
        } else {
            setCenter(searchResults);
        }
    }
}

