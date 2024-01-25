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

/**
 * This factory allocates specified amount of processors and build production chains, feeling them with processors by their priority.
 */
@XSlf4j(topic = "FACTORY")
public class RegularSmartFactoryFactory extends SmartFactoryFactory{
    /**
     * Instatiates a processor pool from a config file
     * @param config config file
     * @return processor pool map
     */
    protected HashMap<String, ArrayList<Processor>> instantiateProcessorPool(JsonNode config) {
        Integer successfullyLoadedProcessors = 0;
        JsonNode processors = config.get("processors");
        Integer processorsCount = processors.size();
        HashMap<String, ArrayList<Processor>> processorPool = new HashMap<>();
        for (JsonNode processor : processors) {
            if (!processor.has("name") || !processor.has("amount")) {
                log.error("Processor has to have name and amount specified");
                continue;
            }
            String name = processor.get("name").asText();
            Integer processorCount = processor.get("amount").asInt();
            if (!Config.hasProcessor(name)) {
                log.error("Processor " + name + " is not defined in config");
                continue;
            }
            ArrayList<Processor> processorList = new ArrayList<>();

            for (int i = 0; i < processorCount; i++) {
                Processor p = Config.getProcessor(name, 1);
                p.setId(i);
                processorList.add(p);
            }

            processorPool.put(name, processorList);
            successfullyLoadedProcessors++;
        }

        log.info("Successfully loaded " + successfullyLoadedProcessors + " different processors from config.");
        return processorPool;
    }

}
