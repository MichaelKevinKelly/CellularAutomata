/**
 * 
 * Simple implementation of the Game of Life in 3D using Unity.
 * To use this script, attach to an empty GameObject in a Unity scene. Set prefab to square 
 * GameObject in the Inspector panel. The square object should be scaled approximately 
 * by the cell size dimension.
 * 
 **/

using UnityEngine;
using System.Collections;

public class LifeScript : MonoBehaviour {

	public GameObject prefab;
	public int boardDim = 25;
	public float cellDim = 3.1f;
	public float initProb = 0.2f;

	private int[,,] stateArr, buff;
	private Object[,,] cells;
	private Vector3 displacement;
	readonly int ALIVE = 1;
	readonly int DEAD = 0;

	void Start() {
		stateArr = new int[boardDim, boardDim, boardDim];
		buff = new int[boardDim, boardDim, boardDim];
		cells = new GameObject[boardDim, boardDim, boardDim];
		RandomFill();
	}

	void Update() {
		UpdateBuff ();
		UpdateState ();
	}

	// Initializes three-dimensional array of cells, using square GameObject to represent individual cells.
	void RandomFill() {
		for (var i = 0; i < boardDim; i++) {
			for (var j = 0; j < boardDim; j++) {
				for (var k = 0; k < boardDim; k++) {
					displacement = new Vector3 (i * cellDim, j * cellDim, k * cellDim);
					Object curr = Instantiate (prefab, displacement, Quaternion.identity);
					cells [i, j, k] = curr;
					if (Random.Range (0.0f, 1.0f) < initProb) {
						buff [i, j, k] = ALIVE;
						stateArr [i, j, k] = ALIVE;
					} else {
						((GameObject)curr).GetComponent<MeshRenderer> ().enabled = false;
					}
				}
			}
		}
	}

	// Updates state array based on buffer array. Live cells are rendered, dead cells are not.
	void UpdateState() {
		for (var i = 0; i < boardDim; i++) {
			for (var j = 0; j < boardDim; j++) {
				for (var k = 0; k < boardDim; k++) {
					((GameObject)cells[i,j,k]).GetComponent<MeshRenderer> ().enabled = false;
					stateArr [i, j, k] = buff [i, j, k];
					if (stateArr [i, j, k] == ALIVE) {
						((GameObject)cells[i,j,k]).GetComponent<MeshRenderer> ().enabled = true;
					}
				}
			}
		}
	}

	// Updates buffer array according to "4555" ruleset for 3D Life
	// Game is played on the surface of a 3-torus
	void UpdateBuff() {
		int neighborCount;
		int iUp, iDown, jUp, jDown, kUp, kDown;

		for (var i = 0; i < boardDim; i++) {
			for (var j = 0; j < boardDim; j++) {
				for (var k = 0; k < boardDim; k++) {
					neighborCount = 0;
					iUp = (i + 1) % boardDim;
					jUp = (j + 1) % boardDim;
					kUp = (k + 1) % boardDim;
					iDown = ((i - 1) % boardDim + boardDim) % boardDim;
					jDown = ((j - 1) % boardDim + boardDim) % boardDim;
					kDown = ((k - 1) % boardDim + boardDim) % boardDim;

					neighborCount = stateArr [iDown, jDown, kDown] + stateArr [iDown, jDown, k] + stateArr [iDown, jDown, kUp]
						+ stateArr [iDown, j, kDown] + stateArr [iDown, j, k] + stateArr [iDown, j, kUp]
						+ stateArr [iDown, jUp, kDown] + stateArr [iDown, jUp, k] + stateArr [iDown, jUp, kUp]
						+ stateArr [i, jDown, kDown] + stateArr [i, jDown, k] + stateArr [i, jDown, kUp]
						+ stateArr [i, j, kDown] + stateArr [i, j, kUp]
						+ stateArr [i, jUp, kDown] + stateArr [i, jUp, k] + stateArr [i, jUp, kUp]
						+ stateArr [iUp, jDown, kDown] + stateArr [iUp, jDown, k] + stateArr [iUp, jDown, kUp]
						+ stateArr [iUp, j, kDown] + stateArr [iUp, j, k] + stateArr [iUp, j, kUp]
						+ stateArr [iUp, jUp, kDown] + stateArr [iUp, jUp, k] + stateArr [iUp, jUp, kUp];

					if (neighborCount < 4) {
						buff [i, j, k] = DEAD;
					} else if (neighborCount == 5) {
						buff [i, j, k] = ALIVE;
					} else if (neighborCount > 5) {
						buff [i, j, k] = DEAD;
					}
				}
			}
		}
	}
}