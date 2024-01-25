package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.visitor.Visitor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Class representing a machine processor
 */
@NoArgsConstructor
public class Machine extends Processor{
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
