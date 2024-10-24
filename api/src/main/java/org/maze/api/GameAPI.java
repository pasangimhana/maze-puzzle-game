package org.maze.api;

public interface GameAPI {
    int getGridWidth();
    int getGridHeight();
    int getPlayerX();
    int getPlayerY();
    void setPlayerPosition(int x, int y);
    boolean isValidMove(int x, int y);
    boolean isObstacle(int x, int y);
    void addObstacle(int x, int y);
    String getLastAcquiredItem();
    void revealGoal();
    void revealAllItems();
    void addItemToInventory(String itemName);
    
}
