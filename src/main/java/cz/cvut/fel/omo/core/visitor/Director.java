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

@XSlf4j(topic = "DIRECTOR")
public class Director implements Visitor{
    @Override
    public void visit(SmartFactory smartFactory) {
        log.info("Director is visiting factory " + smartFactory.getName());
        smartFactory.getLinks().stream()
                .sorted(Comparator.comparing(ProductionChain::getPriority))
                .toList().forEach(productionChain -> productionChain.accept(this));
    }

    @Override
    public void visit(ProductionChain productionChain) {
        log.info("Director is visiting production chain " + productionChain.getName());
        productionChain.getProcessors().forEach(processor -> processor.accept(this));
    }

    @Override
    public void visit(HumanResource humanResource) {
        String[] actionPool = {"is screaming on", "is talking to", "is yelling at", "is laughing at", "is crying on", "is singing to", "is sighing loudly while looking at"};
        if (Math.random()>0.5){
            Double damage = (Math.random()-0.5) * 0.1 / 8;
            humanResource.setDamage(Math.max(humanResource.getDamage() - damage, 0));
        }
        log.info("Director {} " + humanResource.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }

    @Override
    public void visit(Machine processor) {
        String[] actionPool = {"is looking at", "stares on blinking lights on", "nods mindfully"};
        log.info("Director {} " + processor.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }

    @Override
    public void visit(RoboticResource processor) {
        String[] actionPool = {"admires the view of", "follows each movement of", "gazes on a little scratch on"};
        log.info("Director {} " + processor.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }
}
