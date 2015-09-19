package com.dshedd.tiledworld.screens;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dshedd.tiledworld.TiledWorldBuilder;
import com.dshedd.tiledworld.World;
import com.dshedd.tiledworld.WorldRenderer;

public class RunnerScreen implements Screen {

	private TiledWorldBuilder game;
	private World world;
	private WorldRenderer worldRenderer;
	
	public RunnerScreen(TiledWorldBuilder game) {
		this.game = game;
		world = new World(game);
		worldRenderer = new WorldRenderer(world);
		game.setViewport(new FitViewport(game.WIDTH, game.HEIGHT, worldRenderer.getCamera()));
	}

	@Override
	public void render(float delta) {
		world.update(delta);
		worldRenderer.render();
	}
	
	@Override public void resize(int width, int height) { game.getViewport().update(width, height); }
	@Override public void show() {}
	@Override public void hide() {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
