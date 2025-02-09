package maerodrim.game.map.generator;

import com.github.czyzby.noise4j.map.Grid;

import java.util.*;

public class GeneratorProvince {

    private static final float WATER_LEVEL = 0.48f;
    private static final int LAND_PASS_COST = 1;
    private static final int SEA_PASS_COST = 2; // Моря заполняются чуть медленнее
    private final Grid mapHeights;
    private final int[][] provinceMap;
    private final Random random = new Random();

    public GeneratorProvince(Grid heightMap) {
        this.mapHeights = heightMap;
        this.provinceMap = new int[mapHeights.getWidth()][mapHeights.getHeight()];
    }

    public void generateProvinces(int seaProvinces, int landProvinces) {
        List<Point> seaCenters = generateGridBasedCenters(seaProvinces, true);
        List<Point> landCenters = generateGridBasedCenters(landProvinces, false);

        growProvinces(seaCenters, true);
        growProvinces(landCenters, false);

        mergeSmallProvinces(200);
    }

    private List<Point> generateGridBasedCenters(int count, boolean isSea) {
        List<Point> centers = new ArrayList<>();
        int gridSizeX = (int) Math.sqrt(count * 2); // Колонки сетки
        int gridSizeY = count / gridSizeX + 1; // Ряды сетки
        int cellWidth = mapHeights.getWidth() / gridSizeX;
        int cellHeight = mapHeights.getHeight() / gridSizeY;

        for (int gx = 0; gx < gridSizeX; gx++) {
            for (int gy = 0; gy < gridSizeY; gy++) {
                if (centers.size() >= count) break;

                int attempts = 0;
                while (attempts < 10) { // Попытки найти подходящую точку
                    int x = gx * cellWidth + random.nextInt(cellWidth);
                    int y = gy * cellHeight + random.nextInt(cellHeight);

                    float height = mapHeights.get(x, y);
                    if ((isSea && height < WATER_LEVEL) || (!isSea && height >= WATER_LEVEL)) {
                        centers.add(new Point(x, y));
                        break;
                    }
                    attempts++;
                }
            }
        }
        return centers;
    }

    private void growProvinces(List<Point> centers, boolean isSea) {
        PriorityQueue<Tile> queue = new PriorityQueue<>(Comparator.comparingInt(t -> t.cost));
        Map<Point, Point> provinceCenters = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        // Добавляем центры провинций
        for (int i = 0; i < centers.size(); i++) {
            Point center = centers.get(i);
            queue.add(new Tile(center.x, center.y, i + 1, 0));
            visited.add(center);
            provinceCenters.put(center, center); // Запоминаем центр для каждого региона
        }

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            provinceMap[current.x][current.y] = current.provinceId;
            Point provinceCenter = provinceCenters.get(new Point(current.x, current.y));

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (!isValid(nx, ny) || visited.contains(new Point(nx, ny)))
                    continue;

                float height = mapHeights.get(nx, ny);
                boolean isWater = height < WATER_LEVEL;

                if (!isSea && isWater) continue;
                if (isSea && !isWater) continue;

                int moveCost = isWater ? SEA_PASS_COST : LAND_PASS_COST;

                double distance = Math.sqrt(Math.pow(nx - provinceCenter.x, 2) + Math.pow(ny - provinceCenter.y, 2));
                int roundnessPenalty = (int) Math.pow(distance * 0.5, 2);

                queue.add(new Tile(nx, ny, current.provinceId, current.cost + moveCost + roundnessPenalty));
                visited.add(new Point(nx, ny));
                provinceCenters.put(new Point(nx, ny), provinceCenter);
            }
        }

        smoothProvinces();
    }

    // Сглаживание границ после генерации
    private void smoothProvinces() {
        int width = mapHeights.getWidth();
        int height = mapHeights.getHeight();
        int[][] newMap = new int[width][height];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int[] neighbors = new int[8];
                int count = 0;

                // Считаем соседние провинции
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        neighbors[count++] = provinceMap[x + dx][y + dy];
                    }
                }

                Map<Integer, Integer> freq = new HashMap<>();
                for (int n : neighbors) freq.put(n, freq.getOrDefault(n, 0) + 1);
                int dominantProvince = provinceMap[x][y];
                int maxCount = 0;

                for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        dominantProvince = entry.getKey();
                    }
                }

                newMap[x][y] = dominantProvince;
            }
        }

        // Копируем обновлённую карту провинций
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                provinceMap[x][y] = newMap[x][y];
            }
        }
    }

    private void mergeSmallProvinces(int minSize) {
        Map<Integer, Integer> provinceSizes = new HashMap<>();

        for (int x = 0; x < provinceMap.length; x++) {
            for (int y = 0; y < provinceMap[0].length; y++) {
                int provinceId = provinceMap[x][y];
                provinceSizes.put(provinceId, provinceSizes.getOrDefault(provinceId, 0) + 1);
            }
        }

        Set<Integer> smallProvinces = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : provinceSizes.entrySet()) {
            if (entry.getValue() < minSize) {
                smallProvinces.add(entry.getKey());
            }
        }

        for (int x = 0; x < provinceMap.length; x++) {
            for (int y = 0; y < provinceMap[0].length; y++) {
                int provinceId = provinceMap[x][y];
                if (smallProvinces.contains(provinceId)) {
                    int newProvinceId = findNearestLargeProvince(x, y, smallProvinces);
                    if (newProvinceId != -1) {
                        provinceMap[x][y] = newProvinceId;
                    }
                }
            }
        }
    }

    private int findNearestLargeProvince(int x, int y, Set<Integer> smallProvinces) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        queue.add(new Point(x, y));
        visited.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int[] dir : directions) {
                int nx = p.x + dir[0];
                int ny = p.y + dir[1];

                if (isValid(nx, ny)) {
                    int neighborProvince = provinceMap[nx][ny];
                    if (!smallProvinces.contains(neighborProvince)) {
                        return neighborProvince; // Нашли крупную провинцию
                    }
                    if (!visited.contains(new Point(nx, ny))) {
                        queue.add(new Point(nx, ny));
                        visited.add(new Point(nx, ny));
                    }
                }
            }
        }
        return -1; // Если не найдено (маловероятно)
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < mapHeights.getWidth() && y >= 0 && y < mapHeights.getHeight();
    }

    public int[][] getProvinceMap() {
        HashSet<Integer> provinceId = new HashSet<>();
        for (int x = 0; x < mapHeights.getWidth(); x++) {
            for (int y = 0; y < mapHeights.getHeight(); y++) {
                provinceId.add(provinceMap[x][y]);
            }
        }
        System.out.println(provinceId.size());
        return provinceMap;
    }
}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

class Tile {
    int x, y, provinceId, cost;

    Tile(int x, int y, int provinceId, int cost) {
        this.x = x;
        this.y = y;
        this.provinceId = provinceId;
        this.cost = cost;
    }
}
