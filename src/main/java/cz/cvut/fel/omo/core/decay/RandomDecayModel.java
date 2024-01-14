package cz.cvut.fel.omo.core.decay;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.AllArgsConstructor;

import java.util.Random;


@AllArgsConstructor
public class RandomDecayModel implements ProcessorDecayModel{
    Random random = new Random();
    Double minDamageTick = 0.0;
    Double maxDamageTick = 0.5;
    @Override
    public void decay(Processor processor) {
        Double damage = random.nextDouble() * (maxDamageTick - minDamageTick) + minDamageTick;
        processor.dealDamage(damage);
        if (processor.getDamage() > 0.7) {
            Double brokenDamage = random.nextDouble() * (1 - processor.getDamage()) + processor.getDamage();
            processor.dealDamage(brokenDamage - processor.getDamage());
        } else
        if (processor.getDamage() > 0.3) {
            if (random.nextDouble() < 0.5) {
                processor.dealDamage(damage);
            }
        }
    }

}
