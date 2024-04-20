# maze-generator

This project is intended as a learning project for learning Clojure by implementing Maze algorithms from the Book [Mazes For Programmers](http://www.mazesforprogrammers.com) by James Buck.



## Installation

Checkout the source and open up a Leiningen REPL.

## Usage

To print a maze, open up a REPL in the project folder and evaluate One of the following commands :

```
(println (maze-to-string (binary-tree 10) 10))
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
(recursive-backtracker maze-grid starting-cell-id)
```
Creates a recursive backtracker maze.  This algorithm is more complex than binary tree and creates long winding passages.  I find these mazes to be more aesthetically pleasing.

#### Example
```
maze-generator.core=> (println (maze-to-string (recursive-backtracker (create-grid 20) (rand-int 400)) 20))
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
...

### Bugs
Right now, The only grid type supported is a perfect square.  There is a bug with assigning cell-id to non-square grids.
...

## License

Copyright © 2024 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
