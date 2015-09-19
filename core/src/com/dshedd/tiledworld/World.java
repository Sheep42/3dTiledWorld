package com.dshedd.tiledworld;


import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.dshedd.tiledworld.Door.DoorState;

public class World {
	
	private TiledWorldBuilder game;
	private Player player;
	private Map map = new Map();
	private TiledMap tiledMap;
	private TiledMapTileLayer layer;
	private Cell cell;
	
	private Array<Wall> walls = new Array<Wall>();
	private Array<Exit> exits = new Array<Exit>();
	private Array<Floor> floors = new Array<Floor>();
	private Array<Gem> gems = new Array<Gem>();
	private Array<Door> doors = new Array<Door>();
	private Array<Key> keys = new Array<Key>();
	
	private int inventoryGems;
	private int inventoryKeys;
	private int currentLevel = 1;
	private int finalLevel = 3;

	public World(TiledWorldBuilder game) {
		this.game = game;
		player = new Player(this);
		
		tiledMap = new TmxMapLoader().load("maps/test.tmx");
		layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		
		generateLevel();
	}
	
	private void generateLevel() {
		walls.clear();
		floors.clear();
		exits.clear();
		gems.clear();
		doors.clear();
		keys.clear();
		
		//Level loading via tmx will go here
        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){
            	 cell = layer.getCell(x,y);
            	 
            	 if(cell.getTile().getProperties().containsKey("wall")){
            		 walls.add(new Wall(x, layer.getHeight() - y, 1f, 1f)); //Add a wall to the walls array
            	 }
            	 
            	 else if(cell.getTile().getProperties().containsKey("floor")){
            		 floors.add(new Floor(x, layer.getHeight() - y, 1f, 1f)); //Add a floor to the floors array
            	 }
            	 
            	 else if(cell.getTile().getProperties().containsKey("playerStart")){
            		 floors.add(new Floor(x, layer.getHeight() - y, 1f, 1f)); //Add a floor tile beneath the player
            		 player.getCenterPos().set(x, layer.getHeight() - y); //Set player starting pos
            	 }
            }
            
        }
        
        //Old level loading
		//inventoryGems = 0;
		//inventoryKeys = 0;
		//map.load("Level"+levelNumber+".map");
	}
	
	public void update(float delta) {
		if (Gdx.app.getType() == ApplicationType.Desktop)
			handleDesktopInput(delta);
		
		if(Gdx.app.getType() == ApplicationType.Android)
			handleAndroidInput(delta);
	}
	
	// Called by player.tryMove() - returns true if collision with blocking object, false if collision with non-blocking object
	public boolean collision() {
		for(int i=0; i < walls.size; i++ ) {
			if ((walls.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (walls.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (walls.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (walls.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) return true;
		}
		
		for(int i=0; i < exits.size; i++ ) {
			if ((exits.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (exits.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (exits.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (exits.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				if (currentLevel != finalLevel) nextLevel();
				else restartGame();
			}
		}
		
		for(int i=0; i < gems.size; i++ ) {
			if ((gems.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (gems.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (gems.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (gems.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				gems.removeIndex(i);
				addGem();
			}
		}
		
		for(int i=0; i < doors.size; i++ ) {
			if ((doors.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (doors.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (doors.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (doors.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				if (doors.get(i).state.equals(DoorState.CLOSED)) doors.get(i).open();
				else if (doors.get(i).state.equals(DoorState.LOCKED)) {
					if (inventoryKeys == 0) return true;
					else {
						doors.get(i).unlock();
						removeKey();
					}
				}
			}
		}
		
		for(int i=0; i < keys.size; i++ ) {
			if ((keys.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (keys.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (keys.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (keys.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				keys.removeIndex(i);
				addKey();
			}
		}
		
		return false;
	}

	private void handleDesktopInput(float delta) {
		// Desktop controls
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) player.moveForward(delta);
		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) player.moveBackward(delta);
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) player.turnLeft(delta);
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) player.turnRight(delta);
		if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) player.strafeLeft(delta);
		if(Gdx.input.isKeyJustPressed(Input.Keys.X)) player.strafeRight(delta);
		if(Gdx.input.isKeyPressed(Input.Keys.R)) restartLevel();
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) restartGame();
	}

	private void handleAndroidInput(float delta){
		// Android controls
		/*if (Gdx.input.getAccelerometerY() < -2) player.strafeLeft(delta);
		if (Gdx.input.getAccelerometerY() > 2) player.strafeRight(delta);
		if (Gdx.input.getAccelerometerX() < 7) player.moveForward(delta);
		if (Gdx.input.getAccelerometerX() > 9) player.moveBackward(delta);*/
	
		if (Gdx.input.justTouched()) {
			if (Gdx.input.getX() >= ((Gdx.graphics.getWidth() / 2) - 250) && Gdx.input.getX() <= ((Gdx.graphics.getWidth() / 2) + 250) && Gdx.input.getY() < Gdx.graphics.getHeight() / 2) player.moveForward(delta);
			if (Gdx.input.getX() >= ((Gdx.graphics.getWidth() / 2) - 250) && Gdx.input.getX() <= ((Gdx.graphics.getWidth() / 2) + 250) && Gdx.input.getY() > Gdx.graphics.getHeight() / 2) player.moveBackward(delta);

			if (Gdx.input.getX() < ((Gdx.graphics.getWidth() / 2) - 250)) player.turnLeft(delta);
			if (Gdx.input.getX() > ((Gdx.graphics.getWidth() / 2) + 250)) player.turnRight(delta);
		}
	}
	
	private void restartLevel() {
		player.setRotation(0.0f);
		generateLevel();
	}
	
	private void restartGame() {
		player.setRotation(0.0f);
		currentLevel = 1;
		generateLevel();
	}
	
	private void addGem() {
		inventoryGems++;
	}

	private void addKey() {
		inventoryKeys++;
	}

	private void removeKey() {
		inventoryKeys--;
	}
	
	private void nextLevel() {
		currentLevel++;
		player.setRotation(0.0f);
		generateLevel();
	}

	public TiledWorldBuilder getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public Map getMap() {
		return map;
	}

	public Array<Wall> getWalls() {
		return walls;
	}

	public Array<Floor> getFloors() {
		return floors;
	}

	public void setFloors(Array<Floor> floors) {
		this.floors = floors;
	}

	public Array<Exit> getExits() {
		return exits;
	}

	public Array<Gem> getGems() {
		return gems;
	}

	public Array<Door> getDoors() {
		return doors;
	}

	public Array<Key> getKeys() {
		return keys;
	}
	
	public int getInventoryGems() {
		return inventoryGems;
	}

	public int getInventoryKeys() {
		return inventoryKeys;
	}

	public int getLevel() {
		return currentLevel;
	}
}
