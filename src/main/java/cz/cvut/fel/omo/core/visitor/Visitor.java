package cz.cvut.fel.omo.core.visitor;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.HumanResource;
import cz.cvut.fel.omo.model.processor.Machine;
import cz.cvut.fel.omo.model.processor.RoboticResource;

import java.awt.*;

/**
 * Visitor interface for visitor pattern
 */
public interface Visitor {
    void visit(SmartFactory smartFactory);
    void visit(ProductionChain productionChain);

    void visit(HumanResource processor);
    void visit(Machine processor);

    void visit(RoboticResource processor);
}
