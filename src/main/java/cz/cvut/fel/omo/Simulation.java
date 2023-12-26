package cz.cvut.fel.omo;

import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;

public class Simulation {

    static SmartFactory factory;
    static Clock clock;
    private static Integer set_hours = 0;

    public static void main(String[] args) {
        System.out.println("Hello/loadconfig/etc");
        load_config(); // TODO: <config
        run_simulation();//TODO: <simulation
//        report(); //TODO: <report
    }

    private static void load_config() {

        clock = Clock.getTimer();
        factory = SmartFactory.getInstance();
        set_hours = 100 * 24;
    }

    private static void run_simulation() {
        while (clock.getTicks() < set_hours) {
            factory.tick();
            clock.tick();
        }
    }


}
