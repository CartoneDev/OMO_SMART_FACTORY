package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.*;
import cz.cvut.fel.omo.core.visitor.EventCollectingVisitor;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j (topic = "REPORT")
public class EventReport extends ReportMethod{

    @Override
    protected StringBuilder prepareReport(SmartFactory factory, Integer timestamp) {
        EventCollectingVisitor visitor = new EventCollectingVisitor();
        factory.accept(visitor);

        StringBuilder sb = new StringBuilder();
        sb.append(factory.getName()).append(" event report at time: ").append(timestamp).append("\n");
        visitor.getEvents().stream().filter(e -> e.getTimestamp().getTicks() < timestamp)
                .sorted(EventReport::compare)
                .forEach(e -> sb.append(generateEventDescriptor(e, timestamp)).append("\n"));

        return sb;
    }

    private char[] generateEventDescriptor(Event e, Integer timestamp) {
        int maxWidth = String.valueOf(timestamp).length();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%" + maxWidth + "d", e.getTimestamp().getTicks())).append("h ");
        sb.append(e.getType()).append(" ");
        String t = ((EventSource)((Timed) e.getSource()).onTime(e.getTimestamp().getTicks())).getReportDescriptor();
        sb.append(t);

        if (e.getPayload() != null) {
            sb.append(" ").append(formatPayload(e.getPayload(), e));
        }

        if (e.getSolver() != null) {
            sb.append(" ").append(formatSolver(e.getSolver(),e));
        }

        return sb.toString().toCharArray();
    }

    private char[] formatSolver(Object solver, Event event) {
//        log.debug("Solver simple name: {}", solver.getClass().getSimpleName());
        if (solver.getClass().getSimpleName().equals(event.getSource().getClass().getSimpleName())) {
            return "".toCharArray();
        }
        if (solver instanceof Event){
            return ((Event) solver).getSource().getReportDescriptor().toCharArray();
        }

        return ("Solved by " + solver.toString()).toCharArray();
    }

    private char[] formatPayload(Object payload, Event event) {
//        log.debug("Payload simple name: {}", payload.getClass().getSimpleName());
        if (payload.getClass().getSimpleName().equals(event.getSource().getClass().getSimpleName())) {
            return "".toCharArray();
        }
        if (payload instanceof PriorityEvent){
            return ((EventSource)(((Timed)((PriorityEvent) payload).getPayload())
                    .onTime(event.getTimestamp().getTicks())))
                    .getReportDescriptor().toCharArray();
        }
        if (payload instanceof Processor){
            return ((Processor)((Timed) payload).onTime(event.getTimestamp().getTicks())).getReportDescriptor().toCharArray();
        }
        return ("With payload " + payload.toString()).toCharArray();
    }

    private static int compare(Event lhs, Event rhs) {
        int cmp = lhs.getType().ordinal() - rhs.getType().ordinal();
        if (cmp != 0) {
            return cmp;
        }
        cmp = lhs.getSource().getReportDescriptor().compareTo(rhs.getSource().getReportDescriptor());
        if (cmp != 0) {
            return cmp;
        }
        if (lhs.getSolver() != null && rhs.getSolver() != null) {
            cmp = lhs.getSolver().toString().compareTo(rhs.getSolver().toString());
            return cmp;
        } else if (lhs.getSolver() != null) {
            return -1;
        } else if (rhs.getSolver() != null) {
            return 1;
        }
        return 0;
    }

    protected String generatePath(Integer timestamp) {
        return "reports/event_report_" + timestamp + ".txt";
    }
}
