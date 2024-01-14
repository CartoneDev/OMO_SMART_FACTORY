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
@XSlf4j(topic = "ABSTRACT_FACTORY")
public class RegularSmartFactoryFactory implements SmartFactoryFactory{
    @Override
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

    private ArrayList<Processor> populateRepairmen(int i) {
        ArrayList<Processor> repairman = new ArrayList<>();
        for (int j =0; j < i; j++){
            repairman.add( new ProcessorBuilder("worker").amount(1).name("repairman").build() );
            repairman.get(j).setId(j);
        }
        return repairman;
    }

    private ArrayList<ProductionChain> instantiateLinks(JsonNode config, ProcessorPool processorPoolInstance) {
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
                    processorPoolInstance.returnProcessors(productionChainInstance.getProcessors().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
                    productionChainInstance = null;
                    break;
                }
                productionChainInstance.addProcessors(processors);
            }
            if (productionChainInstance == null) { continue; }
            productionChainInstance.setProduct(Config.getProduct(name, 0));
            productionChainInstance.setPriority(priority);
            productionChainInstance.setId(links.size());
            productionChainInstance.setName("PC[" +links.size()+"]" + name);
            links.add(productionChainInstance);
            successfullyConstructedProductionChains++;
        }

        log.info("Successfully constructed " + successfullyConstructedProductionChains + " production chains from config.");

        return links;
    }

    private HashMap<String, ArrayList<Processor>> instantiateProcessorPool(JsonNode config) {
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
