package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.model.ProductionChain;

import java.util.HashMap;

public class SmartFactory {
    private static SmartFactory instance;

    private HashMap<Integer, ProductionChain> links;

    private SmartFactory() {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        instance = this;
    }
    public static SmartFactory getInstance() {
        if (instance == null) {
            new SmartFactory();
        }
        return instance;
    }

    public void tick() { // each tick equals to 1 realtime hour
        links.forEach((k, v) -> v.tick());
    }
}
