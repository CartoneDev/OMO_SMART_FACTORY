package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;

public interface ReportStrategy {
    void generateReport(SmartFactory factory, Integer timestamp);
}
