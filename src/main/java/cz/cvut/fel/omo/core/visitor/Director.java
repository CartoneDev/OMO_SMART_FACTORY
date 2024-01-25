package cz.cvut.fel.omo.core.visitor;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.HumanResource;
import cz.cvut.fel.omo.model.processor.Machine;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.model.processor.RoboticResource;
import lombok.extern.slf4j.XSlf4j;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Visitor implementation for director
 * The class is a visitor that visits all processors in the factory and performs random actions
 * may miraculously cure burned out workers
 */
@XSlf4j(topic = "DIRECTOR")
public class Director implements Visitor{
    /**
     * Visits the factory
     * @param smartFactory factory to visit
     */
    @Override
    public void visit(SmartFactory smartFactory) {
        log.info("Director is visiting factory " + smartFactory.getName());
        smartFactory.getLinks().stream()
                .sorted(Comparator.comparing(ProductionChain::getPriority))
                .toList().forEach(productionChain -> productionChain.accept(this));
    }

    /**
     * Visits the production chain
     * @param productionChain production chain to visit
     */
    @Override
    public void visit(ProductionChain productionChain) {
        log.info("Director is visiting production chain " + productionChain.getName());
        productionChain.getProcessors().forEach(processor -> processor.accept(this));
    }

    /**
     * Visits the human resource and performs random action, may miraculously cure burned out workers
     * @param humanResource worker to visit
     */
    @Override
    public void visit(HumanResource humanResource) {
        String[] actionPool = {"is screaming on", "is talking to", "is yelling at", "is laughing at", "is crying on", "is singing to", "is sighing loudly while looking at"};
        String action = actionPool[(int) (Math.random() * actionPool.length)];
        if (Math.random()>0.5){
            Double damage = (Math.random()-0.5) * 0.1 / 8;
            humanResource.setDamage(Math.max(humanResource.getDamage() - damage, 0));
        }
        log.info("Director {} " + humanResource.toString().toLowerCase().split("assigned")[0], action);
    }

    /**
     * Visits the machine and performs random action
     * @param processor machine to visit
     */
    @Override
    public void visit(Machine processor) {
        String[] actionPool = {"is looking at", "stares on blinking lights on", "nods mindfully"};
        String action = actionPool[(int) (Math.random() * actionPool.length)];
        log.info("Director {} " + processor.toString().toLowerCase().split("assigned")[0], action);
    }

    /**
     * Visits the robotic resource and performs random action
     * @param processor robotic resource to visit
     */
    @Override
    public void visit(RoboticResource processor) {
        String[] actionPool = {"admires the view of", "follows each movement of", "gazes on a little scratch on"};
        String action = actionPool[(int) (Math.random() * actionPool.length)];
        log.info("Director {} " + processor.toString().toLowerCase().split("assigned")[0], action);
    }
}
