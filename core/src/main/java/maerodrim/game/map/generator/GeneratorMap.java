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
        islandStrange = 0.45f;

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
        noiseStage(mapHeights, noiseGenerator, 100, 0.4f);
        applyCapHeights(0.3f);
        noiseStage(mapHeights, noiseGenerator, 16, 0.1f);
        noiseStage(mapHeights, noiseGenerator, 6, 0.05f);
        applyMultiIslandShape(mapHeights, 30, SEED);
        applyIslandShape(mapHeights);
        applyNormalize(mapHeights);
        applyTectonicRidges(mapHeights, 10, SEED + SHIFT++);
        applyMountainMask(mapHeights, 0.5f, 0.5f);
        applyWindErosion(mapHeights, 3, 0.015f);
        applyHydraulicErosion(75, 0.2f, 0.4f, 0.05f);
    }

    public void applyCapHeights(float maxHeight) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (mapHeights.get(x, y) > maxHeight) {
                    mapHeights.set(x, y, maxHeight);
                }
            }
        }
    }

    private void applyWindErosion(Grid grid, int iterations, float windFactor) {
        for (int iter = 0; iter < iterations; iter++) {
            Grid newGrid = new Grid(WIDTH, HEIGHT);

            for (int x = 1; x < WIDTH - 1; x++) {
                for (int y = 1; y < HEIGHT - 1; y++) {
                    float height = grid.get(x, y);
                    float erosion = height * windFactor;

                    newGrid.set(x, y, height - erosion);
                    newGrid.set(x + 1, y, grid.get(x + 1, y) + erosion * 0.5f);
                    newGrid.set(x, y + 1, grid.get(x, y + 1) + erosion * 0.5f);
                }
            }

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    grid.set(x, y, newGrid.get(x, y));
                }
            }
        }
    }

    private void applyMountainMask(Grid grid, float threshold, float amplification) {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float height = grid.get(x, y);
                if (height > threshold) {
                    grid.set(x, y, height + (height - threshold) * amplification);
                }
            }
        }
    }

    private void applyTectonicRidges(Grid grid, int ridgeCount, long seed) {
        Random random = new Random(seed);
        List<int[]> ridges = new ArrayList<>();

        for (int i = 0; i < ridgeCount; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            ridges.add(new int[]{x, y});
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                float minDist = Float.MAX_VALUE;
                for (int[] ridge : ridges) {
                    float dist = (float) Math.sqrt(Math.pow(x - ridge[0], 2) + Math.pow(y - ridge[1], 2));
                    minDist = Math.min(minDist, dist);
                }
                float ridgeFactor = Math.max(0, 1 - minDist / 75);
                grid.set(x, y, grid.get(x, y) + ridgeFactor * 0.3f);
            }
        }
    }

    public void applyHydraulicErosion(int iterations, float rainAmount, float sedimentCapacity, float evaporationRate) {
        float[][] water = new float[WIDTH][HEIGHT];
        float[][] sediment = new float[WIDTH][HEIGHT];

        for (int iter = 0; iter < iterations; iter++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    water[x][y] += rainAmount;
                }
            }

            float[][] newHeights = new float[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    float currentHeight = mapHeights.get(x, y);
                    float currentWater = water[x][y];

                    if (currentWater <= 0) {
                        newHeights[x][y] = currentHeight;
                        continue;
                    }

                    int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    float minHeight = currentHeight + currentWater;
                    int targetX = x, targetY = y;

                    for (int[] n : neighbors) {
                        int nx = x + n[0], ny = y + n[1];
                        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
                            float neighborHeight = mapHeights.get(nx, ny) + water[nx][ny];
                            if (neighborHeight < minHeight) {
                                minHeight = neighborHeight;
                                targetX = nx;
                                targetY = ny;
                            }
                        }
                    }

                    if (targetX != x || targetY != y) {
                        float deltaHeight = (currentHeight + currentWater) - minHeight;
                        float waterFlow = Math.min(deltaHeight * 0.5f, currentWater);
                        float sedimentTransport = Math.min(sediment[x][y], waterFlow * sedimentCapacity);

                        // Перемещение воды и осадка
                        water[x][y] -= waterFlow;
                        water[targetX][targetY] += waterFlow;
                        sediment[x][y] -= sedimentTransport;
                        sediment[targetX][targetY] += sedimentTransport;

                        // Размывание
                        float erosionAmount = sedimentCapacity * waterFlow;
                        newHeights[x][y] = Math.max(0, currentHeight - erosionAmount);
                    } else {
                        newHeights[x][y] = currentHeight;
                    }
                }
            }

            // 3. Испарение воды
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    water[x][y] *= (1 - evaporationRate);
                }
            }

            // 4. Обновляем высоты
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    mapHeights.set(x, y, newHeights[x][y]);
                }
            }
        }
    }

    public void applyThermalErosion(int iterations, float threshold, float erosionFactor) {
        for (int iter = 0; iter < iterations; iter++) {
            Grid newGrid = new Grid(WIDTH, HEIGHT);

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    float currentHeight = mapHeights.get(x, y);
                    float maxDiff = 0;
                    int targetX = -1, targetY = -1;

                    int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
                    for (int[] n : neighbors) {
                        int nx = x + n[0], ny = y + n[1];

                        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
                            float neighborHeight = mapHeights.get(nx, ny);
                            float diff = currentHeight - neighborHeight;

                            if (diff > threshold && diff > maxDiff) {
                                maxDiff = diff;
                                targetX = nx;
                                targetY = ny;
                            }
                        }
                    }

                    if (targetX != -1 && targetY != -1) {
                        float amountToErode = maxDiff * erosionFactor;
                        newGrid.set(x, y, mapHeights.get(x, y) - amountToErode);
                        newGrid.set(targetX, targetY, mapHeights.get(targetX, targetY) + amountToErode);
                    } else {
                        newGrid.set(x, y, mapHeights.get(x, y));
                    }
                }
            }

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    mapHeights.set(x, y, newGrid.get(x, y));
                }
            }
        }
    }

    private void applyNormalize(Grid grid) {
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

                float islandFactor = Math.max(0f, 1f - distance * islandStrange);

                grid.set(x, y, (float) (grid.get(x, y) * islandFactor));
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
                grid.set(x, y, grid.get(x, y) * islandFactor);
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
