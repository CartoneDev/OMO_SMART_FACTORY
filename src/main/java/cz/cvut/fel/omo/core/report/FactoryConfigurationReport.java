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

@XSlf4j(topic = "REPORT")
public class FactoryConfigurationReport implements ReportStrategy {
    @Override
    public void generateReport(SmartFactory factory, Integer timestamp) {
        log.info("Generating factory configuration report at {}", timestamp);
        StringBuilder sb = new StringBuilder();
        sb.append(factory.getName()).append(" configuration at time: ").append(timestamp).append("\n");
        ArrayList<ProductionChain> links = factory.getLinks();
        sb.append("Factory has ").append(links.size()).append(" production lines\n");
        for (ProductionChain link : links) {
            sb.append(" - ").append("Production line #").append(link.getId()).append(" has ").append(link.getProcessors().size()).append(" processors:\n");
            LinkedList<Processor> processors = link.getProcessors();
            processors.forEach(processor -> {
                sb.append(" - - ").append("#").append(processor.getStatusAt(timestamp)).append("\n");
            });
            sb.append(" - - ").append("Product: ").append(link.getProduct().getName()).append("\n");
        }

        String path = "reports/factory_configuration_report_" + timestamp + ".txt";
        ReportWriter.saveReport(sb, path);
    }
}
