package maerodrim.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
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
    private float islandStrength = 0.6f; // Global variable

    private int seed = 1000; // Начальный сид
    private final int seedMin = 1000;
    private final int seedMax = 5000;
    private final int seedStep = 100;

    private long lastUpdateTime;
    private final long updateInterval = 500; // 2 секунды

    @Override
    public void create() {
        batch = new SpriteBatch();
        generateMap();
        lastUpdateTime = TimeUtils.millis();
    }

    @Override
    public void render() {
        if (TimeUtils.millis() - lastUpdateTime > updateInterval) {
            lastUpdateTime = TimeUtils.millis();
            updateMap();
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, 0, WIDTH, HEIGHT);
        batch.end();
    }

    private void generateMap() {
        generator = new GeneratorMap(2560, 1440, seed);
        generator.setIslandStrange(islandStrength);
        generator.generate();
        map = new Map(generator.getMapHeights());

        if (texture != null) texture.dispose();
        texture = new MapRender(map, 2560, 1440).render();
    }

    private void updateMap() {
        seed += seedStep;
        if (seed > seedMax) seed = seedMin; // Зацикливание

        Gdx.app.postRunnable(this::generateMap); // Безопасное обновление в главном потоке
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
