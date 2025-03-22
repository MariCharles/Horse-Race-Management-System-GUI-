package com.example.cw_draft1_3;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class RapidRunTest {

    @BeforeAll
    public static void setUpJFX() {
        new JFXPanel();
    }

    private List<Map<String, String>> horseDetails;

    @BeforeEach
    public void setUp() {
        horseDetails = new ArrayList<>();
        Map<String, String> horse1 = new HashMap<>();
        horse1.put("Horse ID", "123");
        horse1.put("Horse Name", "TestHorse");
        horse1.put("Jockey Name", "TestJockey");
        horse1.put("Age", "5");
        horse1.put("Breed", "TestBreed");
        horse1.put("Race Record", "3/5");
        horse1.put("Group", "A");
        horseDetails.add(horse1);
    }

    @Test
    public void AddHorseDetails_Successful() {
        Platform.runLater(() -> {
            int initialSize = horseDetails.size();
            RapidRun.addHorseDetails();
            assertEquals(initialSize + 1, horseDetails.size());
        });
    }

    @Test
    public void AddHorseDetails_InvalidHorseID() {
        String invalidHorseID = "ABC";

        Platform.runLater(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            try {
                System.setOut(new PrintStream(outputStream));

                RapidRun.addHorseDetails();

                String consoleOutput = outputStream.toString();
                assertEquals("Error: Horse ID must contain only numbers.", consoleOutput.trim());
            } finally {
                System.setOut(originalOut);
            }
        });
    }

    @Test
    public void AddHorseDetails_DuplicateHorseID() {
        String duplicateHorseID = "123";

        Platform.runLater(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            try {
                System.setOut(new PrintStream(outputStream));

                RapidRun.addHorseDetails();

                String consoleOutput = outputStream.toString();
                assertEquals("Error: Horse ID must be unique.", consoleOutput.trim());
            } finally {
                System.setOut(originalOut);
            }
        });
    }

    @Test
    public void AddHorseDetails_RequiredFields() {
        Map<String, String> incompleteHorse = new HashMap<>();
        incompleteHorse.put("Horse ID", "456");

        Platform.runLater(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            try {
                System.setOut(new PrintStream(outputStream));

                RapidRun.addHorseDetails();

                String consoleOutput = outputStream.toString();
                assertTrue(consoleOutput.contains("Error: Required fields cannot be empty."));
            } finally {
                System.setOut(originalOut);
            }
        });
    }

    private List<Map<String, String>> createSampleHorseDetails() {
        List<Map<String, String>> horseDetails = new ArrayList<>();
        Map<String, String> horse1 = new HashMap<>();
        horse1.put("Horse ID", "123");
        horse1.put("Horse Name", "TestHorse");
        horse1.put("Jockey Name", "TestJockey");
        horse1.put("Age", "5");
        horse1.put("Breed", "TestBreed");
        horse1.put("Race Record", "3/5");
        horse1.put("Group", "A");
        horseDetails.add(horse1);
        return horseDetails;
    }

    @Test
    public void UpdateHorseDetails_Successful() {
        Platform.runLater(() -> {
            List<Map<String, String>> horseDetails = createSampleHorseDetails();
            int initialSize = horseDetails.size();

            String horseIdToUpdate = "123";
            String newHorseName = "UpdatedHorseName";

            RapidRun.updateHorseDetails();

            assertEquals(initialSize, horseDetails.size());
            Optional<Map<String, String>> updatedHorse = horseDetails.stream()
                    .filter(horse -> horse.get("Horse ID").equals(horseIdToUpdate))
                    .findFirst();
            assertTrue(updatedHorse.isPresent());
            assertEquals(newHorseName, updatedHorse.get().get("Horse Name"));
        });
    }

    @Test
    public void UpdateHorseDetails_UnavailableID() {
        Platform.runLater(() -> {
            List<Map<String, String>> horseDetails = createSampleHorseDetails();
            int initialSize = horseDetails.size();

            String invalidHorseId = "999";

            RapidRun.updateHorseDetails();

            assertEquals(initialSize, horseDetails.size());
            Optional<Map<String, String>> unchangedHorse = horseDetails.stream()
                    .filter(horse -> horse.get("Horse ID").equals(invalidHorseId))
                    .findFirst();
            assertFalse(unchangedHorse.isPresent());
        });
    }

    @Test
    public void DeleteHorseDetails_Successful() {
        Platform.runLater(() -> {
            int initialSize = horseDetails.size();

            RapidRun.deleteHorseDetails();

            int newSize = horseDetails.size();

            assertEquals(initialSize - 1, newSize);
        });
    }

    @Test
    public void DeleteHorseDetails_UnavailableID() {
        Platform.runLater(() -> {
            String unavailableHorseId = "456";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            try {
                System.setOut(new PrintStream(outputStream));

                RapidRun.deleteHorseDetails();

                String consoleOutput = outputStream.toString().trim();
                assertEquals("Horse with ID " + unavailableHorseId + " does not exist.", consoleOutput);
            } finally {
                System.setOut(originalOut);
            }
        });
    }

    @Test
    public void ViewHorseDetails_Successful() {
        Platform.runLater(() -> {
            RapidRun.viewHorseDetails(horseDetails);

            VBox root = (VBox) Stage.getWindows().stream()
                    .filter(window -> window instanceof Stage)
                    .findFirst()
                    .orElse(null)
                    .getScene().getRoot();

            Label titleLabel = (Label) root.getChildren().get(0);
            assertEquals("Horse Details:", titleLabel.getText());

            TableView<Map<String, String>> tableView = (TableView<Map<String, String>>) root.getChildren().get(1);
            assertNotNull(tableView);

            ObservableList<Map<String, String>> items = tableView.getItems();
            assertEquals(horseDetails.size(), items.size());
        });
    }

    @Test
    public void SaveHorseDetails_Successful() {
        Platform.runLater(() -> {
            RapidRun.saveHorseDetails();

            String filePath = "horse_details.txt";
            File file = new File(filePath);
            assertTrue(file.exists());

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                StringBuilder expectedContent = new StringBuilder();
                for (Map<String, String> horse : horseDetails) {
                    expectedContent.append(String.format("%-10s%-20s%-20s%-10s%-20s%-20s%-10s%-50s%n",
                            horse.get("Horse ID"), horse.get("Horse Name"),
                            horse.get("Jockey Name"), horse.get("Age"),
                            horse.get("Breed"), horse.get("Race Record"),
                            horse.get("Group"), horse.get("Image Name")));
                }
                assertEquals(expectedContent.toString(), content.toString());
            } catch (IOException e) {
                fail("Exception occurred while reading the file: " + e.getMessage());
            }
        });
    }
}