package org.maze.core.io;

import org.maze.core.game.GameConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class InputParser {
    private static final Logger LOGGER = Logger.getLogger(InputParser.class.getName());

    public GameConfig parseInputFile(String filePath) throws IOException {
        LOGGER.info("Reading config from file: " + filePath);
        GameConfig gameConfig = new GameConfig();

        try (FileReader reader = new FileReader(filePath)) {
            MazeParser parser = new MazeParser(reader);
            parser.parse(gameConfig);
            LOGGER.info("Parsing successful");
            logConfigDetails(gameConfig);
            return gameConfig;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Parsing failed. Error: " + e.getMessage(), e);
            throw new IOException("Failed to parse input file", e);
        }
    }

    private void logConfigDetails(GameConfig config) {
        LOGGER.info("Details in the config:");
        LOGGER.info("Size: " + config.getWidth() + "x" + config.getHeight());
        LOGGER.info("Start position: (" + config.getStartX() + ", " + config.getStartY() + ")");
        LOGGER.info("Goal position: (" + config.getGoalX() + ", " + config.getGoalY() + ")");
        LOGGER.info("Number of items: " + config.getItems().size());
        LOGGER.info("Number of obstacles: " + config.getObstacles().size());
    }

}
