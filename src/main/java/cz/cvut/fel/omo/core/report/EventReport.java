package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;

import java.util.Comparator;
import java.util.stream.Stream;

public class EventReport implements ReportStrategy{
    @Override
    public void generateReport(SmartFactory factory, Integer timestamp) {

        Stream.iterate(new Event(EventType.EMPTY, 3, null), e -> new Event(EventType.PROCESSOR_BROKEN, 3, null))
                .filter(e -> e.getType() == EventType.EMPTY).sorted( EventReport::compare );
    }

    private static int compare(Event lhs, Event rhs) {
        int ordinal = lhs.getType().ordinal() - rhs.getType().ordinal();
        if (ordinal != 0) {
            return ordinal;
        }

        if (lhs != null && rhs != null) {
            lhs.getSource().getReportDescriptor().compareTo(rhs.getSource().getReportDescriptor());
        } if (lhs != null) {
            return 1;
        } else if (rhs != null) {
            return -1;
        } else {
            if (lhs.getSolver() != null && rhs.getSolver() != null) {
                lhs.getSolver().toString().compareTo(rhs.getSolver().toString());
            } if (lhs.getSolver() != null) {
                return 1;
            } else if (rhs.getSolver() != null) {
                return -1;
            } else {
                return 0;
            }
        }

    }

}
