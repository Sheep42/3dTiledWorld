package com.dshedd.tiledworld;


import com.badlogic.gdx.math.Rectangle;

public class Door {
	public float centrePosX;
	public float centrePosY;
	public float width;
	public float height;
	public Rectangle bounds;
	public DoorState state;
	public enum DoorState {LOCKED, CLOSED, OPEN};
	
	public Door(float centrePosX, float centrePosY, float width, float height, DoorState state) {
		this.centrePosX = centrePosX;
		this.centrePosY = centrePosY;
		this.width = width;
		this.height = height;
		this.state = state;
		bounds = new Rectangle(centrePosX - width/2, centrePosY - height/2, width, height);
	}
	
	public void open() {
		state = DoorState.OPEN;
		// TODO: auto close door after x seconds
	}
	
	public void unlock() {
		state = DoorState.CLOSED;
	}

	public void close() {
		state = DoorState.CLOSED;
	}
}
