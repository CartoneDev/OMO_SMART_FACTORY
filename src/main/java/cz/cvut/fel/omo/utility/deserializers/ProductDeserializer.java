package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.Product;

/**
 * Utility class
 * Deserializer for Product
 */
public class ProductDeserializer extends StdDeserializer<Product> {
    /**
     * Empty constructor need by Jackson
     */
    public ProductDeserializer() {
        this(null);
    }

    /**
     * Constructor also needed by Jackson
     * @param vc
     */
    public ProductDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Deserializes the Product from the given JsonNode
     * @param jp JsonParser
     * @param ctxt DeserializationContext
     * @return deserialized Product
     * @throws java.io.IOException
     */
    @Override
    public Product deserialize(com.fasterxml.jackson.core.JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String type = node.get("type").asText();
        return Product.builder()
                .name(node.get("name").asText())
                .costPH(CostDeserializer.deserialize(node.get("costPH"), jp, ctxt))
                .amount(0)
                .description(node.get("description").asText())
                .type(type)
                .build();
    }
}
