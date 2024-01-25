package cz.cvut.fel.omo.model.processor;


import cz.cvut.fel.omo.core.visitor.Visitor;
import lombok.NoArgsConstructor;

/**
 * Class representing a robotic resource
 */
@NoArgsConstructor
public class RoboticResource extends Processor{
    /**
     * Accepts a visitor
     * @param visitor
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
