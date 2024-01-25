package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.utility.Config;

import java.io.IOException;

/**
 * Utility class
 * Deserializer for CostPH which is bundled material cost per production hour
 */
public class  CostDeserializer {

    /**
     * Deserializes the CostPH from the given JsonNode
     * @param node JsonNode to be deserialized
     * @param jsonParser JsonParser
     * @param deserializationContext DeserializationContext
     * @return deserialized CostPH
     * @throws IOException
     * @throws JacksonException
     */
    public static CostPH deserialize(JsonNode node, JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        CostPH cost = new CostPH();

        node.fieldNames().forEachRemaining(s -> {
            cost.addCost(Config.getMaterial(s, node.get(s).asInt()));
        });
        return cost;
    }
}
