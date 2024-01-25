package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.decay.ProcessorDecayModel;
import cz.cvut.fel.omo.core.decay.RandomDecayModel;
import cz.cvut.fel.omo.factorial.NoPreprocessorsFactory;
import cz.cvut.fel.omo.factorial.RegularSmartFactoryFactory;
import cz.cvut.fel.omo.factorial.SmartFactoryFactory;
import cz.cvut.fel.omo.model.Material;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.deserializers.MaterialDeserializer;
import cz.cvut.fel.omo.utility.deserializers.ProcessorDeserializer;
import cz.cvut.fel.omo.utility.deserializers.ProductDeserializer;
import cz.cvut.fel.omo.utility.deserializers.ProductionChainDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


/**
 * Utility static class responsible for loading and storing all the configuration data.
 */
public class Config {

    // --- Images ---
    private static HashMap<String, Processor> processors = new HashMap<>();
    private static HashMap<String, Material> materials = new HashMap<>();
    private static HashMap<String, ProductionChain> blueprints = new HashMap<>();
    private static HashMap<String, Product> products = new HashMap<>();
    @Setter
    private static boolean fastConfig = false;

    @Getter
    private static String currency = null;
    private static JsonNode factoryConfig;
    @Getter
    private static ProcessorDecayModel decayModel = new RandomDecayModel(new Random(), 0.0001, 0.005);
    // ---

    public static void loadConfig(String path) throws FileNotFoundException, JsonProcessingException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        String config = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Processor.class, new ProcessorDeserializer());
        module.addDeserializer(Material.class, new MaterialDeserializer());
        module.addDeserializer(Product.class, new ProductDeserializer());
        module.addDeserializer(ProductionChain.class, new ProductionChainDeserializer());
        mapper.registerModule(module);
        JsonNode jsonNode = mapper.readTree(config);


        loadMaterials(jsonNode.get("materials"), mapper);

        loadProcessors(jsonNode.get("processors"), mapper);

        loadProducts(jsonNode.get("products"), mapper);

//        blueprints
        loadBlueprints(jsonNode.get("blueprints"), mapper);
//                factory
        loadFactory(jsonNode.get("factory"), mapper);
    }
    public static void loadProcessors(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Processor[] processors = objectMapper.treeToValue(jsonNode, Processor[].class);
        for (Processor processor : processors) {
            Config.processors.put(processor.getName(), processor);
        }

    }
    public static void loadMaterials(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Material[] materials = objectMapper.treeToValue(jsonNode, Material[].class);
        for (Material material : materials) {
            Config.materials.put(material.getName(), material);
            Config.materials.get(material.getName()).unitize();
        }
    }

    public static void loadProducts(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Product[] products = objectMapper.treeToValue(jsonNode, Product[].class);
        for (Product product : products) {
            Config.products.put(product.getName(), product);
        }

    }
    public static void loadBlueprints(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException  {
        ProductionChain[] productionChains = objectMapper.treeToValue(jsonNode, ProductionChain[].class);
        for (ProductionChain productionChain : productionChains) {
            Config.blueprints.put(productionChain.getProduct().getName(), productionChain);
        }

    }
    public static void loadFactory(JsonNode jsonNode, ObjectMapper mapper) {
        factoryConfig = jsonNode;
        if (jsonNode.has("currency")){
            currency = jsonNode.get("currency").asText();
        } else {
            currency = "shtuck";
        }
    }

    public static Material getMaterial(String s, int amount) {
        return materials.get(s).toBuilder().amount(amount).build();
    }

    public static Processor getProcessor(String s, int amount) {
        Processor processor = processors.get(s);
        return processor.toBuilder().amount(amount).build();
}

    public static Product getProduct(String productName, int amount) {
        Product product = products.get(productName);
        return product.toBuilder().amount(amount).build();
    }

    public static void clear() {
        processors.clear();
        materials.clear();
        blueprints.clear();
        products.clear();
    }

    public static SmartFactory buildFactory() {
        SmartFactoryFactory factoryFactory = (fastConfig ? new NoPreprocessorsFactory() : new RegularSmartFactoryFactory());
        return factoryFactory.createSmartFactory(factoryConfig);
    }

    public static boolean hasProcessor(String name) {
        return processors.containsKey(name);
    }

    public static boolean hasBlueprintFor(String name) {
        return blueprints.containsKey(name);
    }

    public static ProductionChain getBlueprintFor(String product) {
        return blueprints.get(product);
    }

    public static ArrayList<Product> getProducts() {
        return new ArrayList<>(products.values());
    }
}
