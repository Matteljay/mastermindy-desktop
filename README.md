# MasterMindy
## The customizable Mastermind clone - desktop edition

- License: MIT License
- Author: Matteljay
- Language: Java
- IDE: Eclipse
- Homepage: https://github.com/Matteljay


## Screenshots

[MacOS Sierra](https://github.com/Matteljay/mastermindy-desktop/blob/master/screenshots/macos10.png)
[Windows 10](https://github.com/Matteljay/mastermindy-desktop/blob/master/screenshots/win10.png)


## About

MasterMindy is based on Mastermind, a turn-based code breaking game. The app will generate a secret code for you to deduce.
You will only receive minimal hints. A black hint means you positioned a pawn perfectly... but which one? A white hint
means one of your selected pawns is correct but in the wrong place. Play alone or with friends to try and solve this puzzle.
You can easily change the difficulty on the settings page.


Further reading about the history, math & strategy of the game:
- <https://en.wikipedia.org/wiki/Mastermind_(board_game)>
- <http://mathworld.wolfram.com/Mastermind.html>
- <https://www.youtube.com/watch?v=XX5TlB6xT3M>


## Game Features

- Scalable to all window sizes and aspect ratios
- Flexible amount of pawn fields
- Flexible assortment of pawn colors to choose from
- Difficulty change for allowing duplicate colors in the secret
- Ability to show (collision sensitive) startup hints
- Various time and turn limiting capabilities
- Drag & drop pawns on click
- Uniquely named pawns for color blind people 
- Bitcoin donation ability :)


## Coding Details

- Cross-compatible with any operating system (Windows, MacOS, Linux)
- Layout: 22 classes with an average length of 100 lines per class
- [Java-Swing](https://en.wikipedia.org/wiki/Swing_(Java)) dynamic JComponent placement & GUI construction during runtime
- A terminal/console UI version is hidden within the code, consider it an easter egg for you to find and run
- For Android mobile devices, see project [MasterMindy-android](https://github.com/Matteljay/mastermindy)


## How to run

- Download the .jar file from the [releases](https://github.com/Matteljay/mastermindy-android/releases) page.

- Linux users: you probably already have the required [JRE](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
Try running the .jar file in a terminal window with the command `java -jar ~/Downloads/nameOfJarFile.jar`.
If you don't have the JRE, install it for your specific Linux distribution. 

- Windows or MacOS users: find out if you already have the JRE by reading [this](https://www.java.com/en/download/help/version_manual.xml).
If not, get the [JRE](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and double-click the .jar file.


## Contact info & donations

See the [contact](CONTACT.md) file.


