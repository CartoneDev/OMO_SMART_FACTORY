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

    /**
     * Retrieves processors from the pool
     * @param name name of the processors
     * @param amount amount of processors
     * @return list of processors
     */
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

    /**
     * Returns processors to the pool
     * @param processors list of processors to be returned
     */
    public void returnProcessors(ArrayList<Processor> processors) {
        for (Processor processor : processors) {
            returnProcessor(processor);
        }
    }

    /**
     * Returns processor to the pool
     * @param processor processor to be returned
     */
    public void returnProcessor(Processor processor) {
        processor.setProductionChain(null);
        processorPool.get(processor.getName()).add(processor);
    }

    /**
     * Returns the size of the pool
     * @param name name of the processor
     * @return size of the pool
     */
    public int getPoolSize(String name) {
        return processorPool.get(name).size();
    }

    /**
     * Collects all events from the pool
     * @return list of events
     */
    public ArrayList<Event> collectEvents() {
        return (ArrayList<Event>) processorPool.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .flatMap(p -> p.getWaybackMachine()
                        .getEvents().stream())
                .collect(Collectors.toList());
    }
}
