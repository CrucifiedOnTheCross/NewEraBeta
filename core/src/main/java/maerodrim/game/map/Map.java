package maerodrim.game.map;

import com.github.czyzby.noise4j.map.Grid;

public class Map {

    private Grid mapHeights;

    public Map(Grid mapHeights) {
        this.mapHeights = mapHeights;
    }

    public Grid getMapHeights() {
        return mapHeights;
    }

    public void setMapHeights(Grid mapHeights) {
        this.mapHeights = mapHeights;
    }

}
