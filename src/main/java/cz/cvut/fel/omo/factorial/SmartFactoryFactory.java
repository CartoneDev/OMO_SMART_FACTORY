package cz.cvut.fel.omo.factorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.omo.core.SmartFactory;

public interface SmartFactoryFactory {
    public SmartFactory createSmartFactory(JsonNode config);
}
