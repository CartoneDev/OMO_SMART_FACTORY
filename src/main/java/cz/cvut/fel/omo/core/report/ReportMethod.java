package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.ReportWriter;

import lombok.extern.slf4j.XSlf4j;
@XSlf4j(topic = "REPORT")
public abstract class ReportMethod {
    public void generateReport(SmartFactory factory, Integer timestamp){
        Integer actualTime = Math.min(Clock.getTime().getTicks() + 1, timestamp);
        log.info("Generating report at {}", actualTime);
        StringBuilder sb = prepareReport(factory, actualTime);
        String path = generatePath(actualTime);
        saveReport(sb, path);
    }
    protected abstract StringBuilder prepareReport(SmartFactory factory, Integer timestamp);
    protected void saveReport(StringBuilder sb, String path){
        ReportWriter.saveReport(sb, path);
    }

    protected String generatePath(Integer timestamp){
        return "reports/report_" + timestamp + ".txt";
    }
}
