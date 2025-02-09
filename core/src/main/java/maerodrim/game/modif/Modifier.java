package maerodrim.game.modif;

import maerodrim.game.model.RecalculationTime;
import maerodrim.game.model.player.Player;
import maerodrim.game.model.session.map.Provinces;

public abstract class Modifier {
    protected String nameModifier;
    protected RecalculationTime recalculationTime;

    public Modifier(String nameModifier, RecalculationTime recalculationTime) {
        this.nameModifier = nameModifier;
        this.recalculationTime = recalculationTime;
    }

    public abstract Double getRecalculationModifier(Provinces provinces);

    public abstract Double getRecalculationModifier(Player player);

    public abstract Double getRecalculationModifier(Provinces provinces, Player player);
}
