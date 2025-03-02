# Scrabble
An attempt at recreating the classic board-and-tile game, trying to be as accurate and faithful to the official rules as possible. 

![Image](https://github.com/user-attachments/assets/58c90506-6f60-4a3e-b0e2-f246ce9ea884)

![Image](https://github.com/user-attachments/assets/765b7af9-44ca-48d8-9fdc-4daeacd6a782)

Features include the options to pass, exchange, see inventory/bag and reset. Blank tiles and the two end conditions -- playing out and six consecutive scoreless turns -- exist.

## How it's made

Java Swing was used for the GUI, with a 2D array acting as the board. Valid answer choices are queried to a SQL database with the "dictionary" table being the official Scrabble dictionary. The "letters" table holds a relation between the letter, its index (0 - 26), its points, its amount, and its URL for the picture.

## Installation

The src file contains all the code. The pictures file contains the downloads to pictures that become ImageIcons in the program; the absolute download path for each picture needs to be altered for the program to run. "ScrabbleMascot" and "ScrabbleStar" can be found within the code, in the StartingMenu and Pictures class respectively. The rest of the pictures are to be edited within the letters.csv found in the db file. 

JDBC is used to connect Java with SQL -- the JDBC to install depends on the RDBMS that you use. I personally used PostgreSQL. Code will need to be altered in Pictures, searchSQL, and Player -- specifically the parameter in the `con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Scrabble","postgres","password");` line. 

## Struggles

As it was my first semi-complex project, I didn’t put much initial thought into the hierarchical structure of the classes. Though I restructured halfway through, it still felt suboptimal. In the future, it would be beneficial for me to plan out the overall structure of the code before diving in, especially as I begin to tackle more complicated projects.

It felt awkward having the GUI class containing most of the gameplay logic with, well, the GUI — but I frankly didn’t know how to cleanly separate the two with Swing in this project. 

I struggled with lacking the foresight to be able to determine when I should hard code something and when to make it a method. 

At the later stages, I often caught myself getting lost in my own code. In hindsight, I had a bad habit of calling another method in a loosely related method, as well as having a method perform more than one responsibility/function. I realized that I am still lacking in writing clean and effective code.

