package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.Config;
import lombok.extern.slf4j.XSlf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Object pool for processors, processor may preserve its state and be reused
 */
@XSlf4j(topic = "PROC_POOL")
public class ProcessorPool {
    private final HashMap<String, ArrayList<Processor>> processorPool;
    public ProcessorPool(HashMap<String, ArrayList<Processor>> processorPool) {
        this.processorPool = processorPool;
    }

    public ArrayList<Processor> getProcessors(String name, Integer amount) {
        if (!Config.hasProcessor(name) && !name.equals("repairman")) {
            log.error("Processor " + name + " is not defined in config");
        }
        ArrayList<Processor> processors = new ArrayList<>();
        if (processorPool.containsKey(name) && processorPool.get(name).size() >= amount) {
            ArrayList<Processor> pool = processorPool.get(name);
            for (int i = 0; i < amount; i++) {
                processors.add(pool.remove(0));
            }
        }else {

            return null;
        }

        return processors;
    }

    public void returnProcessors(ArrayList<Processor> processors) {
        for (Processor processor : processors) {
            returnProcessor(processor);
        }
    }
    public void returnProcessor(Processor processor) {
        processor.setProductionChain(null);
        processorPool.get(processor.getName()).add(processor);
    }

    public int getPoolSize(String name) {
        return processorPool.get(name).size();
    }

    public ArrayList<Event> collectEvents() {
        return (ArrayList<Event>) processorPool.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .flatMap(p -> p.getWaybackMachine()
                        .getEvents().stream())
                .collect(Collectors.toList());
    }
}
