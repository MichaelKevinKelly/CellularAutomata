/**
 * 
 * Game of Life variant with probabilistic cellular automata, visualized on a torus 
 * 
 **/

import processing.core.PApplet;
import processing.core.PVector;
import java.util.Random;

public class PredPrey_TorusVisual extends PApplet {
	
	// Current predator and prey states, buffers for temporary storage
	private int[][] preyState, predState, preyBuf, predBuf;
	private final int ALIVE = 1, DEAD = 0;
	private final int UP = 3, DOWN = 5, LEFT = 7, RIGHT = 9;
	private Random r;
	
	// Torus parameters / data
	private PVector vertices[][];
	private float majorRad = 150;
	private float minorRad = 80;
	private float rotX, rotY, rotZ;
	
	// Game parameters
	private int boardLength = 220;
	private int boardHeight = 150;
	private double initPrey = 0.15;
	private double initPred = 0.005;
	private boolean gridLines = false;
	private int maxN = 5;
	private int l = (int) (boardLength * 3), h = (int) (boardHeight * 3.5);
	
	// Control variable
	private boolean pause = false;
	
	public static void main(String[] args) {
		PApplet.main("PredPrey_TorusVisual");
	}
	
	public void settings() {
		size(l, h, "processing.opengl.PGraphics3D");
		noSmooth();
	}
	
	public void setup() {
		
		// Initialize arrays and Random object, randomly populate arrays, create torus vertices
		preyState = new int[boardLength][boardHeight];
		predState = new int[boardLength][boardHeight];
		preyBuf = new int[boardLength][boardHeight];
		predBuf = new int[boardLength][boardHeight];
		vertices = new PVector[boardLength][boardHeight];
		r = new Random();
		fillRandom();
		createVertices();
		
		frameRate(20);
		if (!gridLines) noStroke();
		rotX = PI/350;
		rotY = PI/400;
		rotZ = PI/200;
		
	}
	
	public void draw() {
		
		lights();
		background(245, 245, 255);
		
		translate(width/2, height/2, -100);
		rotateX(frameCount*rotX);
		rotateY(frameCount*rotY);
		rotateZ(frameCount*rotZ);
		
		// Fill buffer array according to current state / rule set
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (!pause) fillBuff(i,j);
			}
		}
		
		// Update worldState array and draw to GUI
		int rightInd, upInd;
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
				
				rightInd = (i + 1) % boardLength;
				upInd = (j + 1) % boardHeight;
			    
				beginShape(QUAD_STRIP);
				vertex(vertices[i][j].x, vertices[i][j].y, vertices[i][j].z);
				vertex(vertices[i][upInd].x, vertices[i][upInd].y, vertices[i][upInd].z);
				vertex(vertices[rightInd][j].x, vertices[rightInd][j].y, vertices[rightInd][j].z);
				vertex(vertices[rightInd][upInd].x, vertices[rightInd][upInd].y, vertices[rightInd][upInd].z);
				endShape();		
			}
		}
		
	}
	
	// Update buffer array
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
	}
	
	// Randomly populate worldState array
	public void fillRandom() {
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				double random = r.nextFloat();
				if (random < initPred) predState[i][j] = ALIVE;
				else if (random < initPred + initPrey) preyState[i][j] = ALIVE;
			}
		}
	}
	
	// Create / store vertex data for torus
	public void createVertices() {
		float tempRad, angle, innerAngle, tempY, tempX, tempZ;
		for (int j = 0; j < boardHeight; j++) {
			angle = radians(((float) j / (float) boardHeight) * 360.0f); 
			tempRad = minorRad * cos(angle) + majorRad;
			tempY = minorRad * sin(angle);
			for (int i = 0; i < boardLength; i++) {
				innerAngle = radians(((float) i / (float) boardLength) * 360.0f);
				tempX = tempRad * cos(innerAngle);
				tempZ = tempRad * sin(innerAngle);
				vertices[i][j] = new PVector(tempX, tempY, tempZ);
			}				
		}
	}
		
}
