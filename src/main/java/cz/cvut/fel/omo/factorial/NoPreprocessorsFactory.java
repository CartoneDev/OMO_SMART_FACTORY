package cz.cvut.fel.omo.factorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.omo.core.SmartFactory;




/**
    This factory creates all links and allocates processors pools to be sufficient for the production.
 */
public class NoPreprocessorsFactory implements SmartFactoryFactory{
    @Override
    public SmartFactory createSmartFactory(JsonNode config) {
        return null;
    }
}
