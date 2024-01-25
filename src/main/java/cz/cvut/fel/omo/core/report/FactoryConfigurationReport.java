package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.ReportWriter;
import lombok.extern.slf4j.XSlf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Report for factory configuration
 */
@XSlf4j(topic = "REPORT")
public class FactoryConfigurationReport extends ReportMethod {
    /**
     * Prepares specific report for given factory at given timestamp
     * @param factory factory to generate report for
     * @param timestamp timestamp of the report
     * @return report inside StringBuilder
     */
    @Override
    public StringBuilder prepareReport(SmartFactory factory, Integer timestamp) {
        log.info("Generating factory configuration report at {}", timestamp);
        StringBuilder sb = new StringBuilder();
        sb.append(factory.getName()).append(" configuration at time: ").append(timestamp).append("\n");
        ArrayList<ProductionChain> links = factory.getLinks();
        sb.append("Factory has ").append(links.size()).append(" production lines\n");
        for (ProductionChain link : links) {
            ProductionChain linkOnTime = link.getStateAt(timestamp);
            sb.append(" - ").append("Production line #").append(linkOnTime.getId()).append(" has ").append(linkOnTime.getProcessors().size()).append(" processors:\n");
            LinkedList<Processor> processors = linkOnTime.getProcessors();
            processors.forEach(processor -> {
                sb.append(" - - ").append("#").append(processor.getStatusAt(timestamp)).append("\n");
            });
            sb.append(" - - ").append("Product: ").append(linkOnTime.getProduct().getName()).append("\n");
        }

        return sb;
    }

    /**
     * Generates path for report
     * @param timestamp timestamp of the report
     * @return path for report
     */
    @Override
    protected String generatePath(Integer timestamp) {
        return "reports/factory_configuration_report_" + timestamp + ".txt";
    }
}
