package maerodrim.game.map.generator;

import com.github.czyzby.noise4j.map.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorProvince {

    private static final float WATER_LEVEL = 0.48f;
    private Grid mapHeights;
    private int[][] provinceMap;
    private boolean[] isWaterProvince;

    public GeneratorProvince(Grid heightMap) {
        this.mapHeights = heightMap;
        this.provinceMap = new int[mapHeights.getWidth()][mapHeights.getHeight()];
    }

    public void generateProvince(int landProvinceCount) {
        Random random = new Random();
        List<Point> waterSeeds = new ArrayList<>();
        List<Point> landSeeds = new ArrayList<>();

        // Генерация точек для водных провинций (в два раза реже)
        int waterProvinceCount = landProvinceCount / 2;
        for (int i = 0; i < waterProvinceCount; i++) {
            int x, y;
            do {
                x = random.nextInt(mapHeights.getWidth());
                y = random.nextInt(mapHeights.getHeight());
            } while (mapHeights.get(x, y) > WATER_LEVEL); // Выбираем только воду
            waterSeeds.add(new Point(x, y));
        }

        // Генерация точек для сухопутных провинций
        for (int i = 0; i < landProvinceCount; i++) {
            int x, y;
            do {
                x = random.nextInt(mapHeights.getWidth());
                y = random.nextInt(mapHeights.getHeight());
            } while (mapHeights.get(x, y) <= WATER_LEVEL); // Выбираем только сушу
            landSeeds.add(new Point(x, y));
        }

        List<Point> allSeeds = new ArrayList<>();
        allSeeds.addAll(waterSeeds);
        allSeeds.addAll(landSeeds);
        isWaterProvince = new boolean[allSeeds.size()];

        for (int i = 0; i < waterSeeds.size(); i++) {
            isWaterProvince[i] = true;
        }

        // Заполняем карту провинций с учетом принадлежности к воде или суше
        for (int x = 0; x < mapHeights.getWidth(); x++) {
            for (int y = 0; y < mapHeights.getHeight(); y++) {
                boolean isWater = mapHeights.get(x, y) <= WATER_LEVEL;
                int closestIndex = -1;
                double minDistance = Double.MAX_VALUE;
                for (int i = 0; i < allSeeds.size(); i++) {
                    if (isWaterProvince[i] == isWater) {
                        double distance = allSeeds.get(i).distanceTo(x, y);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestIndex = i;
                        }
                    }
                }
                provinceMap[x][y] = closestIndex;
            }
        }
    }

    public int[][] getProvinceMap() {
        return provinceMap;
    }

}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }
}
