# maze-generator

This project is intended as a learning project for learning Clojure by implementing Maze algorithms from the Book [Mazes For Programmers](http://www.mazesforprogrammers.com) by James Buck.



## Installation

Checkout the source and open up a Leiningen REPL.

## Usage

To print a maze, open up a REPL in the project folder and evaluate One of the following commands :

```
(print-maze binary-tree 10 10)
```
Example output:
```
+---+---+---+---+---+---+---+---+---+---+
|                                       |
+   +---+---+---+---+---+   +   +---+   +
|   |                       |   |       |
+---+   +---+   +---+---+   +   +---+   +
|       |       |           |   |       |
+---+---+   +---+---+---+   +---+   +   +
|           |               |       |   |
+   +   +   +   +---+---+---+---+   +   +
|   |   |   |   |                   |   |
+---+---+   +   +   +---+---+   +---+   +
|           |   |   |           |       |
+   +   +   +---+   +---+   +   +---+   +
|   |   |   |       |       |   |       |
+---+---+---+---+---+---+---+---+---+   +
|                                       |
+   +   +---+---+   +---+---+   +---+   +
|   |   |           |           |       |
+   +   +---+   +---+   +---+   +   +   +
|   |   |       |       |       |   |   |
+---+---+---+---+---+---+---+---+---+---+
```

maze-to-string takes 2 arguments: A maze grid and a row size.


Algorithms So far:

#### Binary tree
```
(binary-tree grid-size)
```
Creates a binary tree maze.  This algorithm is the simplest and has a diagonal bias up towards the northeast side of the grid.

#### Recursive Backtracker
```
(recursive-backtracker width height)
```
Creates a recursive backtracker maze.  This algorithm is more complex than binary tree and creates long winding passages.  I find these mazes to be more aesthetically pleasing.

#### Example

Enter this command into a REPL
```
(print-maze recursive-backtracker 20 20)
```
Example output:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|                           |           |   |                               |   |
+   +---+---+---+---+---+   +---+   +   +   +   +---+   +---+---+---+---+   +   +
|       |                       |   |   |       |       |       |       |   |   |
+   +---+   +---+---+---+---+   +---+   +   +---+   +---+   +   +   +   +   +   +
|   |       |           |       |   |   |   |       |       |   |   |   |   |   |
+   +   +---+   +---+   +   +---+   +   +---+   +---+   +---+   +   +   +   +   +
|   |       |   |       |           |           |       |           |   |   |   |
+   +---+   +   +   +---+---+---+   +---+---+---+   +---+---+---+   +---+   +   +
|       |   |   |   |               |           |               |   |       |   |
+---+---+   +   +   +---+---+---+---+   +---+   +   +---+---+   +   +   +---+   +
|       |       |       |       |       |   |       |       |   |   |   |       |
+   +   +---+---+---+   +   +   +   +---+   +---+---+---+   +   +---+   +   +   +
|   |   |               |   |       |       |           |   |           |   |   |
+   +   +   +---+   +---+   +---+---+   +   +   +---+   +   +---+---+---+---+   +
|   |       |       |       |           |       |   |               |           |
+   +---+---+   +---+   +---+---+---+   +---+---+   +---+---+---+   +   +---+   +
|   |       |       |       |           |                       |   |   |       |
+   +   +   +---+---+---+   +   +---+---+   +---+---+---+---+   +   +   +---+---+
|   |   |       |       |   |   |   |       |                   |   |           |
+   +   +---+   +   +   +   +   +   +   +---+   +   +---+---+---+   +---+---+   +
|   |       |       |       |   |   |   |       |   |           |           |   |
+   +---+   +---+---+---+---+   +   +   +   +---+---+   +---+   +---+---+   +   +
|       |   |                   |       |   |               |   |           |   |
+   +   +   +   +---+---+---+---+---+---+   +   +---+---+---+   +   +---+---+   +
|   |   |   |   |       |               |       |               |           |   |
+   +   +   +   +---+   +   +---+   +   +---+---+   +---+---+   +---+---+   +   +
|   |   |       |       |   |       |               |       |   |       |   |   |
+   +   +---+---+   +---+   +   +---+---+---+---+   +   +   +   +   +   +   +   +
|   |                       |               |       |   |   |       |   |   |   |
+   +---+---+---+---+---+---+---+---+---+   +   +---+   +   +   +---+---+   +   +
|       |       |                       |   |       |   |   |   |           |   |
+---+   +   +   +---+---+   +   +---+   +   +   +---+   +   +   +   +---+---+   +
|       |   |           |   |       |   |   |   |       |   |   |   |           |
+   +---+   +---+---+   +---+   +   +   +   +   +   +---+   +---+   +   +---+   +
|           |       |       |   |   |   |   |   |       |       |       |       |
+---+---+---+   +   +---+   +---+   +---+   +   +---+   +---+   +---+---+   +---+
|           |   |           |       |       |   |       |           |       |   |
+   +---+   +   +---+---+---+   +   +   +---+---+   +---+   +---+---+   +---+   +
|       |                       |   |                   |                       |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```

#### More Examples
##### Aldous Broder
```
(print-maze aldous-broder 10 10)
```
##### Wilson's
```
(print-maze wilson 10 10)
```

##### Recursive Walk
```
(print-maze recursive-walk 10 10)
```

## License

MIT License

Copyright (c) 2024 JDFight

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
