package cz.cvut.fel.omo.core.visitor;

public interface Visitable {
    void accept(Visitor visitor);
}
