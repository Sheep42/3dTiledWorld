package com.dshedd.tiledworld;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {

	private World world;
	private PerspectiveCamera camera;
	private Model cube;
	private Model gem;
	private Model key;
	private ModelBatch modelBatch;
	private ModelInstance wallInstance, 
			floorInstance;
	private Environment environment;
	
	private Texture floorTexture;
	private Texture wallTexture;
	private Texture exitTexture;
	private Texture lockedDoorTexture;
	private Texture gemTexture;
	private Texture keyTexture;
	
	private Material materialLoader = new Material();
	private TextureAttribute textureAttribute;
	
	private int gemRotation = 0;
	private OrthographicCamera hudCam;
	private SpriteBatch hudBatch;
	private BitmapFont hudFont;
	private ModelLoader<?> objLoader = new ObjLoader();
	private Array<ModelInstance> walls = new Array<ModelInstance>();
	private Array<ModelInstance> floors = new Array<ModelInstance>();
	private int currentLevel;
	
	private Stage hudStage;
	private Image upButton, downButton, leftButton, rightButton;
	
	public WorldRenderer(World world) {
		this.world = world;
		
		currentLevel = world.getLevel();
		modelBatch = new ModelBatch();

		//Models
		cube = objLoader.loadModel(Gdx.files.internal("models/cube.obj"));
		gem = objLoader.loadModel(Gdx.files.internal("models/gem.obj"));
		key = objLoader.loadModel(Gdx.files.internal("models/dtKey.obj"));
		
		//Environment
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f,0.8f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		//Camera
		camera = new PerspectiveCamera(70, 6f, 4f);
		camera.near = 0.01f;
		camera.direction.set(0, 2, -1);
		
		//Textures
		floorTexture = new Texture(Gdx.files.internal("textures/grass.png"), true);
		floorTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		wallTexture = new Texture(Gdx.files.internal("textures/wall.png"), true);
		wallTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		exitTexture = new Texture(Gdx.files.internal("textures/exit.png"), true);
		exitTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		lockedDoorTexture = new Texture(Gdx.files.internal("textures/locked.png"), true);
		lockedDoorTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		gemTexture = new Texture(Gdx.files.internal("textures/gem.png"), true);
		gemTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		keyTexture = new Texture(Gdx.files.internal("textures/key.png"), true);
		keyTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);

		//HUD
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, TiledWorldBuilder.WIDTH, TiledWorldBuilder.HEIGHT);
		hudBatch = new SpriteBatch();
		hudFont = new BitmapFont();
		hudStage = new Stage();
		
		
		
		generateMap();
	}
	
	public void render() {
		renderPlayArea();
		renderHud();
	}
	
	private void renderPlayArea() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
		
		if(currentLevel != world.getLevel()){
			generateMap();
			currentLevel = world.getLevel();
		}
	
		//Update camera on world rendering
		camera.position.set(world.getPlayer().getCenterPos().x * 2f, (world.getPlayer().getCenterPos().y * 2f) -0, 0.75f);
		camera.rotate(world.getPlayer().getRotation(), 0, 0, 1);
		camera.update();
		camera.rotate(-world.getPlayer().getRotation(), 0, 0, 1);
		
		//Draw the models to the screen
		modelBatch.begin(camera);
			modelBatch.render(walls, environment);
			modelBatch.render(floors, environment);
		modelBatch.end();
	}

	public void generateMap(){
		for (int i = 0; i < world.getFloors().size; i++){
			//Create the model instance
			floorInstance = null;
			floorInstance = new ModelInstance(
				cube, 
				world.getFloors().get(i).centrePosX * 2f, 
				world.getFloors().get(i).centrePosY * 2f, 
				-2
			);
			
			//Load Texture
			materialLoader = floorInstance.materials.get(0);
			textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, floorTexture);
			materialLoader.set(textureAttribute);
			
			//Add the model instance to the array
			floors.add(floorInstance);
		}
		
		for(int i = 0; i < world.getWalls().size; i++){
			//Create Instance
			wallInstance = null;
			wallInstance = new ModelInstance(
				cube, 
				world.getWalls().get(i).centrePosX * 2f, 
				world.getWalls().get(i).centrePosY * 2f, 
				0
			);
			
			//Load Texture
			materialLoader = wallInstance.materials.get(0);
			textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, wallTexture);
			materialLoader.set(textureAttribute);
			
			//Add Instance to the array
			walls.add(wallInstance);
    	}
	}
	
	private void renderHud() {
		hudCam.position.set(TiledWorldBuilder.WIDTH/2, TiledWorldBuilder.HEIGHT/2, 0.0f);
	    hudCam.update();
	    
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}

	public void setCamera(PerspectiveCamera camera) {
		this.camera = camera;
	}
}
