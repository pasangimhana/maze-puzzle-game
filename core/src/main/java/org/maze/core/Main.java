package org.maze.core;

import org.maze.core.game.GameEngine;
import org.maze.core.game.GameConfig;
import org.maze.core.io.InputParser;
import org.maze.core.i18n.LocalizationManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameConfig config;
        LocalizationManager locManager = LocalizationManager.getInstance();
        
        if (args.length > 0) {
            String configFile = args[0];
            try {
                InputParser parser = new InputParser();
                config = parser.parseInputFile(configFile);
                System.out.println("Configuration loaded from file: " + configFile);
            } catch (IOException e) {
                System.err.println("Error reading config file: " + e.getMessage());
                System.out.println("Using default configuration.");
                config = createDefaultConfig();
            }
        } else {
            System.out.println("No config file provided. Using default configuration.");
            config = createDefaultConfig();
        }

        GameEngine gameEngine = new GameEngine();

        try {
            gameEngine.initialize(config);
            selectLanguage(locManager);
            gameEngine.executeScripts();
            gameEngine.run();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void selectLanguage(LocalizationManager locManager) {
        locManager.setLocale(Locale.forLanguageTag("en"));
    }

    private static GameConfig createDefaultConfig() {
        GameConfig config = new GameConfig();
        config.setWidth(10);
        config.setHeight(10);
        config.setStartX(0);
        config.setStartY(0);
        config.setGoalX(9);
        config.setGoalY(9);
        
        
        config.addObstacle(
            Collections.singletonList(new GameConfig.Position(2, 2)),
            Collections.emptyList()
        );
        config.addObstacle(
            Collections.singletonList(new GameConfig.Position(3, 3)),
            Collections.emptyList()
        );
        config.addObstacle(
            Collections.singletonList(new GameConfig.Position(4, 4)),
            Collections.emptyList()
        );
        
        
        config.addItem("Key", 
            Collections.singletonList(new GameConfig.Position(5, 5)),
            "You found a key!"
        );
        config.addItem("Potion", 
            Collections.singletonList(new GameConfig.Position(7, 3)),
            "You found a potion!"
        );
        config.addItem("Sword", 
            Collections.singletonList(new GameConfig.Position(2, 8)),
            "You found a sword!"
        );
        
        
        config.addPlugin("edu.curtin.gameplugins.Teleport");
        config.addPlugin("edu.curtin.gameplugins.Penalty");
        config.addPlugin("edu.curtin.gameplugins.Prize");
        config.addScript("reveal.Reveal");

        return config;
    }
}
