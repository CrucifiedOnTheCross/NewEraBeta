package maerodrim.game.model.map;

import maerodrim.game.model.base.Culture;
import maerodrim.game.model.base.Religion;
import maerodrim.game.model.map.resources.ProvinceResources;
import maerodrim.game.model.settlement.Settlement;

import java.util.UUID;

public class Provinces {
    private UUID id_provinces;
    private Landscape landscape;
    private Climate climate;
    private Settlement settlement;
    private Culture culture;
    private Religion religion;
    private ProvinceResources provinceResources;
}
