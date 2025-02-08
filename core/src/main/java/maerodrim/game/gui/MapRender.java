package maerodrim.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.github.czyzby.noise4j.map.Grid;
import maerodrim.game.map.Map;

import java.util.HashMap;

public class MapRender {

    private Map map;
    private int width;
    private int height;
    private HashMap<Integer, Color> provinceColors;

    public MapRender(Map map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
        this.provinceColors = new HashMap<>();
    }

    public Texture render() {
        Grid grid = map.getMapHeights();
        int[][] provinceMap = map.getMapProvince();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float height = grid.get(x, y);
                pixmap.drawPixel(x, y, Color.rgba8888(getColorForHeight(height)));

                if (isBorder(x, y, provinceMap)) {
                    pixmap.drawPixel(x, y, Color.rgba8888(Color.WHITE));
                }

                // Закрашиваем провинции уникальными цветами с прозрачностью 0.25
                int provinceId = provinceMap[x][y];
                Color provinceColor = getProvinceColor(provinceId);
                pixmap.setColor(provinceColor);
                pixmap.fillRectangle(x, y, 1, 1);
            }
        }

        return new Texture(pixmap);
    }

    private Color getColorForHeight(final float height) {
        if (height < 0.50f) {
            // Вода: темно-синий → светло-синий
            return interpolateColor(height, 0.0f, 0.50f, new Color(10 / 255f, 30 / 255f, 80 / 255f, 1f), new Color(70 / 255f, 130 / 255f, 180 / 255f, 1f));
        } else if (height < 0.8f) {
            // Земля: зелёный → коричневый
            return interpolateColor(height, 0.50f, 0.8f, new Color(34 / 255f, 139 / 255f, 34 / 255f, 1f), new Color(19 / 255f, 112 / 255f, 20 / 255f, 1f));
        } else if (height < 0.95f) {
            // Горы: коричневый → серый
            return interpolateColor(height, 0.8f, 0.95f, new Color(139 / 255f, 69 / 255f, 19 / 255f, 1f), new Color(169 / 255f, 169 / 255f, 169 / 255f, 1f));
        } else {
            // Снег: серый → белый
            return interpolateColor(height, 0.95f, 1.0f, new Color(169 / 255f, 169 / 255f, 169 / 255f, 1f), new Color(255 / 255f, 255 / 255f, 255 / 255f, 1f));
        }
    }

    private Color interpolateColor(float height, float minHeight, float maxHeight, Color start, Color end) {
        float ratio = (height - minHeight) / (maxHeight - minHeight);
        float r = start.r + (end.r - start.r) * ratio;
        float g = start.g + (end.g - start.g) * ratio;
        float b = start.b + (end.b - start.b) * ratio;
        return new Color(r, g, b, 1f);
    }


    private boolean isBorder(int x, int y, int[][] provinceMap) {
        int currentProvince = provinceMap[x][y];

        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return false;
        }

        if (provinceMap[x - 1][y] != currentProvince) return true;
        if (provinceMap[x][y - 1] != currentProvince) return true;
        if (x < width - 1 && provinceMap[x + 1][y] != currentProvince) return true;
        if (x < width - 1 && provinceMap[x + 1][y + 1] != currentProvince) return true;

        return false;
    }

    private Color getProvinceColor(int provinceId) {
        if (!provinceColors.containsKey(provinceId)) {
            // Генерируем уникальный цвет для каждой провинции
            Color color = generateRandomColor();
            provinceColors.put(provinceId, color);
        }
        return provinceColors.get(provinceId); // Устанавливаем прозрачность 0.25
    }

    private Color generateRandomColor() {
        float r = (float) Math.random();
        float g = (float) Math.random();
        float b = (float) Math.random();
        return new Color(r, g, b, 0.15f); // Сгенерированный цвет с полной непрозрачностью
    }
}
