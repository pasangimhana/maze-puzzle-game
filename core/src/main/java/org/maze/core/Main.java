package org.maze.core;

import org.maze.core.game.GameEngine;
import org.maze.core.game.GameConfig;

public class Main {
    public static void main(String[] args) {
        GameConfig config = createDefaultConfig();
        GameEngine gameEngine = new GameEngine();

        try {
            gameEngine.initialize(config);
            gameEngine.run();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static GameConfig createDefaultConfig() {
        GameConfig config = new GameConfig();
        config.setWidth(10);
        config.setHeight(10);
        config.setStartX(0);
        config.setStartY(0);
        config.setGoalX(9);
        config.setGoalY(9);
        // Add some obstacles
        config.addObstacle(2, 2);
        config.addObstacle(3, 3);
        config.addObstacle(4, 4);
        // Add more items
        config.addItem("Key", 5, 5);
        config.addItem("Potion", 7, 3);
        config.addItem("Sword", 2, 8);
        return config;
    }
}
