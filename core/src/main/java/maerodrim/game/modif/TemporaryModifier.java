package maerodrim.game.modif;

import maerodrim.game.model.RecalculationTime;

public abstract class TemporaryModifier extends Modifier {
    public TemporaryModifier(String nameModifier) {
        super(nameModifier, RecalculationTime.TEMPORARY);
    }
}
