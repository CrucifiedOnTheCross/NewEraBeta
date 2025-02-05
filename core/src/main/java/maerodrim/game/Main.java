package maerodrim.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import maerodrim.game.gui.MapRender;
import maerodrim.game.map.Map;
import maerodrim.game.map.generator.GeneratorMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private SpriteBatch batch;
    private Texture texture;
    private Map map;

    @Override
    public void create() {
        GeneratorMap generatorMap = new GeneratorMap(WIDTH, HEIGHT, Generators.rollSeed());
        generatorMap.generate();
        Map map = new Map(generatorMap.getMapHeights());
        MapRender mapRenderer = new MapRender(map, WIDTH, HEIGHT);

        texture = mapRenderer.render();
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0f, 0f);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
