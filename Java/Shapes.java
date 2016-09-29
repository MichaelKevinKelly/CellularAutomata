/*
 * Contains commonly used / useful patterns for Conway's Game of Life
 * 
 */

public final class Shapes {
	
	private Shapes() {}
	
	public static int[][] getGliderGun() {
		int[][] gunCoord = new int[36][9];
		gunCoord[0][4] = 1;
		gunCoord[0][5] = 1;
		gunCoord[1][4] = 1;
		gunCoord[1][5] = 1;
		gunCoord[10][4] = 1;
		gunCoord[10][5] = 1;
		gunCoord[10][6] = 1;
		gunCoord[11][3] = 1;
		gunCoord[11][7] = 1;
		gunCoord[12][2] = 1;
		gunCoord[12][8] = 1;
		gunCoord[13][2] = 1;
		gunCoord[13][8] = 1;
		gunCoord[14][5] = 1;
		gunCoord[15][3] = 1;
		gunCoord[15][7] = 1;
		gunCoord[16][4] = 1;
		gunCoord[16][5] = 1;
		gunCoord[16][6] = 1;
		gunCoord[17][5] = 1;
		gunCoord[20][2] = 1;
		gunCoord[20][3] = 1;
		gunCoord[20][4] = 1;
		gunCoord[21][2] = 1;
		gunCoord[21][3] = 1;
		gunCoord[21][4] = 1;
		gunCoord[22][1] = 1;
		gunCoord[22][5] = 1;
		gunCoord[24][0] = 1;
		gunCoord[24][1] = 1;
		gunCoord[24][5] = 1;
		gunCoord[24][6] = 1;
		gunCoord[24][6] = 1;
		gunCoord[34][2] = 1;
		gunCoord[34][3] = 1;
		gunCoord[35][2] = 1;
		gunCoord[35][3] = 1;
		return gunCoord;
	}
	
	public static int[][] getEater() {
		int[][] eater = new int[4][4];
		eater[0][0] = 1;
		eater[0][1] = 1;
		eater[1][0] = 1;
		eater[1][2] = 1;
		eater[2][2] = 1;
		eater[3][2] = 1;
		eater[3][3] = 1;
		return eater;	
	}
	
	public static int[][] getDetector() {
		int[][] detector = new int[9][6];
		detector[0][1] = 1;
		detector[0][2] = 1;
		detector[0][4] = 1;
		detector[0][5] = 1;
		detector[1][2] = 1;
		detector[1][4] = 1;
		detector[1][5] = 1;
		detector[2][2] = 1;
		detector[3][0] = 1;
		detector[3][2] = 1;
		detector[4][0] = 1;
		detector[4][1] = 1;
		detector[7][0] = 1;
		detector[7][1] = 1;
		detector[8][0] = 1;
		detector[8][1] = 1;
		return detector;
	}
	
}
