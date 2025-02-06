package maerodrim.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.github.czyzby.noise4j.map.Grid;
import maerodrim.game.map.Map;

public class MapRender {

    private Map map;
    private int width;
    private int height;

    public MapRender(Map map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public Texture render() {
        Grid grid = map.getMapHeights();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float height = grid.get(x, y);
                pixmap.drawPixel(x, y, Color.rgba8888(getColorForHeight(height)));
            }
        }

        return new Texture(pixmap);
    }

    private Color getColorForHeight(final float height) {
        if (height < 0.35) return new Color(10 / 255f, 50 / 255f, 150 / 255f, 1f); // Глубокая вода
        if (height < 0.45) return new Color(20 / 255f, 80 / 255f, 190 / 255f, 1f); // Глубокая вода
        if (height < 0.48) return new Color(100 / 255f, 140 / 255f, 230 / 255f, 1f); // Мелководье
        if (height < 0.50) return new Color(194 / 255f, 178 / 255f, 128 / 255f, 1f); // Песок (пляж)
        if (height < 0.60) return new Color(35 / 255f, 225 / 255f, 70 / 255f, 1f); // Трава
        if (height < 0.83) return new Color(25 / 255f, 200 / 255f, 60 / 255f, 1f); // Луга
        if (height < 0.87) return new Color(120 / 255f, 70 / 255f, 40 / 255f, 1f); // Земля (горы у подножия)
        if (height < 0.91) return new Color(120 / 255f, 120 / 255f, 120 / 255f, 1f); // Каменистая местность
        if (height < 0.97) return new Color(200 / 255f, 200 / 255f, 200 / 255f, 1f); // Высокогорье
        return new Color(255 / 255f, 255 / 255f, 255 / 255f, 1f); // Снег
    }

}
