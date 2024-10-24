Your task is to design and implement a simple 2D maze/puzzle game engine, with certain specific requirements, making use of Gradle, plugins and scripts, a domain-specific language, and internationalisation. (There's no need to use any multithreading.)

You must use Java, unless you obtain written permission from the unit coordinator to use a different language. You are permitted to use any code or libraries shown or provided in this unit. However, you cannot use any 3rd-party code or libraries code beyond this, without prior written permission.

Alternate languages
-------------------
If you do have permission to use a language other than Java, you must accept responsibility for the greater effort and risk this will involve, particularly because:

- There will be far less (if any) unit material dealing with that language; and
- Teaching staff will be less able (and perhaps unable) to assist with issues arising in that language.

1. Basic Concepts and Functionality
-----------------------------------
Your puzzle game must be based on a 2D grid. It may be either console- or GUI-based, as per your preference. (If you choose to incorporate a GUI, you can reuse the same starter code provided for Assignment 1, if you wish.)

In either case, your game must take a single command-line parameter, indicating an input file. This input file is expected to contain game configuration (in a specially-designed format).

This file will determine:

- The size of the 2D grid (the number of rows and columns);
- The player's starting location (row and column);
- The goal location, where the player must get to (row and column);
- The location and nature of "items" and "obstacles";
- Any plugins and/or scripts to be used.

Each grid square may contain an "item", an "obstacle", the "goal", or nothing. (It never contains more than one of these.) Also, each grid square is either hidden or visible. If hidden, the player cannot see whether the grid square contains anything, or what it is.

Initially, all squares are hidden except for the start location and the squares immediately around it. For instance, if the player starts at location (2,2), then that square and the squares (1,2), (2,1), (2,3) and (3,2) will be visible, and all others will be hidden.

During the game, the player must make a sequence of moves, "up", "right", "down" or "left". Any grid square next to the player, at any point, becomes visible and stays visible for the rest of the game.

If a player moves into a square containing an item, the player acquires that item. It disappears from the grid, and becomes part of the player's "inventory".

A player cannot move into a square containing an obstacle, unless the player possesses one or more specific items. The particular item(s) required to move through an obstacle are specific to the obstacle (and specified in the input file). If the player does not have these item(s), the game will display the names of the item(s) required, and the player will not move.

The game ends when the player moves to a specified "goal" location.

1.1 Display Requirements
------------------------
The game must show an up-to-date version of the grid and the player's inventory (a list of all the items obtained) at all times. If using the console, this means re-displaying both after each move.

For each grid square:

- If it's hidden, then indicate that it's hidden (e.g., by printing just "###") and show nothing else about it.
- If it's visible, then it may contain the player, and/or the goal, and/or an item or obstacle, and all of these must be shown in some fashion.

Optional "Cheat" Mode
---------------------
You may like to create a special "cheat" mode in which the contents of "hidden" squares become visible (but is still marked as if it was hidden). This isn't strictly necessary, but it may help you see what's happening.

2. Internationalisation (9 marks)
---------------------------------
These requirements apply to the core game, and not (unless you want) to the implementation of plugins and scripts.

2 marks — Provide a option (which the user can select at any point during the game) to change the locale currently being used. The user should be asked to enter an IETF language tag; e.g., "en-AU", "th-TH-u-nu-thai", etc. The game must initially use the system default locale.

Your game must accept absolutely any locale; you must not impose arbitrary restrictions.

Note: Use Locale.forLanguageTag().

IETF language tags are one of two common locale representations, and this assignment is specifically asking for IETF language tags and not the other format! Also, don't try to parse these with "String.split()" or similar, because they're more complicated than they first appear.

3 marks — All UI text must be translatable, based on the user's selected locale, and there must be translations for English and some other language (of your choice), whether real or fake. (A fake translation may consist of any arbitrary text in place of English text, but it must not be offensive, and must still use the proper internationalisation techniques.)

Obviously you won't be able to create translations for every possible locale, but you should have a default translation.

2 marks — Display a properly localised, in-game date. This will start as the current date (when the game is launched), but must advance by one "day" each time the player makes a move, and updated on-screen accordingly.

At the game's end, display the number of "days" (moves) elapsed.

1 mark — When checking whether a player has the required items for traversing a given obstacle, the required item names must be compared via Unicode compatibility normalisation. (See the "obstacle..." section of the input file format, below, for context.)

1 mark — The input file must be readable in either UTF-8, UTF-16 or UTF-32 encoding. The filename should end in ".utf8.map", ".utf16.map" or ".utf32.map", indicating its encoding.

3. Input File Domain-Specific Language
--------------------------------------
Obtain a copy of saed2024_assignment2_input_generator.zip. This will randomly-generate input files for testing your input-parsing code. (You will need to manually modify these files, or create your own, to test your game as a whole, for reasons that will become clear when you see them.)

You must create a JavaCC grammar (.jj) file to parse the specially-formatted input file:

2 marks — Token definitions.
6 marks — Parser definitions, including logic for extracting information.

The input file starts with three "declarations" as follows:

size (10,10)
start (1,5)
goal (9,8)

Each "(r,c)" pair contains two integers: the first a row number, and the second a column number. For size, they must each be at least 1, and represent the overall number of rows and columns. In all other cases, they indicate a location within the grid, and each number must be 0 or higher, and strictly less than the number of rows/columns (as appropriate).

Line breaks and other spacing are irrelevant, so the same could be written as:

size (
10   ,10)start(
1
,5)goal   (   9   ,   8   )

Following this, there can be any number and combination of the following:

"item" declarations; e.g.:

item "Wooden Sword" {
    at (0,0), (10,5), (7,1)
    message "Use wisely."
}

These consist of word "item", followed by:

- A string in double quotes, indicating the item's name. The string can contain any characters except for a double quote itself.
- A pair of braces containing:
  - One "at" declaration, containing a comma-separated list of row/column locations, indicating where items of this name are to be placed in the grid; then
  - One "message" declaration, containing a string.

"obstacle" declarations; e.g.:

obstacle {
    at (1,1), (2,3), (3,4), (4,5)
    requires "Wooden Sword", "Toffee Apple", "Neil deGrasse Tyson"
}

These consist of the word "obstacle", followed by a pair of braces, containing:

- One "at" declaration (with the same meaning as above); then
- One "requires" declaration, containing a comma-separated list of strings (indicating items that the player must possess in order to pass through the obstacle).

"plugin" declarations; e.g.:

plugin org.example.myplugins.PluginX

These consist of the word "plugin", followed by a fully-qualified Java class name (a sequence of identifiers separated by ".").

"script" declarations; e.g.:

script {
class MyCallback(edu.curtin.game.Callback):
    def handleEvent(e):
        print(api.getSomeInfo())
}

These consist of the word "script", followed by a pair of braces "{…}", assumed to contain Python code.

You should not attempt to parse the Python code itself; rather, treat it as a free-form string. For simplicity, assume the Python code will not contain a closing "}" brace.

The above example shows the Python code without overall indentation, again for simplicity. (You may arrange for it to be indented if you like, but then your code would need to remove one level of indentation before passing it to Jython, because otherwise it won't run.)

JavaCC
------
Many JavaCC examples show a parser object being constructed with "new MyParser(System.in)". However, instead of System.in, you can alternatively pass in a Reader object (e.g., FileReader or StringReader).

Partial Marks for Partial Development
-------------------------------------
The input (DSL) file parsing and the plugins/scripts will be marked separately. You will be able to get all the marks for one without the other, if it comes to that.

To get the marks for plugins/scripts, without necessarily completing work on input file parsing, you can hard-code the information that would occur in the input file. Create a class called "TempHardCodedInput.java" and populate your data structures there. If/when you get parsing working, you can remove this file again.

On the other hand, to get the marks for parsing the input file, without necessarily completing work on plugins/scripts, just print out the parsed input file information, particularly for the "plugin" and "script" declarations. However, you must still properly parse the file (not just read it as a string), and make the information available to your core game (not just display it from the JavaCC grammar file). You can remove this output if/when you get the plugin/scripts mechanisms working.

4. Plugins and Scripts
----------------------
Your game must have mechanisms to load and run the plugins and scripts specified in the input file. You must provide an API for both plugins and scripts. You must also provide several actual plugin/script implementations for specific purposes.

4.1 Loading Mechanisms (2 marks)
--------------------------------
Plugins must be loaded and run according to "plugin" declarations, using the fully-qualified class name provided.

Plugins must be loaded dynamically (using reflection), so that users can, in principle, create and load their own plugins without modifying your code (not counting TempHardCodedInput.java, if you're using that).

Scripts must be run according to "script" declarations. Scripts should be assumed to be Python code (as understood by Jython), though you may use different scripting languages and interpreters if the unit coordinator subsequently gives permission (and hopefully instructions) for doing so.

4.2 API (4 marks)
-----------------
Plugins and scripts must have access to an API, to let them communicate with the game. The API should consist simply of a set of interface declarations. How many you have, what they are called, and what methods they contain are up to you. Overall, though, the API must provide the following:

Three different callback mechanisms, permitting a plugin/script to be notified, and take any arbitrary actions, when certain events happen:

- When the player moves (up, down, left or right);
- When the player acquires a new item;
- When the plugin/script's own menu option (for console-based games) or button (for GUI-based games) is selected.

In the last case, each plugin/script potentially gets its own separate menu option/button, for whatever purpose it wants, but only if it asks for one.

The ability to query and modify various in-game information, both on initialisation, and also from within any of its callbacks:

- The player's location;
- The player's inventory (including the ability to see which item was the most recently acquired);
- The grid size;
- Grid square contents—any item, obstacle or goal at any given location;
- Grid square visibility.

4.3 Actual Plugins and Scripts (8 marks)
----------------------------------------
You must implement 2–3 of the following as plugins, and the remaining 1–2 as Python script(s). Scripts should be embedded within a demonstration input file named "demoinput.utf8.map", to be included within your submission, in the base project directory (the same location as "gradlew").

2 marks — A plugin/script that gives the player one opportunity to "teleport" to some other random point in the grid, by selecting a special menu option or pressing a button.

If implemented as a plugin, it must be called "edu.curtin.gameplugins.Teleport".

2 marks — A plugin/script that creates a new "penalty" obstacle, adjacent to the player's location, if the player took more than 5 seconds to make their previous move.

You may decide on the exact details of this obstacle yourself. (Hint: have it call System.currentTimeMillis() on each player move, and subtract the results to determine the time between moves.)

If implemented as a plugin, it must be called "edu.curtin.gameplugins.Penalty".

2 marks — A plugin/script that, when the player acquires an item with "map" in the name, reveals the location of the goal, and of all remaining hidden items, by ensuring their specific grid squares are visible. (All other hidden grid square remain hidden.)

If implemented as a plugin, it must be called "edu.curtin.gameplugins.Reveal".

2 marks — A plugin/script that counts (a) the number of items the player acquires, and (b) the number of obstacles the player successfully traverses. When these numbers add up to 5, the plugin/script adds a special item to the player's inventory.

You may decide on the exact details of the item to be added.

If implemented as a plugin, it must be called "edu.curtin.gameplugins.Prize".

Warning: These functions are to be performed only by plugins/scripts. The game itself must provide no assistance, other than through the specific API features.

You will lose marks in this section if it's found that the core game itself is doing too much.

5. Build Requirements (5 marks)
-------------------------------
Your game must use Gradle, with separate subprojects for (at least) (a) the core game, (b) the API declarations, (c) each of your plugins. You may create more subprojects at your discretion. You will need to set up dependencies to ensure everything compiles and runs as expected.

The end result must be that:

The build user can type "./gradlew run --args="..."" to have the core game run, and be able to load the plugins/scripts.

Be aware that Gradle's "run" task changes the current directory to the subproject being run, so that if "input.utf8.map" is in the main project directory, you actually need to write "../input.utf8.map":

$ ./gradlew run --args="../input.utf8.map"

The build user can also type "./gradlew install", and run the generated start-up script to achieve the same effect. For instance, if your core subproject is called "game", then you would be able to run it as follows:

$ ./gradlew :game:install
$ game/build/install/game/bin/game input.utf8.map