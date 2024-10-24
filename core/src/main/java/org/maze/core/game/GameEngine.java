package org.maze.core.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameEngine {
    private static final int VISIBILITY_RANGE = 1;
    private GameConfig gameConfig;
    private int playerX;
    private int playerY;
    private boolean gameRunning;
    private List<GameConfig.Item> inventory;
    private boolean[][] revealedCells;

    public void initialize(GameConfig config) {
        this.gameConfig = config;
        this.playerX = config.getStartX();
        this.playerY = config.getStartY();
        this.gameRunning = true;
        this.inventory = new ArrayList<>();
        this.revealedCells = new boolean[config.getHeight()][config.getWidth()];
        revealArea(); // Reveal the starting area
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (gameRunning) {
            displayGame();
            System.out.print("Enter move (W/A/S/D) or Q to quit: ");
            String input = scanner.nextLine().toUpperCase();
            processInput(input);
            checkGameState();
        }
        scanner.close();
    }

    private void displayGame() {
        for (int y = 0; y < gameConfig.getHeight(); y++) {
            for (int x = 0; x < gameConfig.getWidth(); x++) {
                if (isVisible(x, y)) {
                    if (x == playerX && y == playerY) {
                        System.out.print("P ");
                    } else if (x == gameConfig.getGoalX() && y == gameConfig.getGoalY()) {
                        System.out.print("G ");
                    } else if (isObstacle(x, y)) {
                        System.out.print("# ");
                    } else if (isItem(x, y)) {
                        System.out.print("I ");
                    } else {
                        System.out.print(". ");
                    }
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }

        System.out.println("\nInventory:");
        if (inventory.isEmpty()) {
            System.out.println("Empty");
        } else {
            inventory.forEach(item -> System.out.println("- " + item.getName()));
        }
        System.out.println();
    }

    private boolean isObstacle(int x, int y) {
        return gameConfig.getObstacles().stream()
                .anyMatch(o -> o.getX() == x && o.getY() == y);
    }

    private boolean isItem(int x, int y) {
        return gameConfig.getItems().stream()
                .anyMatch(i -> i.getX() == x && i.getY() == y);
    }

    private void processInput(String input) {
        switch (input) {
            case "W": movePlayer(0, -1); break;
            case "S": movePlayer(0, 1); break;
            case "A": movePlayer(-1, 0); break;
            case "D": movePlayer(1, 0); break;
            case "Q": gameRunning = false; break;
            default: System.out.println("Invalid input!");
        }
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;
        if (isValidMove(newX, newY)) {
            playerX = newX;
            playerY = newY;
            revealArea();
            collectItem(playerX, playerY);
        } else {
            System.out.println("Invalid move!");
        }
    }

    private void collectItem(int x, int y) {
        GameConfig.Item item = gameConfig.getItems().stream()
            .filter(i -> i.getX() == x && i.getY() == y)
            .findFirst()
            .orElse(null);

        if (item != null) {
            inventory.add(item);
            gameConfig.getItems().remove(item);
            System.out.println("You picked up a " + item.getName() + "!");
        }
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < gameConfig.getWidth() &&
               y >= 0 && y < gameConfig.getHeight() &&
               !isObstacle(x, y);
    }

    private void checkGameState() {
        if (playerX == gameConfig.getGoalX() && playerY == gameConfig.getGoalY() && isVisible(gameConfig.getGoalX(), gameConfig.getGoalY())) {
            System.out.println("Congratulations! You've reached the goal!");
            gameRunning = false;
        }
    }

    private boolean isVisible(int x, int y) {
        boolean currentlyVisible = Math.abs(x - playerX) <= VISIBILITY_RANGE && Math.abs(y - playerY) <= VISIBILITY_RANGE;
        return currentlyVisible || revealedCells[y][x];
    }

    private void revealArea() {
        for (int y = Math.max(0, playerY - VISIBILITY_RANGE); y <= Math.min(gameConfig.getHeight() - 1, playerY + VISIBILITY_RANGE); y++) {
            for (int x = Math.max(0, playerX - VISIBILITY_RANGE); x <= Math.min(gameConfig.getWidth() - 1, playerX + VISIBILITY_RANGE); x++) {
                revealedCells[y][x] = true;
            }
        }
    }
}
