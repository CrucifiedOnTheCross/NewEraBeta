package maerodrim.game.model.session.map.resources;

import java.util.List;

public class ProvinceResources {
    private List<NaturalResources> naturalResources;
    private List<FoodResources> foodResources;

    public List<FoodResources> getFoodResources() {
        return foodResources;
    }

    public List<NaturalResources> getNaturalResources() {
        return naturalResources;
    }
}
