package com.example.cw_draft1_3;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.*;

import static com.example.cw_draft1_3.RapidRun.horseDetails;
import static com.example.cw_draft1_3.RapidRun.selectedHorses;
import static java.util.Collections.shuffle;

public class RaceManager {
    static void selectHorses() {
        try {
            selectedHorses.clear();

            Set<String> uniqueGroups = new HashSet<>();
            for (Map<String, String> horse : horseDetails) {
                uniqueGroups.add(horse.get("Group"));
            }

            VBox root = new VBox();
            root.setSpacing(10);
            root.setPadding(new Insets(25));
            root.setBackground(new Background(new BackgroundFill(Color.web("#B2D2A4"), CornerRadii.EMPTY, Insets.EMPTY)));

            Label title = new Label("Selected Horses:");
            title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
            title.setTextFill(Color.web("#1A4314"));
            root.getChildren().add(title);

            TableView<Map<String, String>> tableView = new TableView<>();
            ObservableList<Map<String, String>> data = FXCollections.observableArrayList();

            // Define cell factory for all columns
            Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>> cellFactory = col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item);
                        setFont(Font.font("Times New Roman", 14));
                    }
                }
            };

            TableColumn<Map<String, String>, String> horseIDCol = new TableColumn<>("Horse ID");
            horseIDCol.setCellValueFactory(param -> param.getValue().containsKey("Horse ID") ? new SimpleStringProperty(param.getValue().get("Horse ID")) : new SimpleStringProperty(""));
            horseIDCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> horseNameCol = new TableColumn<>("Horse Name");
            horseNameCol.setCellValueFactory(param -> param.getValue().containsKey("Horse Name") ? new SimpleStringProperty(param.getValue().get("Horse Name")) : new SimpleStringProperty(""));
            horseNameCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> jockeyNameCol = new TableColumn<>("Jockey Name");
            jockeyNameCol.setCellValueFactory(param -> param.getValue().containsKey("Jockey Name") ? new SimpleStringProperty(param.getValue().get("Jockey Name")) : new SimpleStringProperty(""));
            jockeyNameCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> ageCol = new TableColumn<>("Age");
            ageCol.setCellValueFactory(param -> param.getValue().containsKey("Age") ? new SimpleStringProperty(param.getValue().get("Age")) : new SimpleStringProperty(""));
            ageCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> breedCol = new TableColumn<>("Breed");
            breedCol.setCellValueFactory(param -> param.getValue().containsKey("Breed") ? new SimpleStringProperty(param.getValue().get("Breed")) : new SimpleStringProperty(""));
            breedCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> raceRecordCol = new TableColumn<>("Race Record");
            raceRecordCol.setCellValueFactory(param -> param.getValue().containsKey("Race Record") ? new SimpleStringProperty(param.getValue().get("Race Record")) : new SimpleStringProperty(""));
            raceRecordCol.setCellFactory(cellFactory);

            TableColumn<Map<String, String>, String> groupCol = new TableColumn<>("Group");
            groupCol.setCellValueFactory(param -> param.getValue().containsKey("Group") ? new SimpleStringProperty(param.getValue().get("Group")) : new SimpleStringProperty(""));
            groupCol.setCellFactory(cellFactory);

            tableView.getColumns().addAll(horseIDCol, horseNameCol, jockeyNameCol, ageCol, breedCol, raceRecordCol, groupCol);

            for (String group : uniqueGroups) {
                List<Map<String, String>> groupHorses = new ArrayList<>();
                for (Map<String, String> horse : horseDetails) {
                    if (horse.get("Group").equals(group)) {
                        groupHorses.add(horse);
                    }
                }
                if (!groupHorses.isEmpty()) {
                    shuffle(groupHorses);
                    bubbleSortByWinningPercentage(groupHorses);
                    double maxWinningPercentage = 0.0;
                    List<Map<String, String>> selectedGroupHorses = new ArrayList<>();
                    for (Map<String, String> horse : groupHorses) {
                        double winningPercentage = calculateWinningPercentage(horse);
                        if (winningPercentage == maxWinningPercentage) {
                            selectedGroupHorses.add(horse);
                        } else if (winningPercentage > maxWinningPercentage) {
                            selectedGroupHorses.clear();
                            selectedGroupHorses.add(horse);
                            maxWinningPercentage = winningPercentage;
                        }
                    }
                    selectedHorses.addAll(selectedGroupHorses);
                    for (Map<String, String> selectedHorse : selectedGroupHorses) {
                        data.add(selectedHorse);
                    }
                } else {
                    TextArea outputArea = new TextArea();
                    outputArea.setEditable(false);
                    outputArea.setWrapText(true);
                    outputArea.setBackground(new Background(new BackgroundFill(Color.web("#E7F2E2"), CornerRadii.EMPTY, Insets.EMPTY)));
                    outputArea.appendText("Error: No horses found in group " + group + ". Skipping this group.\n");
                    root.getChildren().add(outputArea);
                }
            }

            tableView.setItems(data);
            root.getChildren().add(tableView);

            Stage stage = new Stage();
            stage.setTitle("Selected Horses:");
            stage.setScene(new Scene(root, 580, 220));
            stage.show();
            RapidRun.raceStarted = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom bubble sort implementation to sort horses by winning percentage (race record)
    static void bubbleSortByWinningPercentage(List<Map<String, String>> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double winningPercentage1 = calculateWinningPercentage(list.get(j));
                double winningPercentage2 = calculateWinningPercentage(list.get(j + 1));
                if (winningPercentage1 > winningPercentage2) {
                    // Swap the horses if they are in the wrong order
                    Map<String, String> temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // Calculate winning percentage from race record
    static double calculateWinningPercentage(Map<String, String> horse) {
        String[] raceRecord = horse.get("Race Record").split("/");
        if (raceRecord.length == 2) {
            int wins = Integer.parseInt(raceRecord[0]);
            int totalRaces = Integer.parseInt(raceRecord[1]);
            return (double) wins / totalRaces;
        } else {
            return 0.0; // Return 0 if race record is invalid
        }
    }

    static void displayWinningHorses(List<Map<String, String>> selectedHorses) {
        assignRandomTimes(selectedHorses);
        sortHorsesByRaceTime(selectedHorses);

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(25));
        root.setBackground(new Background(new BackgroundFill(Color.web("#B2D2A4"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Final Positions:");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A4314"));
        root.getChildren().add(title);

        for (int i = 0; i < Math.min(selectedHorses.size(), 3); i++) {
            Map<String, String> horse = selectedHorses.get(i);
            String position;
            switch (i) {
                case 0:
                    position = "1st";
                    break;
                case 1:
                    position = "2nd";
                    break;
                case 2:
                    position = "3rd";
                    break;
                default:
                    position = (i + 1) + "th";
                    break;
            }

            Label horseInfo = new Label(position + " Place - " + horse.get("Horse ID") + ",  " + horse.get("Horse Name") + ",  Group: " + horse.get("Group") + ",  Race Time: " + horse.get("Race Time") + "s");
            horseInfo.setFont(Font.font("Times New Roman", 16));
            horseInfo.setTextFill(Color.BLACK);
            root.getChildren().add(horseInfo);
        }

        Stage stage = new Stage();
        stage.setTitle("Final Positions");
        stage.setScene(new Scene(root, 450, 175));
        stage.show();
    }

    static void assignRandomTimes(List<Map<String, String>> horses) {
        Random random = new Random();
        for (Map<String, String> horse : horses) {
            horse.put("Race Time", String.valueOf(random.nextInt(91)));
        }
    }

    static void sortHorsesByRaceTime(List<Map<String, String>> horses) {
        int n = horses.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int raceTime1 = Integer.parseInt(horses.get(j).get("Race Time"));
                int raceTime2 = Integer.parseInt(horses.get(j + 1).get("Race Time"));
                if (raceTime1 > raceTime2) {
                    // Swap horses
                    Map<String, String> temp = horses.get(j);
                    horses.set(j, horses.get(j + 1));
                    horses.set(j + 1, temp);
                }
            }
        }
    }

    static void visualizeWinningHorses() {
        // Create the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Time Taken");

        // Adjust bar width by setting bar gap
        barChart.setBarGap(40);

        // Create series for each horse
        for (int i = 0; i < Math.min(selectedHorses.size(), 3); i++) {
            Map<String, String> horse = selectedHorses.get(i);
            String horseName = horse.get("Horse Name");
            int raceTime = Integer.parseInt(horse.get("Race Time"));
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(horseName);
            series.getData().add(new XYChart.Data<>("", raceTime));
            barChart.getData().add(series);
        }

        // Create a new stage to display the bar chart
        Stage stage = new Stage();
        stage.setTitle("Time Taken");
        stage.setScene(new Scene(barChart, 400, 400));
        stage.show();
    }
}
