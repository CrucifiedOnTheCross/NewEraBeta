package maerodrim.game.map;

import com.github.czyzby.noise4j.map.Grid;

public class Map {

    private Grid mapHeights;
    private int[][] mapProvince;

    public Map(Grid mapHeights, int[][] provinceCard) {
        this.mapHeights = mapHeights;
        this.mapProvince = provinceCard;
    }

    public Grid getMapHeights() {
        return mapHeights;
    }

    public void setMapHeights(Grid mapHeights) {
        this.mapHeights = mapHeights;
    }

    public int[][] getMapProvince() {
        return mapProvince;
    }

    public void setMapProvince(int[][] mapProvince) {
        this.mapProvince = mapProvince;
    }
}
