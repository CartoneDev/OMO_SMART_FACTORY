package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.omo.model.CostPH;

import java.io.IOException;

public class  CostDeserializer {

    public static CostPH deserialize(JsonNode node, JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        CostPH cost = new CostPH();

        node.fieldNames().forEachRemaining(s -> {
            cost.addCost(Config.getMaterial(s, node.get(s).asInt()));
        });
        return cost;
    }
}
