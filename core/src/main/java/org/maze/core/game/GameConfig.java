package org.maze.core.game;

import java.util.*;

public class GameConfig {
    private int width;
    private int height;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;
    private List<Item> items = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<String> scripts = new ArrayList<>();
    private Set<String> plugins = new HashSet<>();

    
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setGoalX(int goalX) {
        this.goalX = goalX;
    }

    public void setGoalY(int goalY) {
        this.goalY = goalY;
    }

    public void addItem(String name, int x, int y) {
        items.add(new Item(name, Arrays.asList(new Position(x, y)), ""));
    }

    public void addItem(String name, List<Position> positions, String message) {
        items.add(new Item(name, positions, message));
    }

    public void addObstacle(List<Position> positions, List<String> requirements) {
        obstacles.add(new Obstacle(positions, requirements));
    }

    public void addScript(String script) {
        scripts.add(script);
    }

    public void addPlugin(String pluginName) {
        plugins.add(pluginName);
    }

    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getGoalX() {
        return goalX;
    }

    public int getGoalY() {
        return goalY;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public Set<String> getPlugins() {
        return plugins;
    }

    public List<String> getPluginClassNames() { return new ArrayList<>(plugins);
    }

    public List<String> getScriptClassNames() { return new ArrayList<>(scripts);
    }



    public static class Item {
        private String name;
        private List<Position> positions;
        private String message;

        public Item(String name, List<Position> positions, String message) {
            this.name = name;
            this.positions = positions;
            this.message = message;
        }

        public int getX() { return positions.get(0).getX(); }
        public int getY() { return positions.get(0).getY(); }

        public String getName() { return name; }

        public List<Position> getPositions() {
            return positions;
        }
    }

    public static class Obstacle {
        private List<Position> positions;
        private List<String> requirements;

        public Obstacle(List<Position> positions, List<String> requirements) {
            this.positions = positions;
            this.requirements = requirements;
        }

        public int getX() { return positions.get(0).getX(); }
        public int getY() { return positions.get(0).getY(); }

        public List<Position> getPositions() {
            return positions;
        }
    }

    public static class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }
}
