package maerodrim.game.model.player.settlement;


import maerodrim.game.model.player.tech.TechnologicalProgress;

import java.util.UUID;

public class Settlement {
    private UUID id;
    private Integer population;
    private Buildings buildings;
    private Products products;
    private TechnologicalProgress technologicalProgress;
}
