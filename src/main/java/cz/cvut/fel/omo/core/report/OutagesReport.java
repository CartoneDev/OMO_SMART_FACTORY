package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.ReportWriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OutagesReport extends ReportMethod{
    public StringBuilder prepareReport(SmartFactory factory, Integer timestamp) {
        Set<Event> events = factory.getMaintenanceEvents().stream().filter(e -> e.getType() == EventType.PROCESSOR_START_REPAIR)
                .filter(e -> ((Event)e.getPayload()).getSolver() != null)
                .filter(e -> ((Event)((Event)e.getPayload()).getSolver()).getTimestamp().getTicks() < timestamp)
                .collect(Collectors.toSet());
        int min =-1, max=-1;
        double avgOutage = 0, avgWait = 0;
        int count = 0;
        Set<Map.Entry<String, Integer>> outages = new HashSet<>();
        for (Event event : events) {
            int brokenAt = ((Event)event.getPayload()).getTimestamp().getTicks();
            int repairStartedAt = (event.getTimestamp().getTicks());
            int repairFinishedAt = ((Event)((Event)event.getPayload()).getSolver()).getTimestamp().getTicks();
            Processor sourceProcessor = (Processor) ((Event)event.getPayload()).getSource();
            int dur = repairFinishedAt - brokenAt;
            String outageDescription = "P[" + sourceProcessor.getId() + "] outage from " + brokenAt + " to " + repairFinishedAt + " (duration: " + dur + ")\n";
            Map.Entry<String, Integer> entry = Map.entry(outageDescription, dur);

            outages.add(entry);

            if (min == -1 || dur < min) min = dur;
            if (max == -1 || dur > max) max = dur;
            avgOutage += dur;
            avgWait += repairStartedAt - brokenAt;
            count++;
        }
        avgOutage /= count;
        avgWait /= count;

        StringBuilder sb = new StringBuilder();
        sb.append("Outages report at time: ").append(timestamp).append("\n");
        if (count == 0) {
            sb.append("No resolved outages occurred");
        } else {
            sb.append("Outages occurred: ").append(count).append("\n");
            sb.append("Average outage duration: ").append(String.format("%.2f", avgOutage)).append("\n");
            sb.append("Average wait duration: ").append(String.format("%.2f",avgWait)).append("\n");
            sb.append("Min outage duration: ").append(min).append("\n");
            sb.append("Max outage duration: ").append(max).append("\n");
            sb.append("Outages:\n");
            outages.stream().sorted(Map.Entry.comparingByValue()).forEach(e -> sb.append(e.getKey()));
        }

        return sb;
    }
    protected String generatePath(Integer timestamp){
        return "reports/outages_report_" + timestamp + ".txt";
    }

}
