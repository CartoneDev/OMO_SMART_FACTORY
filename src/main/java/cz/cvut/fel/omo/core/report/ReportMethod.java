package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.utility.ReportWriter;

import lombok.extern.slf4j.XSlf4j;

/**
 *  Template method for generating reports
 *  All reports are to be located in /reports directory
 */
@XSlf4j(topic = "REPORT")
public abstract class ReportMethod {
    /**
     * Generates specific report at given timestamp
     * @param factory factory to generate report for
     * @param timestamp timestamp of the report
     */
    public void generateReport(SmartFactory factory, Integer timestamp){
        Integer actualTime = Math.min(Clock.getTime().getTicks() + 1, timestamp);
        log.info("Generating report at {}", actualTime);
        StringBuilder sb = prepareReport(factory, actualTime);
        String path = generatePath(actualTime);
        saveReport(sb, path);
    }

    /**
     * Prepares specific report for given factory at given timestamp
     * @param factory factory to generate report for
     * @param timestamp timestamp of the report
     * @return report inside StringBuilder
     */
    protected abstract StringBuilder prepareReport(SmartFactory factory, Integer timestamp);

    /**
     * Saves report to given path
     * @param sb report
     * @param path path to save report to
     */
    protected void saveReport(StringBuilder sb, String path){
        ReportWriter.saveReport(sb, path);
    }

    /**
     * Generates path for report
     * @param timestamp timestamp of the report
     * @return path for report
     */
    protected String generatePath(Integer timestamp){
        return "reports/report_" + timestamp + ".txt";
    }
}
