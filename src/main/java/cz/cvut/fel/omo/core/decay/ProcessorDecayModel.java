package cz.cvut.fel.omo.core.decay;

import cz.cvut.fel.omo.model.processor.Processor;

/**
 * Generic interface for decay models
 * Which are used to wear down processors over time
 */
public interface ProcessorDecayModel {
    void decay(Processor processor);
}
