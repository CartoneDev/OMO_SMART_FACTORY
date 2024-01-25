package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.decay.ProcessorDecayModel;
import cz.cvut.fel.omo.core.decay.RandomDecayModel;
import cz.cvut.fel.omo.factorial.NoPreprocessorsFactoryFactory;
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

    /**
     * Loads the configuration from the given path.
     *
     * @param path path to the configuration file
     * @throws FileNotFoundException if the file is not found
     * @throws JsonProcessingException if the file is not in JSON format
     */
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
        loadFactory(jsonNode.get("factory"));
    }

    /**
     * Loads processors from the given JSON node.
     * @param jsonNode
     * @param objectMapper
     * @throws JsonProcessingException
     */
    public static void loadProcessors(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Processor[] processors = objectMapper.treeToValue(jsonNode, Processor[].class);
        for (Processor processor : processors) {
            Config.processors.put(processor.getName(), processor);
        }

    }

    /**
     * Loads materials from the given JSON node.
     * @param jsonNode
     * @param objectMapper
     * @throws JsonProcessingException
     */
    public static void loadMaterials(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Material[] materials = objectMapper.treeToValue(jsonNode, Material[].class);
        for (Material material : materials) {
            Config.materials.put(material.getName(), material);
            Config.materials.get(material.getName()).unitize();
        }
    }

    /**
     * Loads products from the given JSON node.
     * @param jsonNode
     * @param objectMapper
     * @throws JsonProcessingException
     */
    public static void loadProducts(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Product[] products = objectMapper.treeToValue(jsonNode, Product[].class);
        for (Product product : products) {
            Config.products.put(product.getName(), product);
        }

    }

    /**
     * Loads blueprints from the given JSON node.
     * @param jsonNode
     * @param objectMapper
     * @throws JsonProcessingException
     */
    public static void loadBlueprints(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException  {
        ProductionChain[] productionChains = objectMapper.treeToValue(jsonNode, ProductionChain[].class);
        for (ProductionChain productionChain : productionChains) {
            Config.blueprints.put(productionChain.getProduct().getName(), productionChain);
        }

    }

    /**
     * Loads factory setting from the given JSON node.
     *
     * @param jsonNode
     * @throws JsonProcessingException
     */
    public static void loadFactory(JsonNode jsonNode) {
        factoryConfig = jsonNode;
        if (jsonNode.has("currency")){
            currency = jsonNode.get("currency").asText();
        } else {
            currency = "shtuck";
        }
    }

    /**
     * Returns loaded material with the given name.
     * @param name
     * @param amount
     * @return material
     */
    public static Material getMaterial(String name, int amount) {
        return materials.get(name).toBuilder().amount(amount).build();
    }

    /**
     * Returns loaded processor with the given name.
     * @param name
     * @param amount
     * @return processor
     */
    public static Processor getProcessor(String name, int amount) {
        Processor processor = processors.get(name);
        return processor.toBuilder().amount(amount).build();
    }

    /**
     * Returns loaded product with the given name.
     * @param productName
     * @param amount
     * @return product
     */
    public static Product getProduct(String productName, int amount) {
        Product product = products.get(productName);
        return product.toBuilder().amount(amount).build();
    }

    /**
     * Clears all the loaded data.
     */
    public static void clear() {
        processors.clear();
        materials.clear();
        blueprints.clear();
        products.clear();
    }

    /**
     * Instantiates factory singleton based on configuration provided.
     * @return factory configuration
     */
    public static SmartFactory buildFactory() {
        SmartFactoryFactory factoryFactory = (fastConfig ? new NoPreprocessorsFactoryFactory() : new RegularSmartFactoryFactory());
        return factoryFactory.createSmartFactory(factoryConfig);
    }

    /**
     * Returns true if there is a processor in config with the given name.
     * @param name
     * @return true if there is a processor in config with the given name, false otherwise
     */
    public static boolean hasProcessor(String name) {
        return processors.containsKey(name);
    }

    /**
     * Returns true if there is a production chain in config for product with the given name.
     * @param name
     * @return true if there is a production chain in config for product with the given name, false otherwise
     */
    public static boolean hasBlueprintFor(String name) {
        return blueprints.containsKey(name);
    }

    /**
     * Returns blueprint for product with the given name.
     * @param product
     * @return blueprint for product with the given name
     */
    public static ProductionChain getBlueprintFor(String product) {
        return blueprints.get(product);
    }

    /**
     * Returns all the products loaded from config.
     */
    public static ArrayList<Product> getProducts() {
        return new ArrayList<>(products.values());
    }
}
