package com.dshedd.tiledworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dshedd.tiledworld.screens.RunnerScreen;



public class TiledWorldBuilder extends Game {

	public static final boolean DEBUG = false;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	public RunnerScreen gameScreen;
	private Viewport viewport;
	
	@Override
	public void create() {
		gameScreen = new RunnerScreen(this);
		setScreen(gameScreen);
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
