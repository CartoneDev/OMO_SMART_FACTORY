package cz.cvut.fel.omo.factorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Factory for creating smart factories
 */
@XSlf4j(topic = "FACTORY")
public abstract class SmartFactoryFactory {
    /**
     * Creates a smart factory from a config file with given settings
     * @param config config file
     * @return smart factory
     */
    public SmartFactory createSmartFactory(JsonNode config) {
        SmartFactory.reset();
        HashMap<String, ArrayList<Processor>> processorPool = instantiateProcessorPool(config);
        processorPool.put("repairman", populateRepairmen(config.has("repairman") ? (config.get("repairman").intValue()):1));
        ProcessorPool processorPoolInstance = new ProcessorPool(processorPool);
        ArrayList<ProductionChain> links = instantiateLinks(config, processorPoolInstance);


        return SmartFactory.setInstance(processorPoolInstance,
                links,
                config.has("name") ? config.get("name").asText() : "Regular factory");
    }

    /**
     * Instantiates a processor pool from a config file
     * @param config config file
     * @return processor pool
     */
    abstract HashMap<String, ArrayList<Processor>> instantiateProcessorPool(JsonNode config);

    ArrayList<Processor> populateRepairmen(int i) {
        ArrayList<Processor> repairmen = new ArrayList<>();
        for (int j =0; j < i; j++){
            repairmen.add( new ProcessorBuilder("worker").initState().amount(1).name("repairman").build() );
            repairmen.get(j).setId(j);
        }

        return repairmen;
    }

    /**
     * Instantiates production chains from a config file
     * @param config config file
     * @param processorPoolInstance processor pool
     * @return production chains
     */
    ArrayList<ProductionChain> instantiateLinks(JsonNode config, ProcessorPool processorPoolInstance) {
        ArrayList<ProductionChain> links = new ArrayList<>();

        if (!config.has("productionChains")) {
            log.info("No production chains defined in config");
            log.info("They can be added later see /help for details");
            return links;
        }
        JsonNode productionChains = config.get("productionChains");
        int productionChainsCount = productionChains.size();
        int successfullyConstructedProductionChains = 0;
        for (int i = 0; i < productionChainsCount; i++){

            JsonNode productionChain = productionChains.get(i);
            if (!productionChain.has("makes") || !productionChain.has("priority")) {
                log.error("Production chain has to have name of the product it _makes_ and priority specified");
                continue;
            }

            String name = productionChain.get("makes").asText();
            Integer priority = productionChain.get("priority").asInt();

            if (!Config.hasBlueprintFor(name)) {
                log.error("Blueprint for " + name + " is not defined in config!");
                continue;
            }

            ProductionChain prototype = Config.getBlueprintFor(name);
            ProductionChain productionChainInstance = new ProductionChain(prototype, priority);
            for (Processor processor : prototype.getProcessors()) {
                ArrayList<Processor> processors = processorPoolInstance.getProcessors(processor.getName(), processor.getAmount());
                if (processors == null) {
                    log.error("Not enough processors in pool for " + processor.getName());
                    log.error("Skipping {}-th production chain " + name, i);
                    productionChainInstance = null;
                    break;
                }
                productionChainInstance.addProcessors(new ArrayList<>(processors));
            }
            if (productionChainInstance == null) { continue; }
            productionChainInstance.setProduct(Config.getProduct(name, 0));
            productionChainInstance.setPriority(priority);
            productionChainInstance.setId(links.size());
            productionChainInstance.setName("PC[" +links.size()+"]" + name);
            productionChainInstance.registerProcessors();
            links.add(productionChainInstance);
            successfullyConstructedProductionChains++;
        }

        log.info("Successfully constructed " + successfullyConstructedProductionChains + " production chains from config.");

        return links;
    }
}
