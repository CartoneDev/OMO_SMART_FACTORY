package cz.cvut.fel.omo;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.Config;

import java.io.FileNotFoundException;

public class Simulation {

    static SmartFactory factory;
    static Clock clock;
    private static Integer set_hours = 0;

    public static void main(String[] args) throws FileNotFoundException, JsonProcessingException {
        System.out.println("Hello/loadconfig/etc");
        load_config(); // TODO: <config
        run_simulation();//TODO: <simulation
//        report(); //TODO: <report
    }

    private static void load_config() throws FileNotFoundException, JsonProcessingException {
        Config.loadConfig("src/main/resources/example.config.json");

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
