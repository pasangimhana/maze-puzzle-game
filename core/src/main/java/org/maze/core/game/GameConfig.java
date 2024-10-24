package org.maze.core.game;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
    private int width;
    private int height;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;
    private List<Item> items = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();

    // Getters and setters
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
        items.add(new Item(name, x, y));
    }

    public void addObstacle(int x, int y) {
        obstacles.add(new Obstacle(x, y));
    }

    // Add these getter methods
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
    public static class Item {
        private String name;
        private int x;
        private int y;

        Item(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

        public String getName() { return name; }
    }

    public static class Obstacle {
        private int x;
        private int y;

        Obstacle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }
}
