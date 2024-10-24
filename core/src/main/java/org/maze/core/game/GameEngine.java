package org.maze.core.game;

import org.maze.api.Plugin;
import org.maze.api.Script;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

import org.maze.api.GameAPI;
import org.maze.core.i18n.LocalizationManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class GameEngine implements GameAPI {
    private static final int VISIBILITY_RANGE = 1;
    private GameConfig gameConfig;
    private int playerX;
    private int playerY;
    private boolean gameRunning;
    private List<GameConfig.Item> inventory;
    private boolean[][] revealedCells;
    private LocalizationManager locManager = LocalizationManager.getInstance();
    private Scanner scanner;
    private int daysElapsed = 0;
    private PythonInterpreter interpreter;
    private final List<Plugin> plugins = new ArrayList<>();
    private final List<Script> scripts = new ArrayList<>();
    private Map<String, Plugin> pluginMap = new HashMap<>();
    private long lastRenderTime;

    public void initialize(GameConfig config) {
        this.gameConfig = config;
        this.playerX = config.getStartX();
        this.playerY = config.getStartY();
        this.gameRunning = true;
        this.inventory = new ArrayList<>();
        this.revealedCells = new boolean[config.getHeight()][config.getWidth()];
        revealArea(); 
        this.scanner = new Scanner(System.in);
        this.interpreter = new PythonInterpreter();
    }

    private void loadPlugins(List<String> plugins) {
        for (String className : plugins) {
            try {
                Class<?> clazz = Class.forName(className);
                Plugin plugin = (Plugin) clazz.getDeclaredConstructor(GameAPI.class).newInstance(this);
                pluginMap.put(className, plugin);
            } catch (Exception e) {
                // Exception handling could be improved or logged if needed
            }
        }
    }

    private void loadScripts(List<String> scriptCodes) {
        interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append('.')");  
        for (String scriptCode : scriptCodes) {
            try {
                interpreter.exec(scriptCode);
            } catch (Exception e) {
                System.err.println("Failed to execute script: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void notifyPluginsOnPlayerMove(String direction) {
        for (Plugin plugin : plugins) {
            plugin.onPlayerMove(direction);
        }
    }

    private void notifyPluginsOnItemAcquired(String itemName) {
        for (Plugin plugin : plugins) {
            plugin.onItemAcquired(itemName);
        }
    }

    private void notifyPluginsOnMenuOptionSelected() {
        for (Plugin plugin : plugins) {
            plugin.onMenuOptionSelected();
        }
    }

    public void executeScripts() {
        for (Script script : scripts) {
            try {
                script.execute();
            } catch (Exception e) {
                System.err.println("Error executing script: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void run() {
        System.out.println(locManager.getString("welcome"));
        printConfigDetails(this.gameConfig);
        loadPlugins(this.gameConfig.getPluginClassNames());
        loadScripts(this.gameConfig.getScriptClassNames());
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        lastRenderTime = System.currentTimeMillis();
        long renderInterval = 1000; 

        while (gameRunning) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRenderTime >= renderInterval) {
                clearConsole();
                displayGame(lastRenderTime);
                System.out.print(locManager.getString("move_prompt") + " ");
                lastRenderTime = currentTime;
            }

            try {
                if (reader.ready()) {
                    String input = reader.readLine().toUpperCase();
                    processInput(input);
                    checkGameState();
                    locManager.advanceGameDate();
                    daysElapsed++;
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println(locManager.getFormattedString("days_elapsed", daysElapsed));
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing reader: " + e.getMessage());
        }
    }

    private static void printConfigDetails(GameConfig config) {
        System.out.println("Details in the config:");
        System.out.println("Size: " + config.getWidth() + "x" + config.getHeight());
        System.out.println("Start position: (" + config.getStartX() + ", " + config.getStartY() + ")");
        System.out.println("Goal position: (" + config.getGoalX() + ", " + config.getGoalY() + ")");
        System.out.println("Number of items: " + config.getItems().size());
        System.out.println("Items:");
        for (GameConfig.Item item : config.getItems()) {
            for (GameConfig.Position position : item.getPositions()) {
                System.out.println("  " + item.getName() + " at (" + position.getX() + ", " + position.getY() + ")");
            }
        }
        System.out.println("Number of obstacles: " + config.getObstacles().size());
        System.out.println("Scripts Count: " + config.getScripts().size());
        System.out.println("Scripts:");
        for (String script : config.getScripts()) {
            System.out.println("  " + script);
        }
        System.out.println("Plugins:");
        for (String plugin : config.getPlugins()) {
            System.out.println("  " + plugin);
        }
    }

    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private void processInput(String input) {
        switch (input) {
            case "W": movePlayer(0, -1); break;
            case "S": movePlayer(0, 1); break;
            case "A": movePlayer(-1, 0); break;
            case "D": movePlayer(1, 0); break;
            case "L": changeLanguage(); break;
            case "Q": gameRunning = false; break;
            default:
                if (input.equals("T") && pluginMap.containsKey("edu.curtin.gameplugins.Teleport")) {
                    handlePluginMenuOption("edu.curtin.gameplugins.Teleport");
                } else {
                    System.out.println(locManager.getString("invalid_input"));
                }
        }
    }

    private void changeLanguage() {
        System.out.println(locManager.getString("language_prompt"));
        String lang = scanner.nextLine();
        locManager.setLocale(Locale.forLanguageTag(lang));
    }

    private void displayGame(long lastRenderTime) {
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

        System.out.println("\n" + locManager.getString("inventory_header"));
        if (inventory.isEmpty()) {
            System.out.println(locManager.getString("inventory_empty"));
        } else {
            inventory.forEach(item -> System.out.println("- " + locManager.normalizeItemName(item.getName())));
        }
        System.out.println("\n" + locManager.getLocalizedDate());
        System.out.println("\nOptions:");
        System.out.println("W/A/S/D - Move");
        System.out.println("L - Change Language");
        if (pluginMap.containsKey("edu.curtin.gameplugins.Teleport")) {
            System.out.println("T - Teleport");
        }
        System.out.println("Q - Quit");
        System.out.println();
    }

    private boolean isItem(int x, int y) {
        return gameConfig.getItems().stream()
                .anyMatch(i -> i.getPositions().stream().anyMatch(p -> p.getX() == x && p.getY() == y));
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;
        if (isValidMove(newX, newY)) {
            playerX = newX;
            playerY = newY;
            revealArea();
            collectItem(playerX, playerY);
            String direction = getDirectionString(dx, dy);
            notifyPluginsOnPlayerMove(direction);
            executeScripts();
        } else {
            System.out.println(locManager.getString("invalid_move"));
        }
    }

    private void collectItem(int x, int y) {
        GameConfig.Item item = gameConfig.getItems().stream()
            .filter(i -> i.getPositions().stream().anyMatch(p -> p.getX() == x && p.getY() == y))
            .findFirst()
            .orElse(null);

        if (item != null) {
            inventory.add(item);
            gameConfig.getItems().remove(item);
            System.out.println(locManager.getFormattedString("item_pickup", item.getName()));
            notifyPluginsOnItemAcquired(item.getName());
            executeScripts();
        }
    }

    private void checkGameState() {
        if (playerX == gameConfig.getGoalX() && playerY == gameConfig.getGoalY() && isVisible(gameConfig.getGoalX(), gameConfig.getGoalY())) {
            System.out.println(locManager.getString("goal_reached"));
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

    @Override
    public int getGridWidth() {
        return gameConfig.getWidth();
    }

    @Override
    public int getGridHeight() {
        return gameConfig.getHeight();
    }

    @Override
    public int getPlayerX() {
        return playerX;
    }

    @Override
    public int getPlayerY() {
        return playerY;
    }

    @Override
    public void setPlayerPosition(int x, int y) {
        if (isValidMove(x, y)) {
            playerX = x;
            playerY = y;
            revealArea();
        }
    }

    @Override
    public boolean isValidMove(int x, int y) {
        return x >= 0 && x < gameConfig.getWidth() &&
               y >= 0 && y < gameConfig.getHeight() &&
               !isObstacle(x, y);
    }

    @Override
    public boolean isObstacle(int x, int y) {
        return gameConfig.getObstacles().stream()
        .anyMatch(o -> o.getPositions().stream().anyMatch(p -> p.getX() == x && p.getY() == y));
    }

    @Override
    public void addObstacle(int x, int y) {
        gameConfig.addObstacle(Collections.singletonList(new GameConfig.Position(x, y)), Collections.emptyList());
        revealedCells[y][x] = true; 
    }

    @Override
    public String getLastAcquiredItem() {
        return inventory.isEmpty() ? null : inventory.get(inventory.size() - 1).getName();
    }

    @Override
    public void revealGoal() {
        revealedCells[gameConfig.getGoalY()][gameConfig.getGoalX()] = true;
    }

    @Override
    public void revealAllItems() {
        for (GameConfig.Item item : gameConfig.getItems()) {
            for (GameConfig.Position pos : item.getPositions()) {
                revealedCells[pos.getY()][pos.getX()] = true;
            }
        }
    }

    @Override
    public void addItemToInventory(String itemName) {
        inventory.add(new GameConfig.Item(itemName, Collections.emptyList(), ""));
        System.out.println("Added " + itemName + " to inventory.");
    }

    private String getDirectionString(int dx, int dy) {
        if (dx == 0 && dy == -1) return "UP";
        if (dx == 0 && dy == 1) return "DOWN";
        if (dx == -1 && dy == 0) return "LEFT";
        if (dx == 1 && dy == 0) return "RIGHT";
        return "UNKNOWN";
    }

    public void handlePluginMenuOption(String pluginClassName) {
        Plugin plugin = pluginMap.get(pluginClassName);
        if (plugin != null) {
            plugin.onMenuOptionSelected();
        }
    }
}
