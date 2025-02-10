package maerodrim.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import maerodrim.game.gui.MapRender;
import maerodrim.game.gui.camera.GameCamera;
import maerodrim.game.map.Map;
import maerodrim.game.map.generator.GeneratorMap;
import maerodrim.game.map.generator.GeneratorProvince;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends ApplicationAdapter {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private SpriteBatch batch;
    private Texture texture;
    private MapRender mapRender;
    private GameCamera gameCamera;

    @Override
    public void create() {
        log.info("Run application");

        batch = new SpriteBatch();
        mapRender = new MapRender(generateMapInstance(), 1920, 1080, true, false);
        texture = generateMap();
        log.info("Render texture for map");

        gameCamera = new GameCamera(1920, 1080, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(gameCamera.getCamera().combined);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();
    }

    private Texture generateMap() {
        return mapRender.render();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }

    private Map generateMapInstance() {
        GeneratorMap generatorMap = new GeneratorMap(1920, 1080, 2000);
        generatorMap.generate();
        log.info("Generated height map");

        GeneratorProvince generatorProvince = new GeneratorProvince(generatorMap.getMapHeights(), 2000);
        generatorProvince.generateProvinces(150, 500);
        log.info("Generated province map");

        return new Map(generatorMap.getMapHeights(), generatorProvince.getProvinceMap());
    }
}
