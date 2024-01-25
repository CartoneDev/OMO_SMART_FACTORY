package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.core.event.PriorityEvent;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.extern.slf4j.XSlf4j;

import java.util.*;


/**
 * Class representing the maintenance department of the factory
 */
@XSlf4j (topic = "MAINTENANCE")
public class Maintenance {
    private final PriorityQueue<PriorityEvent> priorityQueue = new PriorityQueue<>();
    private final Set<Event> resolvedEvents = new HashSet<>();
    private ArrayList<Processor> repairSquad = new ArrayList<>();
    private final ArrayList<AbstractMap.SimpleEntry<Processor, PriorityEvent>> currentRepair = new ArrayList<>();
    /**
     * Constructor for the maintenance department
     * @param ppl processor pool to get repairmen from
     */
    public Maintenance(ProcessorPool ppl){
        repairSquad = ppl.getProcessors("repairman", ppl.getPoolSize("repairman"));
    }

    /**
     * Reports an incident to the maintenance department
     * @param e event to be reported
     */
    public void incidentReported(PriorityEvent e){
        priorityQueue.add(e);
    }

    /**
     * Ticks the maintenance department
     */
    public void tick(){
        handleReportedIncidents();

        handleRepair();

    }

    /**
     * Handles the repair of the processors
     */
    public void handleRepair(){
        Iterator<AbstractMap.SimpleEntry<Processor, PriorityEvent>> iterator = currentRepair.iterator();
        while (iterator.hasNext()) {
            AbstractMap.SimpleEntry<Processor, PriorityEvent> entry = iterator.next();
            Processor p = entry.getKey();
            PriorityEvent e = entry.getValue();

            Processor toRepair = (Processor) e.getPayload();
            handleProcessorRepair(p, toRepair);

            if (toRepair.getDamage() < new Random().nextDouble() * 0.15) {
                Event repaired = new Event(EventType.PROCESSOR_REPAIRED, toRepair, p);
                toRepair.addEvent(repaired);
                e.setSolver(repaired);
                resolvedEvents.add(e);
                iterator.remove();
                log.info("({}h)Repair of " + toRepair.getName() + " finished", Clock.getTime());
                repairSquad.add(p);
            }
        }

    }

    /**
     * Handles the repair of the processor
     * @param p repairman
     * @param toRepair processor to be repaired
     */
    private void handleProcessorRepair(Processor p, Processor toRepair) {
        switch (toRepair.getType())
        {
            case "machine":
                handleMachineRepair(toRepair);
            break;
            case "robot":
                handleRobotRepair(toRepair);
            break;
            case "worker":
                handleWorkerRepair(toRepair);
        }
    }

    /**
     * Handles the repair of the worker
     * @param toRepair worker to be repaired
     */
    private void handleWorkerRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - 0.1;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    /**
     * Handles the repair of the robot
     * @param toRepair
     */
    private void handleRobotRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - 0.2;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    /**
     * Handles the repair of the machine
     * @param toRepair
     */
    private void handleMachineRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - new java.util.Random().nextDouble() * 0.5;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    /**
     * Handles the newly(by priority queue order) reported incidents
     */
    public void handleReportedIncidents(){
        while (!repairSquad.isEmpty() && !priorityQueue.isEmpty()){
            PriorityEvent e = priorityQueue.remove();
            Processor p = repairSquad.remove(0);
            Processor toRepair = (Processor) e.getPayload();
            log.info("({}h) Starting repair of " + toRepair, Clock.getTime());
            Event repairStarted = new Event(EventType.PROCESSOR_START_REPAIR, e, p);
            toRepair.addEvent(repairStarted);
            resolvedEvents.add(repairStarted);
            currentRepair.add(new AbstractMap.SimpleEntry<>(p, e));
        }
    }


    /**
     * Returns all events
     * @return set of all events
     */
    public Set<Event> getEvents() {
        HashSet<Event> eventsAll = new HashSet<>(priorityQueue);
        eventsAll.addAll(resolvedEvents);
        eventsAll.addAll(currentRepair.stream().map(AbstractMap.SimpleEntry::getValue).toList());
        return eventsAll;
    }
}
