package com.dshedd.tiledworld;


import com.badlogic.gdx.math.Vector2;

public class Player {
	
	private World world;
	private float width = 0.5f;
	private float depth = 0.3f;
	private float rotation = 0.0f;
	private float rotationModifier;

	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	private Vector2 centerPos = new Vector2(0.0f, 0.0f);
	private Vector2 hitboxFrontRight = new Vector2(0.0f, 0.0f);
	private Vector2 hitboxBackRight = new Vector2(0.0f, 0.0f);
	private Vector2 hitboxBackLeft = new Vector2(0.0f, 0.0f);
	private Vector2 hitboxFrontLeft = new Vector2(0.0f, 0.0f);
	
	public Player (World world) {
		this.world = world;
	}

	public void moveForward(float delta) {
		velocity.set(-(float)(Math.sin(Math.toRadians(getRotation()))), (float)(Math.cos(Math.toRadians(getRotation()))));
		rotationModifier = 0;
		tryMove(delta);
	}
	
	public void moveBackward(float delta) {
		velocity.set((float)(Math.sin(Math.toRadians(getRotation()))), -(float)(Math.cos(Math.toRadians(getRotation()))));
		rotationModifier = 0;
		tryMove(delta);
	}

	public void strafeLeft(float delta) {
		velocity.set(-(float)(Math.cos(Math.toRadians(getRotation()))), -(float)(Math.sin(Math.toRadians(getRotation()))));
		rotationModifier = 0;
		tryMove(delta);
	}
	
	public void strafeRight(float delta) {
		velocity.set((float)(Math.cos(Math.toRadians(getRotation()))), (float)(Math.sin(Math.toRadians(getRotation()))));
		rotationModifier = 0;
		tryMove(delta);
	}

	public void turnLeft(float delta) {
		velocity.set(0.0f, 0.0f);
		rotationModifier = 90;
		tryMove(delta);
	}
	
	public void turnRight(float delta) {
		velocity.set(0.0f, 0.0f);
		rotationModifier = -90;
		tryMove(delta);
	}
	
	public void stopMoving() {
		velocity.set(0.0f, 0.0f);
		rotationModifier = 0;
	}
	
	private void tryMove(float delta) {
		// create temporary backups of centerPos, velocity, and rotation...
		Vector2 centerPosBackup = new Vector2(centerPos);
		Vector2 velocityBackup = new Vector2(velocity);
		float rotationBackup = getRotation();
		int xVel = (int)velocity.x, 
			yVel = (int)velocity.y;
		
		//apply movement
		if(xVel*delta > 0)
			centerPos.add(world.getWalls().get(0).width, 0);
		else if(xVel*delta < 0)
			centerPos.add(-world.getWalls().get(0).width, 0);
		else if(yVel*delta > 0)
			centerPos.add(0, world.getWalls().get(0).width);
		else if(yVel*delta < 0)
			centerPos.add(0, -world.getWalls().get(0).width);
		
		//centerPos.add(velocity.x * delta, velocity.y * delta); //Fluid motion
		
		setRotation(getRotation() + rotationModifier);
		updateBounds();
		
		// if blocking collision at new position...
		if (world.collision()) {
			// ...undo move
			centerPos = centerPosBackup;
			velocity = velocityBackup;
			setRotation(rotationBackup);
			updateBounds();
		}
	}

	private void updateBounds() {
		float cosTheta = (float)(Math.cos(Math.toRadians(getRotation())));
		float sinTheta = (float)(Math.sin(Math.toRadians(getRotation())));
		hitboxFrontRight.x = (float) (centerPos.x + (width*0.3 * cosTheta) - (depth*0.3 * sinTheta));
		hitboxFrontRight.y = (float) (centerPos.y + (width*0.3 * sinTheta) + (depth*0.3 * cosTheta));
		hitboxBackRight.x = (float) (centerPos.x + (width*0.3 * cosTheta) - (-depth*0.3 * sinTheta));
		hitboxBackRight.y = (float) (centerPos.y + (width*0.3 * sinTheta) + (-depth*0.3 * cosTheta));
		hitboxBackLeft.x = (float) (centerPos.x + (-width*0.3 * cosTheta) - (-depth*0.3 * sinTheta));
		hitboxBackLeft.y = (float) (centerPos.y + (-width*0.3 * sinTheta) + (-depth*0.3 * cosTheta));
		hitboxFrontLeft.x = (float) (centerPos.x + (-width*0.3 * cosTheta) - (depth*0.3 * sinTheta));
		hitboxFrontLeft.y = (float) (centerPos.y + (-width*0.3 * sinTheta) + (depth*0.3 * cosTheta));		
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Vector2 getCenterPos() {
		return centerPos;
	}

	public Vector2 getHitboxFrontRight() {
		return hitboxFrontRight;
	}

	public Vector2 getHitboxBackRight() {
		return hitboxBackRight;
	}

	public Vector2 getHitboxBackLeft() {
		return hitboxBackLeft;
	}

	public Vector2 getHitboxFrontLeft() {
		return hitboxFrontLeft;
	}

	public float getWidth() {
		return width;
	}

	public float getDepth() {
		return depth;
	}

	public float getRotation() {
		return rotation;
	}

	public float getRotationModifier() {
		return rotationModifier;
	}
}
