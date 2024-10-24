class Reveal:
    def __init__(self, gameAPI):
        self.gameAPI = gameAPI
        self.revealed = False

    def execute(self):
        if not self.revealed:
            current_item = self.gameAPI.getLastAcquiredItem()
            if current_item and "map" in current_item.lower():
                self.gameAPI.revealGoal()
                self.gameAPI.revealAllItems()
                self.revealed = True
                print("The map has revealed the goal and all remaining items!")

