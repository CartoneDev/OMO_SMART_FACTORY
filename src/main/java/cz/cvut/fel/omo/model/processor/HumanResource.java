package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.visitor.Visitor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HumanResource extends Processor{

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
