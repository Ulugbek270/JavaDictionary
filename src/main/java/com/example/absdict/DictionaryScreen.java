
package com.example.absdict;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    // Function to fetch word details (definitions, synonyms, and antonyms)
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
    }
}
