package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.cvut.fel.omo.model.Material;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;

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
        mapper.registerModule(module);
        JsonNode jsonNode = mapper.readTree(config);


        loadMaterials(jsonNode.get("materials"), mapper);
//        processors
        loadProcessors(jsonNode.get("processors"), mapper);
//                materials
//        blueprints
        loadBlueprints(jsonNode.get("blueprints"));
//                factory
        loadFactory(jsonNode.get("factory"));
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
    public static void loadBlueprints(JsonNode jsonNode) {

    }
    public static void loadFactory(JsonNode jsonNode) {

    }

    public static Material getMaterial(String s, int anInt) {
        return materials.get(s).toBuilder().amount(anInt).build();
    }

    public static Processor getProcessor(String s, int anInt) {
        Processor processor = processors.get(s);
        processor.setAmount(anInt);
        return processor;
    }
}
