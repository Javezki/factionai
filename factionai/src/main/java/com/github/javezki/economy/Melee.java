package com.github.javezki.economy;

public enum Melee {
    
    DOWNRANGE("Downrage", 0);

    private String name;
    private double price;
    private final String imageURL = "factionai\\src\\main\\java\\com\\github\\javezki\\assets\\melee\\";

    Melee(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double price() {
        return price;
    }

    public String getImageURL() {
        return imageURL + getName();
    }

    public static Melee getMelee(String name) {
        return valueOf(name);
    }
}
