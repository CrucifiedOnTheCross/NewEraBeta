package maerodrim.game.modif.prov;

import maerodrim.game.error_warn.WarnMessage;
import maerodrim.game.model.ClimateType;
import maerodrim.game.model.RecalculationTime;
import maerodrim.game.model.player.Player;
import maerodrim.game.model.session.map.Provinces;
import maerodrim.game.model.session.map.resources.FoodResources;
import maerodrim.game.modif.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class FoodModifier extends Modifier {
    Logger logger = LoggerFactory.getLogger(FoodModifier.class);
    protected HashMap<ClimateType, Double> climateModifier;
    protected HashMap<ClimateType, Double> landscapeModifier;

    public FoodModifier() {
        super("food-modifier", RecalculationTime.DAILY);
    }

    @Override
    public Double getRecalculationModifier(Provinces provinces) {
        List<FoodResources> foodResources = provinces.getProvinceResources().getFoodResources();
        return 1 + (climateModifier.get(provinces.getClimate()) * landscapeModifier.get(provinces.getLandscape()));
    }

    @Override
    public Double getRecalculationModifier(Player player) {
        logger.warn(WarnMessage.WARN_API_NOT_SUPPORT);
        return 0.0;
    }

    @Override
    public Double getRecalculationModifier(Provinces provinces, Player player) {
        return getRecalculationModifier(provinces);
    }
}
