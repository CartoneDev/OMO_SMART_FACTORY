package cz.cvut.fel.omo.factorial;

import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.omo.core.ProcessorPool;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.Config;
import cz.cvut.fel.omo.utility.ProcessorBuilder;
import lombok.extern.slf4j.XSlf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
    This factory creates all links and allocates processors pools to be sufficient for the production.
 */
@XSlf4j(topic = "FACTORY")
public class NoPreprocessorsFactoryFactory extends SmartFactoryFactory{
    /**
     * Instantiates production chains from a config file
     * matches the blueprint requirements with the processor pool
     * @param config config file
     * @return processor pool map
     */

    protected HashMap<String, ArrayList<Processor>> instantiateProcessorPool(JsonNode config) {

        JsonNode productionChains = config.get("productionChains");

        HashMap<String, ArrayList<Processor>> processorPool = new HashMap<>();
        int id = 0;
        ArrayList<Processor> totalProcessors = new ArrayList<>();
        for (JsonNode processor : productionChains) {
            if (!processor.has("makes") || !processor.has("priority")) {
                log.error("Production chain has to have name and amount specified");
                continue;
            }

            String name = processor.get("makes").asText();
            if (!Config.hasBlueprintFor(name)) {
                log.error("Blueprint for " + name + " is not defined in config");
                continue;
            }
            ProductionChain prototype = Config.getBlueprintFor(name);
            ArrayList<Processor> processorList = new ArrayList<>();
            for (Processor p : prototype.getProcessors()) {
                for (int i = 0; i < p.getAmount(); i++) {
                    Processor processorInstance = Config.getProcessor(p.getName(), 1);
                    processorInstance.setId(id++);
                    processorList.add(processorInstance);
                }
            }

//            processorList.stream().
            totalProcessors.addAll(processorList);
        }
        totalProcessors.stream().collect(Collectors.groupingBy(Processor::getName)).forEach((k,v)->processorPool.put(k, new ArrayList<Processor>(v)));

        log.info("Loaded processor pool to match requirements of production chains");
        return processorPool;
    }

}
