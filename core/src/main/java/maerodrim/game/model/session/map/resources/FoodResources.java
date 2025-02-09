package maerodrim.game.model.session.map.resources;

public enum FoodResources {
    RICE("rice", 2.0, 1.0),
    WHEAT("wheat", 2.0, 2.0),
    CATTLE("cattle", 1.5, 3.0);

    private String name;
    private Double food;
    private Double cost;

    FoodResources(String name, Double food, Double cost) {
        this.name = name;
        this.food = food;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFood() {
        return food;
    }

    public void setFood(Double food) {
        this.food = food;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
