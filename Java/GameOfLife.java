/**
 * 
 * Conway's Game of Life on a toroidal surface
 * Includes functionality for loading, editing, and saving grids
 * 
 **/

import processing.core.PApplet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class GameOfLife extends PApplet {
	
	// worldState holds current state, buff is temporary storage for subsequent state
	private int[][] worldState, buff;
	private final int ALIVE = 1, DEAD = 0;
	private Random r;
	
	// Game parameters
	private int boardLength = 180;
	private int boardHeight = 100;
	private double initProb = 0.15;
	private boolean gridLines = true;
	private int dim = 6;
	private int l = boardLength * dim, h = boardHeight * dim;
	private String startCondition = ""; //"loadBoard", "rand"
	
	// Control variables
	private boolean pause = false;
	private boolean flip = false;
	private int lastI, lastJ;
	private char lastShape;
	private int rotate = 0;
	private int eraserX = 8;
	private int eraserY = 8;
	
	// Filepaths for input / output csv files
	private String inPath = "/DIRECTORY/CellularAutomata/logic_Gates/NAND.csv";
	private String outPath = "/DIRECTORY/CellularAutomata/logic_Gates/NAND.csv";
	
	public static void main(String[] args) {
		PApplet.main("GameOfLife");
	}
	
	public void settings() {
		size(l, h);
		noSmooth();
	}
	
	public void setup() {
		
		// Initialize arrays and Random object, randomly populate arrays
		worldState = new int[boardLength][boardHeight];
		buff = new int[boardLength][boardHeight];
		r = new Random();
		
		switch (startCondition) {
			case "loadBoard": loadBoard(); break;
			case "rand": fillRandom(); break;
			default: break;
		}
		
		frameRate(25);
		
	}
	
	public void draw() {
		
		if (gridLines) stroke(35);
		else noStroke();
		
		// Fill buffer array according to current state / rule set
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (!pause) fillBuff(i,j);
			}
		}
		
		// Update worldState array and draw to GUI
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				fill(0,0,0);
				if (!pause) worldState[i][j] = buff[i][j];
				if (worldState[i][j] == ALIVE) fill(255,255,255);
				rect(i*dim, j*dim, dim, dim);
			}
		}
		
	}
	
	// Update buffer array
	public void fillBuff(int i, int j) {
		int neighbors, leftInd, rightInd, downInd, upInd;
		
		// Get neighbor indices (cells live on a 2D torus)
		leftInd = (((i - 1) % boardLength + boardLength) % boardLength);
		rightInd = (((i + 1) % boardLength + boardLength) % boardLength);
		downInd = (((j - 1) % boardHeight + boardHeight) % boardHeight);
		upInd = (((j + 1) % boardHeight + boardHeight) % boardHeight);
		
		// Calculate neighbor count
		neighbors = worldState[leftInd][downInd] + worldState[leftInd][j]
				+ worldState[leftInd][upInd] + worldState[i][downInd]
				+ worldState[i][upInd] + worldState[rightInd][downInd]
				+ worldState[rightInd][j] + worldState[rightInd][upInd];
		
		// Update bufferArray based on classic rule set
		if (neighbors < 2) buff[i][j] = DEAD;
		if (neighbors == 2) buff[i][j] = worldState[i][j];
		else if (neighbors == 3) buff[i][j] = ALIVE;
		else if (neighbors > 3) buff[i][j] = DEAD;		
		
	}
	
	/**
	 *  User controls: pause ('enter'/'return'), clear grid ('c'), save / load grid ('s' / 'l'),
	 *  randomly populate grid ('r'), place glider gun, eater, or "detector" ('g','e','d'), 
	 *  undo last placed pattern ('u'), rotate pattern 90 degrees ('t'), flip pattern ('f'),
	 *  move pattern up/down/left/right (arrow keys), erase block ('o'), toggle grid lines ('x'),
	 *  toggle individual cell (mouse click), 
	 *  
	 */
	public void keyPressed() {
		if (key == RETURN || key == ENTER) {
			pause = !pause;
			if (pause) noLoop();
			else loop();
		}
		else if (key == 'c' || key == 'C') {
			if (pause) {
				worldState = new int[boardLength][boardHeight];
				redraw();
			}
		}
		else if (key == 's' || key == 'S') {
			if (pause) saveBoard();
		}
		else if (key == 'l' || key == 'L') {
			if (pause) loadBoard();
		}
		else if (key == 'r' || key == 'R') {
			if (pause) {
				worldState = new int[boardLength][boardHeight];
				fillRandom();
				redraw();
			}
		}
		else if (key == 'g' || key == 'G' ||key == 'e' || key == 'E' || key == 'd' || key == 'D') {
			if (pause) {
				loadShape((int) (map(mouseX, 0, l, 0, boardLength)),
						(int) map(mouseY, 0, h, 0, boardHeight),
						key, rotate, flip, false);
				redraw();
			}
		}
		else if (key == 'u' || key == 'U') {
			loadShape(lastI, lastJ, lastShape, rotate, flip, true);
			redraw();
		}
		else if (key == 't' || key == 'T') {
			rotate = (rotate + 1) % 4;
		}
		else if (key == 'f' || key == 'F') {
			flip = !flip;
		}
		else if (keyCode == UP || keyCode == DOWN || keyCode == LEFT || keyCode == RIGHT) {
			loadShape(lastI, lastJ, lastShape, rotate, flip, true);
			if (keyCode == DOWN) loadShape(lastI, (lastJ+1)%boardHeight, 
					lastShape, rotate, flip, false);
			else if (keyCode == UP) loadShape(lastI, 
					((lastJ-1)%boardHeight+boardHeight)%boardHeight, lastShape, rotate, flip, false);
			else if (keyCode == LEFT) loadShape(((lastI-1)%boardLength+boardLength)%boardLength, 
					lastJ, lastShape, rotate, flip, false);
			else if (keyCode == RIGHT) loadShape(lastI + 1, lastJ, 
					lastShape, rotate, flip, false);
			redraw();
		}
		else if (key == 'o' || key == 'O') {
			erase((int) (map(mouseX, 0, l, 0, boardLength)),
					(int) (map(mouseY, 0, h, 0, boardHeight)), eraserX, eraserY);
			redraw();
		}
		else if (key == 'x' || key == 'X') {
			gridLines = !gridLines;
		}
	}
	
	// Manually toggle individual cells (game must be paused)
	public void mousePressed() {
		if (pause) {
			int x =  (int) (map(mouseX, 0, l, 0, boardLength));
			int y =  (int) (map(mouseY, 0, h, 0, boardHeight));
			worldState[x][y] = (worldState[x][y] + 1) % 2;
			System.out.println("i,j: " + x + ", " + y);
			redraw();
		}
	}
	
	// Randomly populate worldState array
	public void fillRandom() {
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (r.nextFloat() < initProb) worldState[i][j] = ALIVE;				
			}
		}
	}
	
	// Loads saved grid from CSV file
	public void loadBoard() {
		Scanner scan = null;
		try { scan = new Scanner(new File(inPath)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		String[] data = scan.nextLine().split(",");
		int xShift = 0, yShift = 0;
		if (Integer.parseInt(data[0]) < boardLength && Integer.parseInt(data[1]) < boardHeight) {
			xShift = (int) (map(mouseX, 0, l, 0, boardLength));;
			yShift = (int) (map(mouseY, 0, h, 0, boardHeight));;
		}
		if (Integer.parseInt(data[0]) <= boardLength && Integer.parseInt(data[1]) <= boardHeight) {
			while (scan.hasNextLine()) {
				data = scan.nextLine().split(",");
				if (data.length < 3) break;
				worldState[(Integer.parseInt(data[0])+xShift)%boardLength][(Integer.parseInt(data[1])+yShift)%boardLength]
						= Integer.parseInt(data[2]);
			}
			redraw();
		}
		else System.out.println("Saved board has incorrect dimensions");
	}
	
	// Saves grid to CSV file
	public void saveBoard() {
		File file = null;
		PrintWriter print = null;
		file = new File(outPath);
		try { print = new PrintWriter(file); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		print.println(boardLength + "," + boardHeight);
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight ;j++) {
				print.println(i + "," + j + "," + worldState[i][j]);
			}
		}
		print.print("\b");
		print.close();
	}
	
	// Generate a pattern at specified coordinates, or clear a grid the size of the pattern
	public void loadShape(int i, int j, char shape, int rotate, boolean flip, boolean clear) {
		lastI = i; 
		lastJ = j; 
		lastShape = shape;
		int[][] shapeArr = null;
		if (shape == 'g' || shape == 'G') shapeArr = Shapes.getGliderGun(); 
		else if (shape == 'e' || shape == 'E') shapeArr = Shapes.getEater();
		else if (shape == 'd' || shape == 'D') shapeArr = Shapes.getDetector();
		for (int k = 0; k < shapeArr.length; k++) {
			for (int n = 0; n < shapeArr[0].length; n++) {
				int curr = shapeArr[k][n];
				if (flip) curr = shapeArr[shapeArr.length - k - 1][n];
				if (clear) curr = DEAD;
				if (rotate == 0) worldState[(i+k)%boardLength][(j+n)%boardHeight] = curr;
				else if (rotate == 1) worldState[(i+n)%boardLength][(j+k)%boardHeight] = curr;
				else if (rotate == 2)
					worldState[((i-k)%boardLength+boardLength)%boardLength][((j-n)%boardHeight+boardHeight)%boardHeight] = curr;
				else if (rotate == 3)
					worldState[((i-n)%boardLength+boardLength)%boardLength][((j-k)%boardHeight+boardHeight)%boardHeight] = curr;
			}
		}
	}
	
	// Erase cells
	public void erase(int x, int y, int dI, int dJ) {
		for (int i = 0; i < dI; i++) {
			for (int j = 0; j < dJ; j++) {
				worldState[(x+i)%boardLength][(y+j)%boardHeight] = DEAD;
			}
		}	
	}
		
}
