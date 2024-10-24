package org.maze.core.io;

import org.maze.core.game.GameConfig;

import java.io.*;
import java.nio.charset.Charset;

public class InputParser {

    public GameConfig parseInputFile(String filePath) throws IOException {
        System.out.println("Reading config from file: " + filePath);
        GameConfig gameConfig = new GameConfig();

        File file = new File(filePath);
        String encoding = EncodingDetector.detectEncoding(file);
        System.out.println("Detected file encoding: " + encoding);

        try (Reader reader = new InputStreamReader(new FileInputStream(file), Charset.forName(encoding))) {
            MazeParser parser = new MazeParser(reader);
            parser.parse(gameConfig);
            System.out.println("Parsing successful");
            return gameConfig;
        } catch (Exception e) {
            System.out.println("Parsing failed. Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to parse input file", e);
        }
    }

}
