package maerodrim.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import maerodrim.game.gui.MapRender;
import maerodrim.game.map.Map;
import maerodrim.game.map.generator.GeneratorMap;
import maerodrim.game.map.generator.GeneratorProvince;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture texture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = generateMap();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();
    }

    private Texture generateMap() {
        GeneratorMap generator = new GeneratorMap(2560, 1440, 2000);
        generator.generate();
        GeneratorProvince generatorProvince = new GeneratorProvince(generator.getMapHeights());
        generatorProvince.generateProvinces(300, 1500);
        Map map = new Map(generator.getMapHeights(), generatorProvince.getProvinceMap());
        return new MapRender(map, 2560, 1440).render();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
