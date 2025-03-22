package com.example.cw_draft1_3;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleObjectProperty;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.layout.VBox;
import java.nio.file.Paths;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Pos;

public class RapidRun extends Application {
    static boolean raceStarted = false;
    static List<Map<String, String>> horseDetails = new ArrayList<>();
    static List<Map<String, String>> selectedHorses = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Rapid Run");

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RapidRun.fxml"));
        Parent root = loader.load();

        // Load horse details from file
        String filePath = "horse_details.txt";
        loadHorseDetailsFromFile(filePath);

        // Initialize JavaFX components
        TextField userInputField = (TextField) root.lookup("#userInputField");
        Button executeButton = (Button) root.lookup("#executeButton");

        // Add event handlers for the buttons
        executeButton.setOnAction(event -> {
            String userInput = userInputField.getText().toUpperCase();
            switch (userInput) {
                case "AHD":
                    if (!raceStarted) {
                        addHorseDetails();
                    } else {
                        System.out.println("You cannot execute this option after starting the race.\n");
                    }
                    break;
                case "UHD":
                    if (!raceStarted) {
                        updateHorseDetails();
                    } else {
                        System.out.println("You cannot execute this option after starting the race.\n");
                    }
                    break;
                case "DHD":
                    if (!raceStarted) {
                        deleteHorseDetails();
                    } else {
                        System.out.println("You cannot execute this option after starting the race.\n");
                    }
                    break;
                case "VHD":
                    viewHorseDetails(horseDetails);
                    break;
                case "SHD":
                    saveHorseDetails();
                    break;
                case "SDD":
                    RaceManager.selectHorses();
                    break;
                case "WHD":
                    RaceManager.displayWinningHorses(selectedHorses);
                    break;
                case "VWH":
                    RaceManager.visualizeWinningHorses();
                    break;
                case "ESC":
                    System.out.println("Exiting...Thank You for Your Cooperation!");
                    primaryStage.close();
                    return;
                default:
                    System.out.println("Invalid command.\n");
            }
        });

