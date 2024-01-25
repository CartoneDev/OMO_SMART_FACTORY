package cz.cvut.fel.omo.core.visitor;

/**
 * Visitable interface for visitor pattern
 */
public interface Visitable {
    void accept(Visitor visitor);
}
