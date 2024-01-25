package cz.cvut.fel.omo;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.report.ConsumptionReport;
import cz.cvut.fel.omo.core.report.EventReport;
import cz.cvut.fel.omo.core.report.FactoryConfigurationReport;
import cz.cvut.fel.omo.core.report.OutagesReport;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.utility.Config;
import lombok.extern.slf4j.XSlf4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;


/**
 * Main static class and entry point of the program
 * handles user input and simulation process
 */
@XSlf4j(topic = "SIM")
public class Simulation {

    private static final double DIRECTOR_CHANCE_PER_TICK = 0.001221;
    private static final double INSPECTOR_CHANCE_PER_TICK = 0.001111;
    private static SmartFactory factory;

    static Integer slowdown_ms = 35;
    static boolean processing = true;
    static boolean running = false;
    static Clock clock;
    static Integer set_hours = -1;

    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        init();
        run_simulation();
    }

    /**
     * Initializes simulation
     */
    private static void init() {
        log.info("Setting clock");
        clock = Clock.getTimer();
    }

    /**
     * Runs simulation
     */
    static void run_simulation() {
        log.info("Simulation launched!");

        handleLoadConfig("-ff -d"); // DEBUG ONLY
        running = true;

        while (processing){
            // Maybe separate thread for input would be more appropriate
            handleInput();

            if (running) {
                handleRandomEvents();
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

    /**
     * Handles random events
     */
    private static void handleRandomEvents() {
        if (Math.random() < INSPECTOR_CHANCE_PER_TICK) {
            log.info("Wild inspector appears!");
            factory.inspect();
        }
        if (Math.random() < DIRECTOR_CHANCE_PER_TICK) {
            log.info("Director decides to check up on{}", factory.getName());
            factory.direct();
        }
    }

    /**
     * Handles slowdown between ticks and makes help more readable
     * @param slowdownms slowdown in ms
     */
    private static void handleSlowdown(Integer slowdownms) {
        try {
            Thread.sleep(slowdownms);
        } catch (InterruptedException e) {
            log.error("Error while sleeping!");
        }
    }

    /**
     * Handles input from console
     */
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
                factory.printStatus(Clock.getTime().getTicks());
            } else if (input.startsWith("/stateOn")){
                handleStateOn(input);
            }else if (input.startsWith("/visit")) {
                handleVisit(input);
            } else if (input.startsWith("/report"))  {
                handlePrintReport(input);
            } else if (input.startsWith("/prod")){
                handlePrintProductionTotal(input);
            }
            else if (input.startsWith("/reassemble")) {
                handleLinkReassemble();
            } else if (input.startsWith("/exit")) {
                processing = false;
            } else {
                log.error("Unknown command!");
            }
        }
        catch (Exception e) {
            log.error("Error while reading input!");
        }
    }

    /**
     * Handles printing of production total
     * @param input {command} <time>
     */
    private static void handlePrintProductionTotal(String input) {
        String[] args = input.split(" ");
        int time = Clock.getTime().getTicks();
        if (args.length == 2) {
            try {
                time = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.error("Invalid time!");
                log.info("Usage: /prod <time>");
            }
        }else {
            log.info("Usage: /prod <time>");
        }
        if (time < 0 || time > Clock.getTime().getTicks()) {
            log.error("Invalid time!");
            log.info("Time must be in range <0, {}>", Clock.getTime().getTicks());
            return;
        }
        factory.printProductionTotal(time);
    }

    /**
     * Handles reassembling of production chain
     */
    private static void handleLinkReassemble() {
        ArrayList<ProductionChain> links = SmartFactory.getInstance().getLinks();
        System.out.println("Please enter the id of the production chain you want to reassemble:");
        for (ProductionChain productionChain : links) {
            System.out.println(productionChain.getName() + " #" + productionChain.getId() + " " + productionChain.getPriority() +
                    " producing: " + productionChain.getProduct().getName());
        }
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Integer cId = 0;
        try {
            cId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            log.error("Invalid id!");
            return;
        }
        Integer finalCId = cId;
        ProductionChain productionChain = links.stream().filter(link -> link.getId().equals(finalCId)).findFirst().orElse(null);
        if (productionChain == null) {
            log.error("Production chain with id {} not found!", cId);
            return;
        }
        ArrayList<Product> products = Config.getProducts();
        System.out.println("Please enter the new product number, options:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println("#" + i + " " + products.get(i).getName());
        }
        input = scanner.nextLine();
        Integer productNumber = 0;
        try {
            productNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            log.error("Invalid product number!");
            return;
        }
        if (productNumber < 0 || productNumber >= products.size()) {
            log.error("Invalid product number!");
            return;
        }
        if (productionChain.getProduct().getName().equals(products.get(productNumber).getName())) {
            log.info("Product is already set to {}", products.get(productNumber).getName());
            return;
        }
        productionChain.rebuildTo(products.get(productNumber));
    }

    /**
     * Handles printing of factory state at given time
     * @param input {command} <time>
     */
    private static void handleStateOn(String input) {
        String[] args = input.split(" ");
        if (args.length < 2) {
            log.error("No time specified!");
            log.info("Usage: /stateOn <time>");
            return;
        }
        try {
            int time = Integer.parseInt(args[1]);
            if (time < 0 || time > Clock.getTime().getTicks()) {
                log.error("Invalid time!");
                log.info("Time must be in range <0, {}>", Clock.getTime().getTicks());
                return;
            }
            factory.printStatus(time);
        } catch (NumberFormatException e) {
            log.error("Invalid time!");
            log.info("Usage: /stateOn <time>");
        }
    }

    /**
     * Handles visiting of factory with given visitor
     * @param input {command} <visitor> - visitor can be inspector or director
     */
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

    /**
     * Handles printing of current time
     */
    private static void handleShowTime() {
        log.info("Current time passed is {} hours", clock.getTicks());
    }

    /**
     * Handles printing of given report at given time
     * @param input {command} <report> [time]
     */
    private static void handlePrintReport(String input) {
        String[] args = input.split(" ");
        if (args.length < 2) {
            log.error("No report specified!");
            log.info("Usage: /report <report> [time]");
            log.info("Available reports: factory, event, consumption, outages");
            return;
        }

        Integer time;
        try {
            time = args.length > 2 ? Integer.valueOf(args[2]) : Clock.getTime().getTicks();
        } catch (NumberFormatException e) {
            log.error("Invalid time!");
            log.info("Usage: /report <report> [time]");
            log.info("Available reports: factory, event, consumption, outages");
            return;
        }

        String report = args[1];
        switch (report) {
            case "factory" ->
                new FactoryConfigurationReport().generateReport(factory, time);
            case "event" ->
                new EventReport().generateReport(factory, time);
            case "consumption" ->
                new ConsumptionReport().generateReport(factory, time);
            case "outages" ->
                new OutagesReport().generateReport(factory, time);
            default -> log.error("Unknown report!");
        }
    }

    /**
     * Handles setting of simulation slowdown between ticks
     * @param input {command} <ms>
     */
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
        slowdown_ms = ms;
    }

    /**
     * Handles setting of simulation hours
     * Which overrides default behaviour of infinite simulation
     * @param input {command} <hours>
     */
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

    /**
     * Handles printing of help
     */
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
        handleSlowdown(250);
        log.info("/status - shows current status of the factory");
        handleSlowdown(250);
        log.info("/stateOn <time> - shows state of the factory at given time");
        handleSlowdown(250);
        log.info("/visit <visitor> - visits factory with given visitor");
        handleSlowdown(250);
        log.info("/report <report> [time] - prints given report at given time");
        handleSlowdown(250);
        log.info("/exit - exits the program");
        handleSlowdown(250);
        log.info("/help - shows this help");
    }

    /**
     * Handles running for given number of ticks
     * @param input {command} <ticks>
     */
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

    /**
     * Handles loading of config file
     * @param input {command} [-ff] [-d] <path> -ff enables fast config mode, -d loads default config
     */
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