        // Set up the scene and show the stage
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void loadHorseDetailsFromFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.strip().split("\\s+");
                Map<String, String> horse = new HashMap<>();
                horse.put("Horse ID", fields[0]);
                horse.put("Horse Name", fields[1]);
                horse.put("Jockey Name", fields[2]);
                horse.put("Age", fields[3]);
                horse.put("Breed", fields[4]);
                horse.put("Race Record", fields[5]);
                horse.put("Group", fields[6]);
                horse.put("Image Name", fields[7]);
                horseDetails.add(horse);
            }
            System.out.println("Horse details loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found. No horse details loaded.");
        } catch (Exception e) {
            System.out.println("An error occurred while loading horse details: " + e.getMessage());
        }
    }

    static void addHorseDetails() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Horse Details");
        dialog.setHeaderText(null);
        File[] selectedFile = {null};

        // Set up the VBox layout for the dialog
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(25));
        root.setBackground(new Background(new BackgroundFill(Color.web("#B2D2A4"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Add Horse Details:");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A4314"));
        root.getChildren().add(title);

        // Set up the GridPane layout for the input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // Create input fields
        TextField horseIdField = new TextField();
        horseIdField.setPromptText("Horse ID");
        TextField horseNameField = new TextField();
        horseNameField.setPromptText("Horse Name");
        TextField jockeyNameField = new TextField();
        jockeyNameField.setPromptText("Jockey Name");
        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        TextField breedField = new TextField();
        breedField.setPromptText("Breed");
        TextField raceRecordField = new TextField();
        raceRecordField.setPromptText("Race Record");
        TextField groupField = new TextField();
        groupField.setPromptText("Group");

        // Add input fields to the GridPane
        grid.add(new Label("Horse ID:"), 0, 0);
        grid.add(horseIdField, 1, 0);
        grid.add(new Label("Horse Name:"), 0, 1);
        grid.add(horseNameField, 1, 1);
        grid.add(new Label("Jockey Name:"), 0, 2);
        grid.add(jockeyNameField, 1, 2);
        grid.add(new Label("Age (No. of years):"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Breed:"), 0, 4);
        grid.add(breedField, 1, 4);
        grid.add(new Label("Race Record (No. of Wins/No. of Races):"), 0, 5);
        grid.add(raceRecordField, 1, 5);
        grid.add(new Label("Group:"), 0, 6);
        grid.add(groupField, 1, 6);

        // Set the font of labels to Times New Roman and increase font size
        for (Node node : grid.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setFont(Font.font("Times New Roman", 16));
            }
        }

        // Create a label for the Select Image button
        Label selectImageLabel = new Label("Select Image:");
        selectImageLabel.setFont(Font.font("Times New Roman", 16));

        // Image selection button
        Button chooseImageButton = new Button("Select Image");

        // Add label and button to the GridPane
        grid.add(selectImageLabel, 0, 7);
        grid.add(chooseImageButton, 1, 7);

        chooseImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Horse Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg")
            );
            selectedFile[0] = fileChooser.showOpenDialog(null);
            if (selectedFile[0] != null) {
                System.out.println("Selected Image: " + selectedFile[0].getName() + "\n");
            }
        });

        // Add input grid to the root VBox
        root.getChildren().add(grid);
        dialog.getDialogPane().setContent(root);

        // Add buttons to the dialog
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create a BooleanBinding to check if any field is empty
        BooleanBinding isAnyFieldEmpty = Bindings.createBooleanBinding(() ->
                        horseIdField.getText().isEmpty() ||
                                horseNameField.getText().isEmpty() ||
                                jockeyNameField.getText().isEmpty() ||
                                ageField.getText().isEmpty() ||
                                breedField.getText().isEmpty() ||
                                raceRecordField.getText().isEmpty() ||
                                groupField.getText().isEmpty(),
                horseIdField.textProperty(),
                horseNameField.textProperty(),
                jockeyNameField.textProperty(),
                ageField.textProperty(),
                breedField.textProperty(),
                raceRecordField.textProperty(),
                groupField.textProperty()
        );

        // Bind the disable property of the Add button to the BooleanBinding
        ((Button) dialog.getDialogPane().lookupButton(addButton)).disableProperty().bind(isAnyFieldEmpty);

        // Set up result converter to convert dialog result to horse map
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                Map<String, String> horse = new HashMap<>();
                horse.put("Horse ID", horseIdField.getText());
                horse.put("Horse Name", horseNameField.getText());
                horse.put("Jockey Name", jockeyNameField.getText());
                horse.put("Age", ageField.getText());
                horse.put("Breed", breedField.getText());
                horse.put("Race Record", raceRecordField.getText());
                horse.put("Group", groupField.getText().toUpperCase());
                horse.put("Image Name", selectedFile[0] != null ? selectedFile[0].getName() : "");
                return horse;
            }
            return null;
        });

        // Show the dialog and wait for user input
        Optional<Map<String, String>> result = dialog.showAndWait();

        // Process the result if the user clicked Add button
        result.ifPresent(horse -> {
            // Validate the horse ID
            String horseId = horse.get("Horse ID");
            if (!horseId.matches("\\d+")) {
                System.out.println("Error: Horse ID must contain only numbers.\n");
                return;
            }
            for (Map<String, String> existingHorse : horseDetails) {
                if (existingHorse.get("Horse ID").equals(horseId)) {
                    System.out.println("Error: Horse ID must be unique.\n");
                    return;
                }
            }

            // Validate the age
            String age = horse.get("Age");
            if (!age.matches("\\d+")) {
                System.out.println("Error: Age must contain only numbers.\n");
                return;
            }

            String raceRecord = horse.get("Race Record");
            if (!raceRecord.matches("\\d+/\\d+")) {
                System.out.println("Error: Race Record must be in the format 'No. of wins/No. of races'.\n");
                return;
            }

            // Validate the group
            String group = horse.get("Group").toUpperCase();
            if (!group.matches("[ABCD]")) {
                System.out.println("Error: Group must be 'A', 'B', 'C', or 'D'.\n");
                return;
            }

            // Add the horse to the horseDetails list
            horseDetails.add(horse);
            System.out.println("Horse details added successfully.\n");
        });
    }

    static void updateHorseDetails() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Horse Details");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the ID of the Horse to Update:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String horseId = result.get();

            // Search for the horse with the given ID
            Optional<Map<String, String>> optionalHorse = horseDetails.stream()
                    .filter(horse -> horse.get("Horse ID").equals(horseId))
                    .findFirst();

            if (optionalHorse.isPresent()) {
                Map<String, String> horse = optionalHorse.get();
                Dialog<Map<String, String>> updateDialog = new Dialog<>();
                updateDialog.setTitle("Update Horse Details");
                updateDialog.setHeaderText(null);

                // Set up the VBox layout for the dialog
                VBox root = new VBox();
                root.setSpacing(10);
                root.setPadding(new Insets(25));
                root.setBackground(new Background(new BackgroundFill(Color.web("#B2D2A4"), CornerRadii.EMPTY, Insets.EMPTY)));

                Label title = new Label("Update Horse Details:");
                title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
                title.setTextFill(Color.web("#1A4314"));
                root.getChildren().add(title);

                // Set up the GridPane layout for the input fields
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 20, 10, 10));

                // Create input fields with current values
                TextField horseNameField = new TextField(horse.get("Horse Name"));
                TextField jockeyNameField = new TextField(horse.get("Jockey Name"));
                TextField ageField = new TextField(horse.get("Age"));
                TextField breedField = new TextField(horse.get("Breed"));
                TextField raceRecordField = new TextField(horse.get("Race Record"));
                TextField groupField = new TextField(horse.get("Group"));
                TextField imageNameField = new TextField(horse.get("Image Name"));

                // Add input fields to the GridPane
                grid.add(new Label("Horse Name:"), 0, 0);
                grid.add(horseNameField, 1, 0);
                grid.add(new Label("Jockey Name:"), 0, 1);
                grid.add(jockeyNameField, 1, 1);
                grid.add(new Label("Age:"), 0, 2);
                grid.add(ageField, 1, 2);
                grid.add(new Label("Breed:"), 0, 3);
                grid.add(breedField, 1, 3);
                grid.add(new Label("Race Record:"), 0, 4);
                grid.add(raceRecordField, 1, 4);
                grid.add(new Label("Group:"), 0, 5);
                grid.add(groupField, 1, 5);
                grid.add(new Label("Image Name:"), 0, 6);
                grid.add(imageNameField, 1, 6);

                // Set the font of labels to Times New Roman and increase font size
                for (Node node : grid.getChildren()) {
                    if (node instanceof Label) {
                        Label label = (Label) node;
                        label.setFont(Font.font("Times New Roman", 16)); // Adjust font size as needed
                    }
                }

                root.getChildren().add(grid);
                updateDialog.getDialogPane().setContent(root);

                // Add buttons to the dialog
                ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                updateDialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

                // Set up result converter to convert dialog result to horse map
                updateDialog.setResultConverter(buttonType -> {
                    if (buttonType == updateButton) {
                        horse.put("Horse Name", horseNameField.getText());
                        horse.put("Jockey Name", jockeyNameField.getText());
                        horse.put("Age", ageField.getText());
                        horse.put("Breed", breedField.getText());
                        horse.put("Race Record", raceRecordField.getText());
                        horse.put("Group", groupField.getText().toUpperCase());
                        horse.put("Image Name", imageNameField.getText());
                        return horse;
                    }
                    return null;
                });

                // Show the update dialog and wait for user input
                Optional<Map<String, String>> updateResult = updateDialog.showAndWait();

                // Process the result if the user clicked Update button
                updateResult.ifPresent(updatedHorse -> {
                    // Validate the age
                    String age = horse.get("Age");
                    if (!age.matches("\\d+")) {
                        System.out.println("Error: Age must contain only numbers.\n");
                        return;
                    }

                    String raceRecord = horse.get("Race Record");
                    if (!raceRecord.matches("\\d+/\\d+")) {
                        System.out.println("Error: Race Record must be in the format 'No. of wins/No. of races'.\n");
                        return;
                    }

                    // Validate the group
                    String group = horse.get("Group").toUpperCase(); // Convert to uppercase for case-insensitive matching
                    if (!group.matches("[ABCD]")) {
                        System.out.println("Error: Group must be 'A', 'B', 'C', or 'D'.\n");
                        return;
                    }
                    saveHorseDetails();
                    System.out.println("Horse details updated successfully.\n");
                });
            } else {
                System.out.println("Horse with ID " + horseId + " does not exist.\n");
            }
        }
    }

    static void deleteHorseDetails() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Horse Details");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the ID of the Horse to Delete:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String horseIdToDelete = result.get();

            // Search for the horse with the given ID
            Optional<Map<String, String>> optionalHorse = horseDetails.stream()
                    .filter(horse -> horse.get("Horse ID").equals(horseIdToDelete))
                    .findFirst();

            if (optionalHorse.isPresent()) {
                Map<String, String> horse = optionalHorse.get();
                horseDetails.remove(horse);
                System.out.println("Horse details with ID " + horseIdToDelete + " deleted successfully.\n");
                saveHorseDetails();
            } else {
                System.out.println("Horse with ID " + horseIdToDelete + " does not exist.\n");
            }
        }
    }

    static void viewHorseDetails(List<Map<String, String>> horseDetails) {
        bubbleSort1(horseDetails);

        String projectRoot = System.getProperty("user.dir");

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(25));
        root.setBackground(new Background(new BackgroundFill(Color.web("#B2D2A4"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Horse Details:");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A4314"));
        root.getChildren().add(title);

        TableView<Map<String, String>> tableView = new TableView<>();
        ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
        data.addAll(horseDetails);

        String[] columnOrder = {"Image Name", "Horse ID", "Horse Name", "Jockey Name", "Age", "Breed", "Race Record", "Group"};

        for (String key : columnOrder) {
            if (key.equals("Image Name")) {
                TableColumn<Map<String, String>, ImageView> column = new TableColumn<>("Image");
                column.setCellValueFactory(param -> {
                    Map<String, String> entry = param.getValue();
                    String imageName = entry.get(key);
                    try {
                        String imagePath = Paths.get(projectRoot, imageName).toString();
                        ImageView imageView = new ImageView(new Image(new FileInputStream(imagePath)));
                        imageView.setFitHeight(40);
                        imageView.setFitWidth(50);
                        return new SimpleObjectProperty<>(imageView);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
                tableView.getColumns().add(column);
            } else {
                TableColumn<Map<String, String>, String> column = new TableColumn<>(key);
                column.setCellValueFactory(param -> {
                    Map<String, String> entry = param.getValue();
                    return entry.containsKey(key) ? new SimpleStringProperty(entry.get(key)) : new SimpleStringProperty("");
                });
                // Set custom cell factory to vertically center align text
                column.setCellFactory(tc -> new TableCell<Map<String, String>, String>() {
                    {
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                        }
                    }
                });
                tableView.getColumns().add(column);
            }
        }

        tableView.setItems(data);
        root.getChildren().add(tableView);

        Stage stage = new Stage();
        stage.setTitle("Horse Details");
        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    static void bubbleSort1(List<Map<String, String>> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Compare two adjacent horses based on their Horse ID
                int id1 = Integer.parseInt(list.get(j).get("Horse ID"));
                int id2 = Integer.parseInt(list.get(j + 1).get("Horse ID"));
                if (id1 > id2) {
                    // Swap the horses if they are in the wrong order
                    Map<String, String> temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    static void saveHorseDetails() {
        String filePath = "horse_details.txt";
        try (FileWriter writer = new FileWriter(filePath)) {
            bubbleSort2(horseDetails);

            // Write each horse's details to the file and append to outputArea
            for (Map<String, String> horse : horseDetails) {
                String imageName = horse.get("Image Name");
                String horseDetailsStr = String.format("%-10s%-20s%-20s%-10s%-20s%-20s%-10s%-50s\n",
                        horse.get("Horse ID"), horse.get("Horse Name"),
                        horse.get("Jockey Name"), horse.get("Age"),
                        horse.get("Breed"), horse.get("Race Record"),
                        horse.get("Group"), imageName);
                writer.write(horseDetailsStr);
            }
            System.out.println("Horse details saved to file: " + filePath + "\n");
        } catch (IOException e) {
            System.out.println("Error: Failed to save horse details to file: " + e.getMessage() + "\n");
        }
    }

    static void bubbleSort2(List<Map<String, String>> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (compareGroups(list.get(j), list.get(j + 1)) > 0) {
                    // Swap elements at j and j+1
                    Map<String, String> temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    static int compareGroups(Map<String, String> horse1, Map<String, String> horse2) {
        String group1 = horse1.get("Group");
        String group2 = horse2.get("Group");
        return group1.compareTo(group2);
    }
}
