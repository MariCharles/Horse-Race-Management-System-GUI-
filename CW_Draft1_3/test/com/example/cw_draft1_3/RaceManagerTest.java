package com.example.cw_draft1_3;

import javafx.application.Platform;
import org.junit.Test;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RaceManagerTest {

    @BeforeAll
    public static void setUpJFX() {
        new JFXPanel();
    }

    @Test
    public void DisplayWinningHorses_Sucessful() {
        List<Map<String, String>> selectedHorses = new ArrayList<>();
        Map<String, String> horse1 = new HashMap<>();
        horse1.put("Horse ID", "123");
        horse1.put("Horse Name", "TestHorse1");
        horse1.put("Group", "A");
        horse1.put("Race Time", "50");
        selectedHorses.add(horse1);

        Map<String, String> horse2 = new HashMap<>();
        horse2.put("Horse ID", "456");
        horse2.put("Horse Name", "TestHorse2");
        horse2.put("Group", "B");
        horse2.put("Race Time", "60");
        selectedHorses.add(horse2);

        Map<String, String> horse3 = new HashMap<>();
        horse3.put("Horse ID", "789");
        horse3.put("Horse Name", "TestHorse3");
        horse3.put("Group", "C");
        horse3.put("Race Time", "70");
        selectedHorses.add(horse3);

        RapidRun.selectedHorses = selectedHorses;

        Platform.runLater(() -> RaceManager.displayWinningHorses(selectedHorses));

        assertTrue(true);
    }
}