package maerodrim.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private GeneratorMap generator;
    private Map map;
    private float islandStrength = 0.7f; // the global variable

    @Override
    public void create() {
        batch = new SpriteBatch();

        generator = new GeneratorMap(2560, 1440, 1800); // Generate at larger resolution
        generator.setIslandStrange(islandStrength);
        generator.generate();
        map = new Map(generator.getMapHeights());

        texture = new MapRender(map, 2560, 1440).render(); // Create texture at 2560x1440 resolution
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        // Scale the texture to fit the screen resolution
        batch.draw(texture, 0, 0, WIDTH, HEIGHT);

        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
