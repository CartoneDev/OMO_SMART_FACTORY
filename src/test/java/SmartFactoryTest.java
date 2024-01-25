import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.model.processor.states.*;
import cz.cvut.fel.omo.utility.Config;
import cz.cvut.fel.omo.utility.ProductionChainStateIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;


public class SmartFactoryTest {

    private SmartFactory sf;
    @BeforeEach
    public void setUp() {
        try {
            Config.loadConfig("src/main/resources/example.config.json");
            sf = Config.buildFactory();
        } catch (FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    /**
     * Tests if config loads correctly
     */
    @Test
    public void testSmartFactoryConfigures() {
        assert (sf.getLinks().size() == 2);
        Assertions.assertNotEquals(sf.getName(), "Empty Factory");
    }

    /**
     * Tests if main state machine flow works
     */
    @Test
    public void testStateMachine() {
        Processor p = sf.getProcessorPool().getProcessors("alliance worker", 1).get(0);
        ProductionChain pc = sf.getLinks().get(0);
        assert (p.getState() instanceof Initial);
        p.addEvent(new Event(EventType.PROCESSOR_ASSIGNED, pc, pc));
        assert (p.getState() instanceof Waiting);
        p.addEvent(new Event(EventType.PROCESSOR_STARTED, pc, pc));
        assert (p.getState() instanceof Processing);
        p.addEvent(new Event(EventType.PROCESSOR_BROKEN, pc, pc));
        assert (p.getState() instanceof Broken);
        p.addEvent(new Event(EventType.PROCESSOR_START_REPAIR, pc, pc));
        assert (p.getState() instanceof BeingRepaired);
        p.addEvent(new Event(EventType.PROCESSOR_REPAIRED, pc, pc));
        assert (p.getState() instanceof Processing);
    }

    /**
     * Tests if chain reassembly works and production chain state iterator distinguishes between epochs
     */
    @Test
    public void productionChainReassemblyIteratorTest(){
        tickN(5);
        ProductionChain pc = sf.getLinks().get(0);
        Product productBefore = pc.getProduct();
        ArrayList<Product> products = Config.getProducts();

        for (Product product : products) {
            if (!product.getName().equals(productBefore.getName())) {
                pc.rebuildTo(product);
                return;
            }
        }

        tickN(5);
        Product productAfter = pc.getProduct();

        ProductionChainStateIterator iterator = new ProductionChainStateIterator(pc, 10);
        assert (iterator.hasNext());
        ProductionChain pc1 = iterator.next();
        Assertions.assertEquals(pc1.getProduct().getName(), productBefore.getName());
        Assertions.assertNotEquals(pc1.getProduct().getName(), productAfter.getName());
        assert (iterator.hasNext());
        ProductionChain pc2 = iterator.next();
        Assertions.assertEquals(pc2.getProduct().getName(), productAfter.getName());
        assert (!iterator.hasNext());
    }

    @Test
    public void basicFactoryFunctionalityTest(){
        tickN(5);
        ProductionChain pc = sf.getLinks().get(0);
        pc = sf.getLinks().get(0);
        Event prodEvent = pc.getWaybackMachine().getEvents().stream().filter(e -> e.getType() == EventType.PRODUCT_PRODUCED).findFirst().get();
        assert (prodEvent != null);
        Assertions.assertEquals(((Product)prodEvent.getPayload()).getName(), pc.getProduct().getName());
        tickN(500);
        ProductionChain finalPc = pc;
        Event brokenEvent = sf.getMaintenanceEvents().stream()
                .filter(event -> (event.getType() == EventType.PROCESSOR_BROKEN) && (Objects.equals(((Processor) event.getPayload()).getProductionChain().getId(), finalPc.getId()))).findFirst().get();
        assert (brokenEvent != null);
        ArrayList<Event> procEvents = ((Processor) brokenEvent.getPayload()).getWaybackMachine().getEvents();
        assert (procEvents.stream().anyMatch(e -> e.getType() == EventType.PROCESSOR_BROKEN));
        assert (procEvents.stream().anyMatch(e -> e.getType() == EventType.PROCESSOR_REPAIRED));
        assert (procEvents.stream().anyMatch(e -> e.getType() == EventType.PROCESSOR_STARTED));
        assert (procEvents.stream().anyMatch(e -> e.getType() == EventType.PROCESSOR_START_REPAIR));
    }
    private void tickN(int n){
        for (int i = 0; i < n; i++) {
            Clock.getTimer().tick();
            sf.tick();
        }
    }
}
