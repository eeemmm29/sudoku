# Sudoku:
A sudoku project made with JavaFX by a group of two people for an Uni assignment. It’s a logic-based number-placement puzzle game with Japanese origins. The objective is to fill a 9 × 9 grid with digits so that each column, each row, and each of the nine 3 × 3 subgrids that compose the grid (also called "boxes", "blocks", or "regions") contains all of the digits from 1 to 9. At the start of the game the board is partially complete and you have to insert the numbers in empty cells. There can be only one solution for a board.
In our implementation of the game there are three difficulty settings and an arcade-game-like leaderboard that displays 5 best results.

# Gameplay:
On the main interface there are:
- Your current score
- The count of your mistakes in the current game
- A button for choosing the difficulty of the game
- The difficulty of the current game
- Leaderboard button that opens a menu that shows 5 best results so far in the database
- A pause button that stops the timer and hides all the values on the board
- The board with cells
- A New game button that starts a new game (With the current difficulty set)
- A note button
- A hint button that reveals a random cell in the board. This decreases your score by 10 (The score doesn’t go below 0)
- The number buttons that you can use to insert values in the cells without a keyboard if you so prefer.
If you insert a correct value it turns blue and you can no longer edit the cell. If it’s wrong, it turns red and you can still correct it (You also lose 10 points every time you make a mistake. The score doesn’t go below 0). If you use a hint the cell revealed turns green. Pressing the note button enters the note mode that lets you put notes on cells that are not checked for correctness.

After you complete the game there is a popup with a congratulatory message and a textfield where you have to write your name so your result can be included in the database. You can also choose not to leave your result in the database.

# Task distribution:
I was behind most of the backend and my groupmate was behind most of the frontend. I implemented the main logic for how the application works, manages user input and sends results to the database. (groupmate name here) made the visuals, the grid, included the images and added some buttons + implemented the logic for some of them. We were communicating a lot about the project both in person and through online calls.

# Technologies/techniques used:
The main parts of the program were taken from here:
https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaGame_Sudoku.html (Be aware that the current program is different in many ways compared to the one in the website.)
Though the implementation here is made with Swing and not JavaFX. Even though I had to convert many things from Swing to JavaFX, starting with this made things much easier because it made me realize that separating everything in many files with each having one* class is very convenient.

One problem is that this implementation is using a hard-coded sudoku board. I found this page on GeeksForGeeks with a sudoku board generator:
https://www.geeksforgeeks.org/program-sudoku-generator/
I then made a separate file for a generator class that uses this algorithm and connected it to the first program.

The database is implemented using SQLite, which we were learning about for a while in Web Development classes. It wasn’t too hard, but it was interesting.

For the frontend Scene builder was used. It’s not too fancy but using it was fun and gave us a lot of valuable experience.

For project synchronization and better communication we used GitHub (it was for the first time for me).

# Challenges faced and how they were resolved:
The main challenge at first was connecting the backend to the frontend because we were making them separately. My program was creating a board of new TextFields in the corner of the screen and I just couldn’t figure out how to connect them with the TextFields in the FXML file that my groupmate gave me. I then reached out to google and looked around how people were making sudoku games with JavaFX. After some time I found out about a class called GridPane in which you can insert any other objects and they will be displayed in a grid you can easily manipulate in Scene Builder. With this knowledge I replaced all the existing TextFields (That were just manually placed as a grid at first) with a GridPane and made it so it’s filled with Cells inside the program.

There were more challenges along the way, but another biggest one was how the input is handled and how the color of the cell changes depending on it. For example at one point after making a mistake the cell would turn red as expected, but when making a correct guess the color would stay as red. Or after starting a new game the color of a cell would stay the same and not revert to black.
The first problem was solved by just editing one line in the CSS file. I’ve never heard about it before, but there exists a rule called important which makes it so the property/value set is more important than normal. So when adding a class with a color property like this it overrides all the other ones.
The second problem was solved by just clearing the style classes and adding a default cell class again to all the cells every time a new game starts. I don’t think this is very pretty, but hey, it works. Actually the first problem was solved like this too (clearing and adding a default class on every check, so everytime the user inputs something into the grid), but I wanted to make it prettier.

Later when implementing the note button: at first it wasn’t putting your note in the corner of the cell but directly inside the TextField. At first it was seemingly working fine, but there was one problem: If you tried to ender the same value in the cell while not in the note mode the function changed() in the ChangeListener won’t be called because it’s only called when there is a change.
This was resolved by adding a label for every cell inside the initialize() function, setting the text of the cell to “” and setting the text of the corresponding label to the new number. I don’t think this is very pretty, but it works. (We were working on this the day before the deadline actually)

Overall we feel like making this project was fun and it made us learn and experience a lot of new things that will come in handy in the future.
