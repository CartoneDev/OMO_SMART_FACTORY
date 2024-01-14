package cz.cvut.fel.omo.utility.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.utility.Config;

public class ProductionChainDeserializer extends StdDeserializer<ProductionChain> {

        public ProductionChainDeserializer() {
            this(null);
        }

        public ProductionChainDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ProductionChain deserialize(JsonParser jp, DeserializationContext ctxt) throws java.io.IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            ProductionChain productionChain = new ProductionChain();
            String productName = node.get("name").asText();
            Integer amount = node.get("amount").asInt();
            productionChain.setProduct(Config.getProduct(productName, amount));

            JsonNode processors = node.get("processors");
            for (int i = 0; i < processors.size(); i++) {
                String processorName = processors.get(i).get("name").asText();
                Integer processorAmount = processors.get(i).get("amount").asInt();
                productionChain.addProcessor(Config.getProcessor(processorName, processorAmount));
            }
            return productionChain;
        }
}
