package cz.cvut.fel.omo;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.Config;
import lombok.extern.slf4j.XSlf4j;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;


@XSlf4j(topic = "SIM")
public class Simulation {

    private static SmartFactory factory;

    static Integer slowdown_ms = 350;
    static boolean processing = true;
    static boolean running = false;
    static Clock clock;
    static Integer set_hours = -1;

    public static void main(String[] args) {
        init();
        run_simulation();
    }

    private static void init() {
        log.info("Setting clock");
        clock = Clock.getTimer();
    }


    static void run_simulation() {
        log.info("Simulation launched!");

        handleLoadConfig("-d"); // DEBUG ONLY
        running = true;

        while (processing){
            // Maybe separate thread for input would be more appropriate
            handleInput();

            if (running) {
                if (factory != null){
                    factory.tick();
                }else{
                    log.error("Factory not initialized!");
                    handleSlowdown(slowdown_ms *3);
                    log.info("Pausing simulation");
                    running=false;
                }
                clock.tick();
                handleSlowdown(slowdown_ms);
            }
            else if (Objects.equals(clock.getTicks(), set_hours)) {
                running = false;
                log.info("Simulation finished!");
            }
        }
        log.info("Simulation stopped!");
    }

    private static void handleSlowdown(Integer slowdownms) {
        try {
            Thread.sleep(slowdownms);
        } catch (InterruptedException e) {
            log.error("Error while sleeping!");
        }
    }

    private static void handleInput() {
        try {
            if (! (System.in.available() > 0)) return; // Don't block main thread
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.startsWith("/loadConfig")) {
                handleLoadConfig(input);
            } else if (input.startsWith("/start")) {
                log.info("Starting!");
                running = true;
            } else if (input.startsWith("/stop")) {
                log.info("Pausing!");
                running = false;
            } else if (input.startsWith("/setHours")) {
                handleSetHours(input);
            } else if (input.startsWith("/tick")) {
                handleTick(input);
            } else if (input.startsWith("/help")) {
                handleHelp();
            } else if (input.startsWith("/slowdown")) {
                handleSetSlowdown(input);
            } else if (input.startsWith("/time")) {
                handleShowTime();
            } else if (input.startsWith("/status")) {
                factory.printStatus();
            } else if (input.startsWith("/visit")) {
                handleVisit(input);
            } else if (input.startsWith("/report")) {
                handlePrintReport(input);
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

    private static void handleVisit(String input) {
        String[] args = input.split(" ");
        if (args.length < 2) {
            log.error("No visitor specified!");
            return;
        }
        String visitor = args[1];
        if (visitor.equals("inspector")) {
            log.info("Visiting with inspector gadget");
            factory.inspect();
        } else if (visitor.equals("director")) {
            log.info("Visiting with director");
            factory.direct();
        } else {
            log.error("Unknown visitor!");
        }
    }

    private static void handleShowTime() {
        log.info("Current time passed is {} hours", clock.getTicks());
    }

    private static void handlePrintReport(String input) {
    }

    private static void handleSetSlowdown(String input) {
        String[] args = input.split(" ");
        int ms = 0;
        if (args.length > 1) {
            try {
                ms = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.error("Invalid number of ms!");
            }
        }
        if (ms < 0) {
            log.error("Invalid can't slow down on negative value!");
            return;
        }
        log.info("Simulation slowdown set to {} ms", ms);
    }

    private static void handleSetHours(String input) {
        String[] args = input.split(" ");
        int hours = 0;
        if (args.length > 1) {
            try {
                hours = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.error("Invalid number of hours!");
            }
        }
        set_hours = hours;
        log.info("Simulation will run for {} hours", hours);
        log.info("Current tick: {}", clock.getTicks());
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
        handleSlowdown(100);
        log.info("/stop - stops simulation");
        handleSlowdown(250);
        log.info("/setHours <hours> - sets number of hours to run simulation, infinite by default");
        handleSlowdown(100);
        log.info("/tick <ticks> - runs given number of ticks, 1 by default");
        handleSlowdown(250);
        log.info("/slowdown <ms> - sets simulation slowdown in ms, 350 by default");
        handleSlowdown(350);
        log.info("/time - shows current time passed");

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
        if (input.contains("-d")){
            try {

            log.info("Loading default config");
//            Config.loadConfig("src/main/resources/30.plus.json");
            Config.loadConfig("src/main/resources/example.config.json");
        }catch (FileNotFoundException e) {
            log.error("Config file not found!");
            return;
        } catch (JsonProcessingException e) {
            log.error("Error while processing example config file!");
            return;
        }
        } else {

        String[] args = input.split(" ");
        String path = args[args.length-1];
        try {
            log.info("Loading config from {}", path);
            Config.loadConfig(path);
        } catch (FileNotFoundException e) {
            log.error("Config file not found!");
        } catch (JsonProcessingException e) {
            log.error("Error while processing given config file!");
        }
        }
        log.info("Config loaded!");
        try {
            factory = Config.buildFactory();
        }catch (Exception e){
            log.error("Error while building factory!");
        }
        init();
    }

}
