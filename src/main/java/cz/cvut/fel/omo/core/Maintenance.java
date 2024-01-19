package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.core.event.PriorityEvent;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.extern.slf4j.XSlf4j;

import java.util.*;


@XSlf4j (topic = "MAINTENANCE")
public class Maintenance {
    private final PriorityQueue<PriorityEvent> priorityQueue = new PriorityQueue<>();
    private ArrayList<Processor> repairSquad = new ArrayList<>();
    private final ArrayList<AbstractMap.SimpleEntry<Processor, PriorityEvent>> currentRepair = new ArrayList<>();
    public Maintenance(ProcessorPool ppl){
        repairSquad = ppl.getProcessors("repairman", ppl.getPoolSize("repairman"));
    }
    public void incidentReported(PriorityEvent e){
        priorityQueue.add(e);
    }

    public void tick(){
        handleReportedIncidents();

        handleRepair();

    }
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
                iterator.remove();
                log.info("({}h)Repair of " + toRepair.getName() + " finished", Clock.getTime());
                repairSquad.add(p);
            }
        }

    }

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

    private void handleWorkerRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - 0.1;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    private void handleRobotRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - 0.2;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    private void handleMachineRepair(Processor toRepair) {
        double cur_dam = toRepair.getDamage();
        double new_dam = cur_dam - new java.util.Random().nextDouble() * 0.5;
        if (new_dam < 0){
            new_dam = 0;
        }
        toRepair.setDamage(new_dam);
    }

    public void handleReportedIncidents(){
        while (!repairSquad.isEmpty() && !priorityQueue.isEmpty()){
            PriorityEvent e = priorityQueue.remove();
            Processor p = repairSquad.remove(0);
            Processor toRepair = (Processor) e.getPayload();
            log.info("({}h) Starting repair of " + toRepair, Clock.getTime());
            toRepair.addEvent(new Event(EventType.PROCESSOR_START_REPAIR, p, toRepair));
            currentRepair.add(new AbstractMap.SimpleEntry<>(p, e));
        }
    }


}
