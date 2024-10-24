package edu.curtin.gameplugins;

import org.maze.api.Plugin;
import org.maze.api.GameAPI;

public class Prize implements Plugin {
    private GameAPI gameAPI;
    private int itemsAcquired = 0;
    private int obstaclesTraversed = 0;
    private boolean prizeAwarded = false;

    public Prize(GameAPI gameAPI) {
        this.gameAPI = gameAPI;
    }

    @Override
    public void onPlayerMove(String direction) {
        int playerX = gameAPI.getPlayerX();
        int playerY = gameAPI.getPlayerY();
        if (gameAPI.isObstacle(playerX, playerY)) {
            obstaclesTraversed++;
            checkPrize();
        }
    }

    @Override
    public void onItemAcquired(String itemName) {
        itemsAcquired++;
        checkPrize();
    }

    @Override
    public void onMenuOptionSelected() {
        
    }

    private void checkPrize() {
        if (!prizeAwarded && (itemsAcquired + obstaclesTraversed) >= 5) {
            gameAPI.addItemToInventory("Special Prize");
            System.out.println("Congratulations! You've been awarded a Special Prize!");
            prizeAwarded = true;
        }
    }
}
