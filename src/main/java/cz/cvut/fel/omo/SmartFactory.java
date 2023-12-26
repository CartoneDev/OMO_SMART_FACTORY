package cz.cvut.fel.omo;

public class SmartFactory {
    private static SmartFactory instance;
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
    }
}
