package cz.cvut.fel.omo.model.processor;


import cz.cvut.fel.omo.core.visitor.Visitor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoboticResource extends Processor{
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
