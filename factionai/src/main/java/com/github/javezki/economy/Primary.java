package com.github.javezki.economy;

public enum Primary {

    AA_12("AA-12", 500),
    AK_74N("AK74N", 0);

    private String name;
    private final String imageURL = "factionai\\src\\main\\java\\com\\github\\javezki\\assets\\primary\\";
    private double cost;

    Primary(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getImageUrl() {
        return imageURL + getName();
    }

    /**
     * @apiNote ONLY USE THIS FOR RETRIEVING FROM DATABASE
     * @param name
     * @return
     */
    public static Primary getPrimary(String name) {
        return Primary.valueOf(name);
    }
}
