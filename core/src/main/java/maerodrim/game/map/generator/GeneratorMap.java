package maerodrim.game.map.generator;

import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorMap {

    private int WIDTH, HEIGHT;
    private int SEED;
    private int SHIFT;
    private float islandStrange;

    private Grid mapHeights;

    public GeneratorMap(int width, int height, int seed) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        islandStrange = 1.0f;

        mapHeights = new Grid(WIDTH, HEIGHT);
    }

    private void noiseStage(final Grid grid,
                            final NoiseGenerator noiseGenerator,
                            final int radius,
                            final float modifier) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(SEED + SHIFT++);
        noiseGenerator.generate(grid);
    }

    public void generate() {
        final NoiseGenerator noiseGenerator = new NoiseGenerator();
        noiseStage(mapHeights, noiseGenerator, 100, 1.5f);
        noiseStage(mapHeights, noiseGenerator, 20, 0.17f);
        noiseStage(mapHeights, noiseGenerator, 10, 0.1f);
        applyMultiIslandShape(mapHeights, 10, SEED);
        noiseStage(mapHeights, noiseGenerator, 8, 0.05f);
        applyIslandShape(mapHeights);
        normalizeGrid(mapHeights);
    }

    private void normalizeGrid(Grid grid) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        // Найдем минимальное и максимальное значение
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float value = grid.get(x, y);
                if (value < min) min = value;
                if (value > max) max = value;
            }
        }

        // Если min и max равны, все значения одинаковые, нормализация не нужна
        if (min == max) return;

        // Нормализуем значения
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float value = grid.get(x, y);
                grid.set(x, y, (value - min) / (max - min)); // Приводим к [0,1]
            }
        }
    }

    private void applyIslandShape(Grid grid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float distX = (x - centerX) / (width / 2f);
                float distY = (y - centerY) / (height / 2f);
                float distance = (float) Math.sqrt(distX * distX + distY * distY);

                float islandFactor = (float) Math.max(0f, 1f - distance * islandStrange);

                grid.set(x, y, grid.get(x, y) * islandFactor);
            }
        }
    }

    private void applyMultiIslandShape(Grid grid, int islandCount, long seed) {
        int width = grid.getWidth();
        int height = grid.getHeight();

        Random random = new Random(seed + SHIFT++);
        List<float[]> centers = new ArrayList<>();

        for (int i = 0; i < islandCount; i++) {
            float centerX = random.nextFloat() * width;
            float centerY = random.nextFloat() * height;
            centers.add(new float[]{centerX, centerY});
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float minDistance = Float.MAX_VALUE;

                for (float[] center : centers) {
                    float distX = (x - center[0]) / (width / 2f);
                    float distY = (y - center[1]) / (height / 2f);
                    float distance = (float) Math.sqrt(distX * distX + distY * distY);
                    minDistance = Math.min(minDistance, distance);
                }

                float islandFactor = Math.max(0f, 1f - minDistance * islandStrange);
                grid.set(x, y, (float) (grid.get(x, y) * islandFactor));
            }
        }
    }

    public Grid getMapHeights() {
        return mapHeights;
    }

    public float getIslandStrange() {
        return islandStrange;
    }

    public void setIslandStrange(float islandStrange) {
        this.islandStrange = islandStrange;
    }
}
