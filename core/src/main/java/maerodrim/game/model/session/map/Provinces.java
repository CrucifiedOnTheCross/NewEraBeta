package maerodrim.game.model.session.map;

import maerodrim.game.model.session.map.resources.ProvinceResources;

import java.util.UUID;

public interface Provinces {
    UUID getIdProvinces();

    Landscape getLandscape();

    Climate getClimate();

    ProvinceResources getProvinceResources();
}
