package com.example.absdict;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class DictionaryScreen extends BorderPane {
    // ListViews to display search results and favorite words
    private ListView<HBox> searchResults;
    private ListView<String> favoriteWords;

    // Store the current username and the current search word
    private String username;
    private String currentSearchWord;

    // Database connection to store favorite words
    private Connection connection;

    // Constructor to initialize the screen with the user's username
    public DictionaryScreen(String username) {
        this.username = username;

        // Set up the database connection to store favorites
        setupDatabase();

        // Create and configure the top bar (search bar, buttons, and user info)
        VBox topBarContainer = new VBox();
        topBarContainer.setPadding(new Insets(10, 20, 10, 20));
        topBarContainer.setSpacing(10);

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #4CAF50;");

        // Search field setup
        TextField searchField = new TextField();
        searchField.setPromptText("Search a word...");
        searchField.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-padding: 5px;");
        searchField.setPrefWidth(250);

        // Search button setup
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-weight: bold;");
        searchButton.setPrefWidth(100);

        // User label setup to greet the user
        Label userLabel = new Label("Welcome, " + username);
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setStyle("-fx-text-fill: white;");

        // Favorites button setup
        Button favoritesButton = new Button("Favorites");
        favoritesButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-weight: bold;");
        favoritesButton.setPrefWidth(100);

        // Add all elements to the top bar
        topBar.getChildren().addAll(favoritesButton, searchField, searchButton, userLabel);

        // Label to display the searched word
        Label searchedWordLabel = new Label();
        searchedWordLabel.setFont(Font.font("Arial", 16));
        searchedWordLabel.setStyle("-fx-text-fill: #000000;");

        // Add the top bar and the searched word label to the container
        topBarContainer.getChildren().addAll(topBar, searchedWordLabel);

        // Set the top container as the top of the BorderPane
        setTop(topBarContainer);

        // Initialize the search results list view
        searchResults = new ListView<>();
        searchResults.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

        // Set action on the search button to trigger a word search
        searchButton.setOnAction(e -> {
            currentSearchWord = searchField.getText().trim(); // Store the current search word
            if (!currentSearchWord.isEmpty()) {
                performSearch(currentSearchWord); // Trigger the search
            }
        });

        // Set the search results list view as the center of the BorderPane
        setCenter(searchResults);

        // Initialize the favorites list view
        favoriteWords = new ListView<>();
        favoriteWords.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

        // Set action for the favorites button to toggle between views
        favoritesButton.setOnAction(e -> toggleFavoritesView());
    }

    // Set up the SQLite database to store user favorites
    private void setupDatabase() {
        try {
            // Establish a connection to the SQLite database (favorites.db)
            connection = DriverManager.getConnection("jdbc:sqlite:favorites.db");
            Statement stmt = connection.createStatement();

            // Create the 'favorites' table if it doesn't already exist
            stmt.execute("CREATE TABLE IF NOT EXISTS favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "word TEXT NOT NULL, " +
                    "context TEXT NOT NULL, " +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(username, word))");
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database connection errors
        }
    }

    // Perform the search for the word and display results
    private void performSearch(String query) {
        searchResults.getItems().clear(); // Clear previous search results

        // Call the search_db method to get the definition of the word
        String definition = search_db(query);

        if (definition != null && !definition.isEmpty()) {
            // Create a result box to display the word's definition
            HBox resultBox = new HBox(10);
            resultBox.setPadding(new Insets(10));
            resultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

            Label definitionLabel = new Label(definition);
            definitionLabel.setFont(Font.font("Arial", 14));
            definitionLabel.setStyle("-fx-text-fill: #333333;");

            // Button to allow adding the word to favorites
            Button favoriteButton = new Button("Add to Favorites");
            favoriteButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5px;");

            // Add functionality to the 'Add to Favorites' button
            favoriteButton.setOnAction(e -> {
                saveToFavorites(query, definition, favoriteButton); // Save the word to favorites
            });

            // Add the definition label and the button to the result box
            resultBox.getChildren().addAll(definitionLabel, favoriteButton);

            // Add the result box to the search results list
            searchResults.getItems().add(resultBox);
        } else {
            // Display a message if no results are found
            Label noResultLabel = new Label("No results found for: " + query);
            noResultLabel.setFont(Font.font("Arial", 14));
            noResultLabel.setStyle("-fx-text-fill: #ff0000;");

            HBox noResultBox = new HBox(noResultLabel);
            noResultBox.setPadding(new Insets(10));
            noResultBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5px; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

            searchResults.getItems().add(noResultBox);
        }
    }

    // Save a word to the favorites list in the database
    private void saveToFavorites(String word, String context, Button favoriteButton) {
        new Thread(() -> {
            try {
                // Insert the word into the favorites table if it doesn't already exist
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT OR IGNORE INTO favorites (username, word, context) VALUES (?, ?, ?)");
                stmt.setString(1, username);
                stmt.setString(2, word);
                stmt.setString(3, context);
                stmt.executeUpdate();

                // Update the UI to indicate the word has been favorited
                Platform.runLater(() -> {
                    favoriteButton.setText("Favorited");
                    favoriteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px;");
                    favoriteButton.setDisable(true); // Optionally disable the button after favoriting
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    // Show an error alert if saving the word to favorites fails
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Failed to save the word to favorites.");
                    alert.show();
                });
            }
        }).start();
    }

    // Update the list of favorite words
    private void updateFavoritesList() {
        favoriteWords.getItems().clear(); // Clear current list of favorites

        try {
            // Fetch the list of favorite words from the database
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT word, context, date FROM favorites WHERE username = ? ORDER BY date DESC");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Format and display each favorite word in the list view
            while (rs.next()) {
                String word = rs.getString("word");
                String context = rs.getString("context");
                String date = rs.getString("date");

                // Format each item to display word, context, and the date added
                String formattedItem = String.format("%s\n   Context:\n   %s (Added: %s)",
                        word, context.replace("\n", "\n   "), date);

                favoriteWords.getItems().add(formattedItem);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle errors while fetching favorites
        }
    }

    // Toggle between the search results and favorites list
    private void toggleFavoritesView() {
        // If currently showing the search results, switch to favorites
        if (getCenter() == searchResults) {
            updateFavoritesList(); // Update the favorites list
            setCenter(favoriteWords); // Show favorites list
        } else {
            setCenter(searchResults); // Switch back to search results
        }
    }
    public String search_db(String word) {
        StringBuilder result = new StringBuilder();
        try {
            // Define API keys and references
            String dictionaryKey = "30178a37-7bf8-495a-8790-ce97e64b1dd0"; // Replace with your actual Dictionary API key
            String thesaurusApiKey = "vGqqiS8iq0qdNfdo07tJDw==cD30DZ1ZM5qztRyT"; // Replace with your actual Thesaurus API key
            String ref = "collegiate"; // Example dictionary reference

            // Fetch and display definitions from Dictionary API
            String dictionaryUrl = "https://dictionaryapi.com/api/v3/references/" + ref + "/json/" + word + "?key=" + dictionaryKey;
            URL dictUrl = new URL(dictionaryUrl);
            HttpURLConnection dictConnection = (HttpURLConnection) dictUrl.openConnection();
            dictConnection.setRequestMethod("GET");

            int responseCode = dictConnection.getResponseCode();
            String line;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(dictConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse and display definitions
                JSONArray jsonArray = new JSONArray(response.toString());
                if (jsonArray.length() > 0 && jsonArray.get(0) instanceof JSONObject) {
                    JSONObject definition = jsonArray.getJSONObject(0);

                    result.append("Definitions:\n");
                    if (definition.has("shortdef")) {
                        JSONArray shortDefs = definition.getJSONArray("shortdef");
                        for (int i = 0; i < shortDefs.length(); i++) {
                            result.append("- " + shortDefs.getString(i) + "\n");
                        }
                    }
                } else {
                    result.append("No definitions found.\n");
                }
            } else {
                result.append("Failed to fetch definitions. Response Code: " + responseCode + "\n");
            }

            // Fetch and display synonyms and antonyms from Thesaurus API
            String thesaurusUrl = "https://api.api-ninjas.com/v1/thesaurus?word=" + word;
            URL thesUrl = new URL(thesaurusUrl);
            HttpURLConnection thesConnection = (HttpURLConnection) thesUrl.openConnection();
            thesConnection.setRequestProperty("accept", "application/json");
            thesConnection.setRequestProperty("X-Api-Key", thesaurusApiKey);

            BufferedReader thesReader = new BufferedReader(new InputStreamReader(thesConnection.getInputStream()));
            StringBuilder thesResponse = new StringBuilder();
            while ((line = thesReader.readLine()) != null) {
                thesResponse.append(line);
            }
            thesReader.close();

            // Parse and display synonyms and antonyms
            JSONObject thesaurusResponse = new JSONObject(thesResponse.toString());

            // Synonyms (limit to 5)
            if (thesaurusResponse.has("synonyms")) {
                JSONArray synonyms = thesaurusResponse.getJSONArray("synonyms");
                if (synonyms.length() > 0) {
                    result.append("\nSynonyms:\n");
                    for (int i = 0; i < Math.min(synonyms.length(), 5); i++) {
                        result.append("- " + synonyms.getString(i) + "\n");
                    }
                }
            }

            // Antonyms (limit to 5)
            if (thesaurusResponse.has("antonyms")) {
                JSONArray antonyms = thesaurusResponse.getJSONArray("antonyms");
                if (antonyms.length() > 0) {
                    result.append("\nAntonyms:\n");
                    for (int i = 0; i < Math.min(antonyms.length(), 5); i++) {
                        result.append("- " + antonyms.getString(i) + "\n");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }    }
