package cz.cvut.fel.omo.core.visitor;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.HumanResource;
import cz.cvut.fel.omo.model.processor.Machine;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.model.processor.RoboticResource;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

import lombok.extern.slf4j.XSlf4j;
@XSlf4j(topic = "INSPECTOR")
public class InspectorGadget implements Visitor{

    @Override
    public void visit(SmartFactory smartFactory) {
        smartFactory.getLinks().stream()
                .sorted(Comparator.comparing(productionChain -> productionChain.getProcessors().stream().max(Comparator.comparing(Processor::getDamage)).get().getDamage()))
                .forEach(productionChain -> productionChain.accept(this));
    }

    @Override
    public void visit(ProductionChain productionChain) {
        productionChain.getProcessors().stream().sorted(Comparator.comparing(Processor::getDamage)).forEach(processor -> processor.accept(this));
    }

    @Override
    public void visit(HumanResource processor) {
        String[] actionPool = {"evaluates", "inspects", "checks", "examines", "scrutinizes", "surveys", "views", "analyzes"};
        log.info("Inspector Gadget {} " + processor.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }

    @Override
    public void visit(Machine processor) {
        String[] actionPool = {"monitors", "observes", "watches", "tracks", "follows", "keeps an eye on", "keeps tabs on", "keeps under surveillance"};
        if (Math.random()>0.8){
            Double damage = (Math.random()-0.8) * 0.1 / 8;
            processor.setDamage(Math.max(processor.getDamage() + damage, 0));
        }
        log.info("Inspector Gadget {} " + processor.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }

    @Override
    public void visit(RoboticResource processor) {
        String[] actionPool = {"staring", "gazing", "checks", "monitors", "checks configuration of", "checks status of", "checks integrity of", "checks performance of"};

        log.info("Inspector Gadget {} " + processor.toString().toLowerCase().split("assigned")[0], Arrays.stream(actionPool).min((a, b) -> Math.random() > 0.5 ? 1 : -1).get());
    }


}
