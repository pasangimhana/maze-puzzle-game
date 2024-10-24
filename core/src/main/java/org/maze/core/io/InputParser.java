package org.maze.core.io;

import org.maze.core.game.GameConfig;

import java.io.FileReader;

public class InputParser {
    public GameConfig parseInputFile(String filePath) throws Exception {
        GameConfig gameConfig = new GameConfig();
        try (FileReader reader = new FileReader(filePath)) {
            MazeParser parser = new MazeParser(reader);
            parser.parse(gameConfig);
        }
        return gameConfig;
    }
}
