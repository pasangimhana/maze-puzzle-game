package edu.curtin.gameplugins;

import org.maze.api.Plugin;
import org.maze.api.GameAPI;

import java.util.Random;

public class Teleport implements Plugin {
    private GameAPI gameAPI;
    private boolean teleportUsed = false;
    private Random random = new Random();

    public Teleport(GameAPI gameAPI) {
        this.gameAPI = gameAPI;
    }

    @Override
    public void onPlayerMove(String direction) {
        
    }

    @Override
    public void onItemAcquired(String itemName) {
        
    }

    @Override
    public void onMenuOptionSelected() {
        if (!teleportUsed) {
            int newX = random.nextInt(gameAPI.getGridWidth());
            int newY = random.nextInt(gameAPI.getGridHeight());
            
            if (gameAPI.isValidMove(newX, newY)) {
                gameAPI.setPlayerPosition(newX, newY);
                System.out.println("You've been teleported to (" + newX + ", " + newY + ")!");
                teleportUsed = true;
            } else {
                System.out.println("Teleportation failed. Try again.");
            }
        } else {
            System.out.println("You've already used your teleportation opportunity.");
        }
    }
}
