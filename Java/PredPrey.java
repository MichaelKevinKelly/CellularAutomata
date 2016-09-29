/**
 * 
 * Game of Life variant with probabilistic cellular automata
 * 
 **/

import processing.core.PApplet;
import java.util.Random;

public class PredPrey extends PApplet {
	
	// Current predator and prey states, buffers for temporary storage
	private int[][] preyState, predState, preyBuf, predBuf;
	private final int ALIVE = 1, DEAD = 0;
	private final int UP = 3, DOWN = 5, LEFT = 7, RIGHT = 9;
	private Random r;
	
	// Game parameters (additional game parameters in rules)
	private int boardLength = 200;
	private int boardHeight = 125;
	private double initPrey = 0.15;
	private double initPred = 0.005;
	private int maxN = 5;
	private boolean gridLines = false;
	private int dim = 4;
	private int l = boardLength * dim, h = boardHeight * dim;
	
	// Control variables
	private boolean pause = false;
	private int pressCount = 0;
	
	public static void main(String[] args) {
		PApplet.main("PredPrey");
	}
	
	public void settings() {
		size(l, h);
		noSmooth();
	}
	
	public void setup() {
		
		// Initialize buffers and Random object, randomly populate state arrays
		preyState = new int[boardLength][boardHeight];
		predState = new int[boardLength][boardHeight];
		preyBuf = new int[boardLength][boardHeight];
		predBuf = new int[boardLength][boardHeight];
		r = new Random();
		fillRandom();
		
		// Determine frame rate
		frameRate(55);
		
	}
	
	public void draw() {
		
		if (gridLines) stroke(48);
		else stroke(0);
		
		// Fill buffer array according to current state / rule set
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (!pause) fillBuff(i,j);
			}
		}
		
		// Update worldState array and render
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				fill(0,0,0);
				if (!pause) {
					predState[i][j] = predBuf[i][j];
					preyState[i][j] = preyBuf[i][j];
				}
				if (predState[i][j] >= ALIVE && preyState[i][j] == ALIVE) fill(128,0,0); 
				else if (predState[i][j] >= ALIVE) fill(255,0,0);
				else if (preyState[i][j] == ALIVE) fill(255,255,255);
				rect(i*dim, j*dim, dim, dim);
			}
		}
		
	}
	
	// Update buffer array and population counts
	public void fillBuff(int i, int j) {
		int prey, predators, leftInd, rightInd, downInd, upInd;
		
		// Get neighbor indices (cells live on a 2D torus)
		leftInd = (((i - 1) % boardLength + boardLength) % boardLength);
		rightInd = (((i + 1) % boardLength + boardLength) % boardLength);
		downInd = (((j - 1) % boardHeight + boardHeight) % boardHeight);
		upInd = (((j + 1) % boardHeight + boardHeight) % boardHeight);
		
		// Calculate prey count
		prey = preyState[leftInd][downInd] + preyState[leftInd][j]
				+ preyState[leftInd][upInd] + preyState[i][downInd]
				+ preyState[i][upInd] + preyState[rightInd][downInd]
				+ preyState[rightInd][j] + preyState[rightInd][upInd];
		
		// Calculate predator count
		predators = predState[leftInd][downInd]%2 + predState[leftInd][j]%2
				+ predState[leftInd][upInd]%2 + predState[i][downInd]%2
				+ predState[i][upInd]%2 + predState[rightInd][downInd]%2
				+ predState[rightInd][j]%2 + predState[rightInd][upInd]%2;
		
		// Update bufferArray based on variant rule set
		double random = r.nextFloat();
		if (predState[i][j] > ALIVE) predBuf[i][j] = DEAD;
		if (predState[leftInd][j] == RIGHT || predState[rightInd][j] == LEFT 
				|| predState[i][upInd] == DOWN || predState[i][downInd] == UP) {
			predBuf[i][j] = ALIVE;
		}
		if (predState[i][j] == ALIVE && preyState[i][j] == ALIVE) preyBuf[i][j] = DEAD;
		if (predators > 0) {
			if (predators == 1 && prey > 3) predBuf[i][j] = ALIVE;
			else {
				preyBuf[i][j] = DEAD;
				if (predators > 3) predBuf[i][j] = DEAD;
				if (prey == 0 && random < 0.25) predBuf[i][j] = DEAD;
			}
		}
		else if (predState[i][j] == ALIVE && prey == 0 && preyState[i][j] == DEAD) {
			if (random < 0.95) {
				predBuf[i][j] = DEAD;
				if (random < 0.15) predBuf[i][j] = LEFT;
				else if (random < 0.30) predBuf[i][j] = RIGHT;
				else if (random < 0.45) predBuf[i][j] = UP;
				else if (random < 0.60) predBuf[i][j] = DOWN;
				else predBuf[i][j] = ALIVE;
			}
			else predBuf[i][j] = DEAD;
		}
		else {
			if (prey < 2) preyBuf[i][j] = DEAD;
			else if (prey == 3) preyBuf[i][j] = ALIVE;
			else if (prey > maxN) preyBuf[i][j] = DEAD;
			if (random < 0.05) preyBuf[i][j] = DEAD;
		}
	}
	
	/**
	 *  User controls: pause ('enter'/'return'), clear grid ('c'), randomly populate grid ('r'),
	 *  toggle grid lines ('x'), toggle individual cell (mouse click)
	 *  
	 */
	public void keyPressed(){
		if (key == RETURN || key == ENTER) {
			pause = !pause;
			if (pause) noLoop();
			else loop();
		}
		else if (key == 'c' || key == 'C') {
			if (pause) {
				preyState = new int[boardLength][boardHeight];
				predState = new int[boardLength][boardHeight];
				redraw();
			}
		}
		else if (key == 'r' || key == 'R') {
			if (pause) {
				preyState = new int[boardLength][boardHeight];
				predState = new int[boardLength][boardHeight];
				fillRandom();
				redraw();
			}
		}
		else if (key == 'x' || key == 'X') {
			gridLines = !gridLines;
		}
	}
	
	// Manually toggle through states for specific cell (game must be paused)
	public void mousePressed() {
		if (pause) {
			int i =  (int) (map(mouseX, 0, l, 0, boardLength));
			int j =  (int) (map(mouseY, 0, h, 0, boardHeight));
			pressCount = (pressCount + 1) % 3;
			System.out.println(i + ", " + j);
			predState[i][j] = 0;
			preyState[i][j] = 0;
			if (pressCount == 1)  {
				predState[i][j] = predBuf[i][j] = 1;
			}
			else if (pressCount == 2) {
				preyState[i][j] = preyBuf[i][j] = 1;
			}
			redraw();
		}
	}
	
	// Randomly populate state arrays
	public void fillRandom() {
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				double random = r.nextFloat();
				if (random < initPred) predState[i][j] = ALIVE;
				else if (random < initPred + initPrey) preyState[i][j] = ALIVE;
			}
		}
	}
		
}
