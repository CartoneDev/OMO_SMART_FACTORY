package cz.cvut.fel.omo.core;

public class Clock { // conuter alike clock + date for reporting
    private static Clock instance;
    private Integer ticks = 0;

    public Clock(Integer ticks) {
        this.ticks = ticks;
    }

    public static Clock getTime() {
        if (instance == null) {
            instance = new Clock(0);
        }
        return instance.copy();
    }

    public static Clock getTimer() {
        if (instance == null) {
            instance = new Clock(0);
        }
        return instance;
    }

    private Clock copy() {
        return new Clock(ticks);
    }

    public void tick() { // each tick equals to 1 realtime hour
        ticks++;
    }

    public Integer getTicks() {
        return ticks;
    }

    @Override
    public String toString(){
        return ticks.toString();
    }
}
