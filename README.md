Cellular Automata
===========

This repo contains code for three variants of Conway's Game of Life, two implemented in Java and Processing and one in C# and Unity.

## The Game of Life

The main GameOfLife.java file runs Life on a 2D toroidal array, and includes some functionality for loading, editing, and saving patterns. Basic patterns are included in the Shapes class, and entire grids can be loaded from and/or saved to .csv files. Grid editing can only take place while the game is paused.

Parameters such as the frame rate, grid dimensions, cell size, and initial probability that a cell will be alive can be easily adjusted in the source code.

###User Controls
* Pause ('enter' / 'return')
* Clear grid ('c')
* Load / save grid ('l' / 's'),
* Randomly populate grid ('r')
* Place basic pattern at cursor location (glider gun, eater, "detector" : 'g', 'e', 'd')
* Undo last pattern placement ('u')
* Rotate the next pattern to be placed on the grid clockwise by 90 degrees ('t')
* Flip the next pattern to be placed on the grid ('f')
* Move last pattern placed on the grid up/down/left/right one cell (arrow keys)
* Erase block ('o')
* Toggle grid lines ('x')
* Toggle individual cell state (mouse click)
 
I added these controls in order to develop and edit more complex patterns. For example, I used them to implement a number of logic gates in Life, which can be found in the logicGates directory. They are based largely (some entirely) on Jean-Philippe Rennard's LogiCell and the implementations he describes.

Far larger and more complex patterns can be made in Life, such as large "breeders" and "puffer trains." Incredibly, Paul Rendell has even designed a universal Turing machine in Life. The code provided here, however, was written primarily for clarity and lacks any significant optimizations for speed. Expect performance to drop off pretty sharply as grid size increases.
 
## Predator-Prey

PredPrey.java contains a variant of Life using probabilistic, eight-state cellular automata. The four basic states are predator, prey, both, or dead (the penultimate state might be interpreted as the predator "eating" the prey). A cell in the predator state can also be in one of four additional states: up, down, left, or right. Cells in these secondary states die in the next iteration, while one of their neighbors enters the predator state. Cells in the predator state are rendered in red, those in the prey state in white, and those in both the predator and prey states in maroon. Dead cells are rendered in black.

###Rules

####Predation:
* If a cell is in both the predator and prey state, the cell leaves the prey state (i.e. the prey dies)
* If there is one predator but three or fewer prey in a prey cell's neighborhood, the prey dies
* If there is more than one predator in a prey cell's Moore neighborhood, the prey dies

####Reproduction:
* If there are more than three prey and only one predator in a cell's neighborhood, the cell enters (or maintains) the predator state (i.e. the predator becomes alive)
* If there are exactly three prey and no predators in a cell's neighborhood, the cell enters (or maintains) the prey state

####Overcrowding / Starvation:
* If the predator count for a predator cell's neighborhood exceeds three, the predator dies
* If there is a positive predator count and no prey in a predator cell's neighborhood, the predator dies with probability 0.25
* If the prey count for a prey cell exceeds the maximum tolerated level (the default is five), the prey dies 

####Isolation / Starvation:
* If there are no prey and no predators in the area, the predator enters a transition state (up, down, left, or right) with probability 0.6, or dies with probability 0.05. A cell in the transition state dies in the next iteration, while the appropriate neighbor (i.e. the upper, lower, left, or right neighbor) enters the predator state in the next iteration 
* If there are fewer than two prey in a prey cell's neighborhood, the prey dies

####Death Rate:
* If not killed via predation, overcrowding, or isolation, prey cells enter the death state with probability 0.05

For certain sets of parameter values, the predator and prey populations remain in what appears to be a dynamic equilibrium.

Adjustments to these parameter values can upset this equilibrium and lead to highly divergent results. For example, increasing the maximum number of prey-neighbors tolerated in a cell's Moore neighborhood from 5 to 7 generally leads to a rapid proliferation of prey cells trailed by a surge in predator cells, which nearly wipes out the prey cells. The resulting scarcity drives a severe die-off in predator cells, and if any prey cells remain they then populate the entire grid. If not, the grid is left empty.

## Torus Visualization

GameOfLife_TorusVisual.java and PredPrey_TorusVisual.java contain the classic formulation of Life and my Predator-Prey game, respectively, but rendered in 3D on the surface of a torus, rather than simply on a 2D toroidal array.

Processing does not provide a torus primitive, so I wrote a short method that generates a set of vertices for the torus based on the board dimensions and torus parameters given. Each vertex maps to a single cell in the state array. The draw phase iterates through the vertices, selecting for each the three other vertices that define the corresponding cell and then rendering the resulting rectangular strip with a color determined by the state array.

## Game of Life - 3D

The C# directory of this repo contains two scripts to implement a three-dimensional version of the Game of Life in Unity and in VR using Google Cardboard. To run in Unity, just attach the Life.cs file to an empty GameObject and set the prefab variable to a cube. Make sure to scale the cube to an appropriate size for the chosen cell size (I scaled the cube's dimensions by 3 and set the cell size to 3.1).

I also used the Google VR SDK to create a VR version of the game. To do this it is necessary first to download the SDK and import it into your project, then to drag the GvrViewerMain prefab into your scene, and then finally to update the Build Settings to match your target platform. I wrote the Motion.cs script to allow the user to move around and examine the various patterns that emerge in the game - attaching the script to the main camera causes it to move at a constant speed in the direction of the user's gaze. Pressing the Cardboard unit's button toggles the motion on and off.
