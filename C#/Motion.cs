/*
 * Attach this script to the main camera to cause it to continuously 
 * move in the direction of the user's gaze
 * 
 */

using UnityEngine;

public class Motion : MonoBehaviour {

	public float speed = 10.0f;
	private bool moving = true;

	void Update () {
		if (GvrViewer.Instance.Triggered) moving = !moving;
		if (moving) transform.position += this.transform.forward * speed * Time.deltaTime;
	}
}