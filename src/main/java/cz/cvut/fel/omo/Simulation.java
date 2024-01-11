package cz.cvut.fel.omo;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.Config;
import lombok.extern.slf4j.XSlf4j;

import java.io.FileNotFoundException;


@XSlf4j(topic = "SIM")
public class Simulation {

    static SmartFactory factory;

    static boolean running = false;
    static Clock clock;
    private static Integer set_hours = 0;

    public static void main(String[] args) throws FileNotFoundException, JsonProcessingException {
        System.out.println("Hello/loadconfig/etc");
        load_config(); // TODO: <config
        run_simulation();//TODO: <simulation
//        report(); //TODO: <report
    }

    private static void load_config() {
        try {
            Config.loadConfig("src/main/resources/example.config.json");
        } catch (FileNotFoundException e ) {
            log.error("Config file not found!");
        } catch (JsonProcessingException e) {
            log.error("Error while processing config file!");
        }

        clock = Clock.getTimer();
        factory = SmartFactory.getInstance();
        set_hours = 100 * 24;
    }

    private static void run_simulation() {
        while (true){
            // Maybe separate thread for input would be more appropriate
            handleInput();

            if (running && clock.getTicks() < set_hours) {
                factory.tick();
                clock.tick();
            }
        }
    }

    private static void handleInput() {
        String input = System.console().readLine(); // parallize this
        if (input.equals("exit")) {
            System.exit(0);
        }
    }


}
