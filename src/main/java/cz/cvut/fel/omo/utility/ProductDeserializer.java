package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.Product;

public class ProductDeserializer extends StdDeserializer<Product> {

        public ProductDeserializer() {
            this(null);
        }

        public ProductDeserializer(Class<?> vc) {
            super(vc);
        }

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
