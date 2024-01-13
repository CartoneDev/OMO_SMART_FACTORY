package cz.cvut.fel.omo.factorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.omo.core.SmartFactory;

/**
 * This factory allocates specified amount of processors and build production chains, feeling them with processors by their priority.
 */
public class RegularSmartFactoryFactory implements SmartFactoryFactory{
    @Override
    public SmartFactory createSmartFactory(JsonNode config, ObjectMapper mapper) {
        return null;
    }
}
