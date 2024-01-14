package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.Material;

import java.io.IOException;

public class MaterialDeserializer extends StdDeserializer<Material> {
    public MaterialDeserializer () {
        this(null);
    }

    public MaterialDeserializer (Class<?> vc) {
        super(vc);
    }

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
