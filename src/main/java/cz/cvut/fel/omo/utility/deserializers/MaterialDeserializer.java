package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.Material;

import java.io.IOException;

/**
 * Utility class
 * Deserializer for Material
 */
public class MaterialDeserializer extends StdDeserializer<Material> {

    /**
     * Empty constructor need by Jackson
     */
    public MaterialDeserializer () {
        this(null);
    }

    /**
     * Constructor also needed by Jackson
     * @param vc Class
     */
    public MaterialDeserializer (Class<?> vc) {
        super(vc);
    }

    /**
     * Deserializes the Material from the given JsonNode
     * Throws IOException if the node is not in the correct format
     * Throws JacksonException if the node is not in the correct format
     * @param jsonParser JsonParser
     * @param deserializationContext DeserializationContext
     * @return deserialized Material
     */
    @Override
    public Material deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return Material.builder()
                .name(node.get("name").asText())
                .amount(node.get("amount").asInt())
                .type(node.get("type").asText())
                .value(node.get("value").decimalValue())
                .build();
    }

}
