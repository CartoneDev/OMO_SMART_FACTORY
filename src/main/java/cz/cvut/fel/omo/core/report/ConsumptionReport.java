package cz.cvut.fel.omo.core.report;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.summator.MaterialSummator;
import cz.cvut.fel.omo.utility.ProductionChainStateIterator;

import java.util.ArrayList;

/**
 * Report for factory consumption
 */
public class ConsumptionReport extends ReportMethod {
    @Override
    public StringBuilder prepareReport(SmartFactory factory, Integer timestamp) {
        StringBuilder sb = new StringBuilder();
        sb.append(factory.getName()).append(" consumption report at time: ").append(timestamp).append("\n");
        MaterialSummator totalSummator = new MaterialSummator();

        StringBuilder sbLinks = new StringBuilder();
        for (ProductionChain link : factory.getLinks()) {
            sbLinks.append(" - ").append("Production line #").append(link.getId());

            ProductionChainStateIterator iterator = new ProductionChainStateIterator(link, timestamp);
            MaterialSummator localSummator = new MaterialSummator();
            StringBuilder linkTotalConsumptionHistory = iterateLink(iterator, localSummator);
            sbLinks.append(" - ").append("Total consumption: \n    - ").append(
                    String.join("\n    - ", localSummator.toString().split(";"))
            ).append("\n");
            sbLinks.append(linkTotalConsumptionHistory);
            totalSummator.add(localSummator);
        }
        sb.append("Total consumption: \n ").append(
                String.join("\n", totalSummator.toString().split(";"))
        ).append("\n");
        sb.append("\nConcretely: \n");
        sb.append(sbLinks);

        return sb;
    }

    private StringBuilder iterateLink(ProductionChainStateIterator iterator, MaterialSummator totalSummator) {
        StringBuilder sbLink = new StringBuilder();
        while (iterator.hasNext()) {
            ProductionChain chain = iterator.next();
            ArrayList<Event> events = chain.getWaybackMachine().getEvents();
            Integer epochStart = events.get(0).getTimestamp().getTicks();
            Integer epochEnd = events.get(events.size() - 1).getTimestamp().getTicks() + 1;
            Integer totalDuration = epochEnd - epochStart;
            Integer productionDuration = Math.toIntExact(events.stream().filter(e -> e.getType() == EventType.PRODUCT_PRODUCED).count());
            Integer idleDuration = totalDuration - productionDuration;

            sbLink.append("    - Epoch: ").append(epochStart).append(" - ").append(epochEnd)
                    .append(" producing ").append(chain.getProduct().getName()).append("\n");
            sbLink.append("    - Total duration: ").append(totalDuration)
                    .append(" Production duration: ").append(productionDuration).append("\n");

            MaterialSummator summator = new MaterialSummator();
            for (Processor processor : chain.getProcessors()){
                MaterialSummator processorSummator = new MaterialSummator();
                processorSummator.add(processor.getCost(), idleDuration * getProcessorIdleCost(processor.getType()));
                processorSummator.add(processor.getCost(), productionDuration);
                sbLink.append("        - ").append(processor.getName()).append(": ").append("\n            -  ").append(
                        String.join("\n            - ", processorSummator.toString().split(";"))
                ).append("\n");
                summator.add(processorSummator);
            }
            summator.add(chain.getProduct().getCostPH(), productionDuration);
            sbLink.append("        - ").append("Total with production cost included\n            -  ").append(
                    String.join("\n            - ", summator.toString().split(";"))
            ).append("\n");
            totalSummator.add(summator);
        }
        return sbLink;
    }

    private Double getProcessorIdleCost(String type) {
        switch (type){
            case "machine" -> {
                return 0.1;
            }
            case "robot" -> {
                return 0.7;
            }
            default -> {
                return 1.0;
            }
        }
    }

    public String generatePath(Integer timestamp){
        return "reports/consumption_report_" + timestamp + ".txt";
    }
}
