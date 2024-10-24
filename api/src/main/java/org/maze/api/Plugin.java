package org.maze.api;

public interface Plugin {
    void onPlayerMove(String direction);
    void onItemAcquired(String itemName);
    void onMenuOptionSelected();
}
