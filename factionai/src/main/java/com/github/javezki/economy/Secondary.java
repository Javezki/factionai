package com.github.javezki.economy;

public enum Secondary {
    
    NONE("None", 0), 
    B75("B-75", 0);

    private String name;
    private double price;
    private final String imageURL = "factionai\\src\\main\\java\\com\\github\\javezki\\assets\\secondary\\";

    Secondary(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String imageURL() {
        return imageURL + name;
    }

    public static Secondary getSecondary(String name) {
        return valueOf(name);
    }
}
