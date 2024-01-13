package cz.cvut.fel.omo;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.Config;
import lombok.extern.slf4j.XSlf4j;

import java.io.FileNotFoundException;
import java.util.Scanner;


@XSlf4j(topic = "SIM")
public class Simulation {

    private static SmartFactory factory;

    static Integer slowdownms = 350;
    static boolean processing = true;
    static boolean running = false;
    static Clock clock;
    static Integer set_hours = 0;

    public static void main(String[] args) {
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
//        factory = Config.getFactory();
//        set_hours = Config.getSetHours();
    }

    static void run_simulation() {
        log.info("Simulation started!");
        while (processing){
            // Maybe separate thread for input would be more appropriate
            handleInput();

            if (running && clock.getTicks() < set_hours) {
                factory.tick();
                clock.tick();
                handleSlowdown(slowdownms);
            }
            else if (clock.getTicks() >= set_hours) {
                running = false;
                log.info("Simulation finished!");
            }
        }
        log.info("Simulation stopped!");
    }

    private static void handleSlowdown(Integer slowdownms) {
        try {
            Thread.sleep(Simulation.slowdownms);
        } catch (InterruptedException e) {
            log.error("Error while sleeping!");
        }
    }

    private static void handleInput() {
        try {
            if (! (System.in.available() > 0)) return; // Don't block main thread
            log.debug("Input detected pausing simulation");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.startsWith("/loadConfig")) {
                handleLoadConfig(input);
            } else if (input.startsWith("/start")) {
                running = true;
            } else if (input.startsWith("/stop")) {
                running = false;
            } else if (input.startsWith("/setHours")) {
                set_hours = Integer.parseInt(input.split(" ")[1]);
            } else if (input.startsWith("/tick")) {
                handleTick(input);
            } else if (input.startsWith("/help")) {
                handleHelp();
            } else if (input.startsWith("/report")) {

            } else if (input.startsWith("/exit")) {
                processing = false;
            } else {
                log.error("Unknown command!");
            }



        }
        catch (Exception e) {
            log.error("Error while reading input!");
        }

//
    }

    private static void handleHelp() {
        log.info("These programs allows you run a factory simulation by a given config file.");
        handleSlowdown(450);
        log.info("Commands:");
        handleSlowdown(250);
        log.info("/loadConfig -d -ff <path> - loads config file from given path");
        handleSlowdown(150);
        log.info("    -d - loads default config file");
        handleSlowdown(150);
        log.info("    -ff - enables fast config mode");
        handleSlowdown(250);
        log.info("/start - starts simulation");
        log.info("/stop - stops simulation");
        handleSlowdown(250);
        log.info("/setHours <hours> - sets number of hours to run simulation");
        log.info("/tick <ticks> - runs given number of ticks, 1 by default");
        handleSlowdown(250);

    }

    private static void handleTick(String input) {
        String[] args = input.split(" ");
        Integer ticks = 1;
        if (args.length > 1) {
            try {
                ticks = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.error("Invalid number of ticks!");
            }
        }

        for (int i = 0; i < ticks; i++) {
            factory.tick();
            clock.tick();
        }
    }

    private static void handleLoadConfig(String input) {
        Config.clear();
        Config.setFastConfig(input.contains("-ff"));
        try {
        if (input.contains("-d")){
            log.info("Loading default config");
            Config.loadConfig("src/main/resources/example.config.json");
            return;
            }
        }catch (FileNotFoundException e) {
            log.error("Config file not found!");
        } catch (JsonProcessingException e) {
            log.error("Error while processing example config file!");
        }

        String[] args = input.split(" ");
        String path = args[args.length-1];
        try {
            Config.loadConfig(path);
        } catch (FileNotFoundException e) {
            log.error("Config file not found!");
        } catch (JsonProcessingException e) {
            log.error("Error while processing given config file!");
        }
    }


}
