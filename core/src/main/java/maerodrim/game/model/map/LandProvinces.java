package maerodrim.game.model.map;

import maerodrim.game.model.base.Culture;
import maerodrim.game.model.base.Religion;
import maerodrim.game.model.map.resources.ProvinceResources;
import maerodrim.game.model.settlement.Settlement;

import java.util.UUID;

public class LandProvinces implements Provinces {
    private final UUID idProvinces;
    private final Landscape landscape;
    private final Climate climate;
    private Settlement settlement;
    private Culture culture;
    private Religion religion;
    private final ProvinceResources provinceResources;

    public LandProvinces(UUID idProvinces, Landscape landscape,
                         Climate climate, Settlement settlement,
                         Culture culture, Religion religion, ProvinceResources provinceResources) {
        this.idProvinces = idProvinces;
        this.landscape = landscape;
        this.climate = climate;
        this.settlement = settlement;
        this.culture = culture;
        this.religion = religion;
        this.provinceResources = provinceResources;
    }

    public UUID getIdProvinces() {
        return idProvinces;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public Climate getClimate() {
        return climate;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public Culture getCulture() {
        return culture;
    }

    public Religion getReligion() {
        return religion;
    }

    public ProvinceResources getProvinceResources() {
        return provinceResources;
    }

    public void setReligion(Religion religion) {
        this.religion = religion;
    }

    public void setCulture(Culture culture) {
        this.culture = culture;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
}
