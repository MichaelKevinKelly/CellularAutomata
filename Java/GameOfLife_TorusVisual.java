/**
 * 
 * Conway's Game of Life, visualized on a torus
 * 
 **/

import processing.core.PApplet;
import processing.core.PVector;
import java.util.Random;

public class GameOfLife_TorusVisual extends PApplet {
	
	// WorldState holds current state, buff is temporary storage for subsequent state
	private int[][] worldState, buff;
	private final int ALIVE = 1, DEAD = 0; 
	private Random r;
	
	// Torus parameters / data
	private PVector vertices[][];
	private float majorRad = 150;
	private float minorRad = 80;
	private float rotX, rotY, rotZ;
	
	// Game parameters
	private int boardLength = 220;
	private int boardHeight = 150;
	private double initProb = 0.15;
	private boolean gridLines = false;
	private String startCondition = "rand";
	private int l = (int) (boardLength * 3), h = (int) (boardHeight * 3.5);
	
	// Control variable
	private boolean pause = false;
	private boolean flip = false;
	private int rotate = 0, lastI = 0, lastJ = 0;
	private char lastShape;
	private float currFrameCount;
	
	public static void main(String[] args) {
		PApplet.main("GameOfLife_TorusVisual");
	}
	
	public void settings() {
		size(l, h, "processing.opengl.PGraphics3D");
		noSmooth();
	}
	
	public void setup() {
		
		// Initialize arrays and Random object, randomly populate arrays
		worldState = new int[boardLength][boardHeight];
		buff = new int[boardLength][boardHeight];
		vertices = new PVector[boardLength][boardHeight];
		r = new Random();
		
		switch (startCondition) {
			case "rand": fillRandom(); break;
			default: break;
		}	
		createVertices();
		
		frameRate(15);
		if (!gridLines) noStroke();
		rotX = PI/350;
		rotY = PI/400;
		rotZ = PI/200;
		
	}
	
	public void draw() {
	
		lights();
		background(245, 245, 255);
		
		translate(width/2, height/2, -100);
		float frames = frameCount;
		if (pause) frames = currFrameCount;
		rotateX(frames*rotX);
		rotateY(frames*rotY);
		rotateZ(frames*rotZ);
		
		// Fill buffer array according to current state / rule set
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (!pause) fillBuff(i,j);
			}
		}
		
		// Update worldState array and draw to GUI
		int upInd, rightInd;
		for (int i = 0; i < boardLength ; i++) {
			for (int j = 0; j < boardHeight; j++) {
				fill(0,0,0);
				if (!pause) worldState[i][j] = buff[i][j];
				if (worldState[i][j] == ALIVE) fill(255,255,255);
				
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
	 *  User controls: pause ('enter'/'return'), clear grid ('c'), randomly populate grid ('r'),
	 *  place glider gun, eater, or "detector" ('g','e','d'), rotate pattern 90 degrees ('t'), 
	 *  flip pattern ('f')
	 *  
	 */
	public void keyPressed(){
		if (key == RETURN || key == ENTER) {
			pause = !pause;
			if (pause) {
				currFrameCount = frameCount;
				noLoop();
			}
			else loop();
		}
		else if (key == 'c' || key == 'C') {
			if (pause) {
				worldState = new int[boardLength][boardHeight];
				redraw();
			}
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
				loadShape(0, 0, key, rotate, flip, false);
				redraw();
			}
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
	}
	
	// Randomly populate worldState array
	public void fillRandom() {
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardHeight; j++) {
				if (r.nextFloat() < initProb) worldState[i][j] = ALIVE;				
			}
		}
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
