package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.processor.*;
import cz.cvut.fel.omo.utility.ProcessorBuilder;

import java.io.IOException;
import java.util.Optional;

/**
 * Utility class
 * Deserializer for Processor
 */
public class ProcessorDeserializer extends StdDeserializer<Processor> {

    /**
     * Empty constructor need by Jackson
     */
    public ProcessorDeserializer() {
        this(null);
    }

    /**
     * Constructor also needed by Jackson
     * @param vc Class
     */
    public ProcessorDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Deserializes the Processor from the given JsonNode
     * @param jp JsonParser
     * @param ctxt DeserializationContext
     * @return deserialized Processor
     * throws IOException
     */
    @Override
    public Processor deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String type = node.get("type").asText();
        Double damage = node.has("damage") ? node.get("damage").asDouble() : 0.0; // max 1.0


        return new ProcessorBuilder(type)
                .name(node.get("name").asText())
                .cost(CostDeserializer.deserialize(node.get("costPH"), jp, ctxt))
                .damage(damage)
                .amount(Optional.ofNullable(node.get("amount"))
                        .map(JsonNode::asInt)
                        .orElse(1))
                .initState()
                .build();

    }
}
