package edu.curtin.gameplugins;

import org.maze.api.Plugin;
import org.maze.api.GameAPI;

public class Penalty implements Plugin {
    private final GameAPI gameAPI;
    private long lastMoveTime = System.currentTimeMillis();

    public Penalty(GameAPI gameAPI) {
        this.gameAPI = gameAPI;
        System.out.println("Penalty plugin initialized");
    }

    @Override
    public void onPlayerMove(String direction) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastMoveTime;
        
        System.out.println("Time since last move: " + timeDifference + " ms");
        
        if (timeDifference > 5000) { 
            System.out.println("Attempting to add penalty obstacle");
            int playerX = gameAPI.getPlayerX();
            int playerY = gameAPI.getPlayerY();
            
            
            int[][] adjacentCells = {{0,1}, {1,0}, {0,-1}, {-1,0}};
            for (int[] cell : adjacentCells) {
                int newX = playerX + cell[0];
                int newY = playerY + cell[1];
                if (gameAPI.isValidMove(newX, newY) && !gameAPI.isObstacle(newX, newY)) {
                    gameAPI.addObstacle(newX, newY);
                    System.out.println("A penalty obstacle has appeared at (" + newX + ", " + newY + ")!");
                    break;
                }
            }
        } else {
            System.out.println("Move was too quick for penalty");
        }
        
        lastMoveTime = currentTime;
    }

    @Override
    public void onItemAcquired(String itemName) {
        
    }

    @Override
    public void onMenuOptionSelected() {
        
    }
}
