package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.cvut.fel.omo.model.Material;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Config {

    // --- Images ---
    private static HashMap<String, Processor> processors = new HashMap<>();
    private static HashMap<String, Material> materials = new HashMap<>();
    private static HashMap<String, ProductionChain> blueprints = new HashMap<>();
    private static HashMap<String, Product> products = new HashMap<>();
    @Setter
    private static boolean fastConfig = false;

    private static JsonNode factoryConfig;
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
//        processors
        loadProcessors(jsonNode.get("processors"), mapper);

        loadProducts(jsonNode.get("products"), mapper);

//                materials
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


    }

    public static Material getMaterial(String s, int anInt) {
        return materials.get(s).toBuilder().amount(anInt).build();
    }

    public static Processor getProcessor(String s, int anInt) {
        Processor processor = processors.get(s);
        return processor.toBuilder().amount(anInt).build();
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
}
