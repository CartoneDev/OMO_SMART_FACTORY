package cz.cvut.fel.omo.model.processor;

public class HumanResource extends Processor{

    private ProcessorState state;
    public void tick() {
        state.tick();
    }
}
