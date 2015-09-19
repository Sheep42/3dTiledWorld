package com.dshedd.tiledworld;


import com.badlogic.gdx.math.Rectangle;

public class Exit {
	public float centrePosX;
	public float centrePosY;
	public float width;
	public float height;
	public Rectangle bounds;
	
	public Exit(float centrePosX, float centrePosY, float width, float height) {
		this.centrePosX = centrePosX;
		this.centrePosY = centrePosY;
		this.width = width;
		this.height = height;
		bounds = new Rectangle(centrePosX - width/2, centrePosY - height/2, width, height);
	}
}
