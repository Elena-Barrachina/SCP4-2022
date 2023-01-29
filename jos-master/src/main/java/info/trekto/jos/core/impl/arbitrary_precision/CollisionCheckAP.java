package info.trekto.jos.core.impl.arbitrary_precision;

import info.trekto.jos.core.Simulation;
import info.trekto.jos.core.model.SimulationObject;
import info.trekto.jos.core.numbers.Number;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

//import static info.trekto.jos.core.impl.arbitrary_precision.SimulationRecursiveAction.threshold;

class CollisionCheckAP {
    private final int fromIndex;
    private final int toIndex;
    private final Simulation simulation;

    public CollisionCheckAP(int fromIndex, int toIndex, Simulation simulation) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.simulation = simulation;
    }

    public void checkAllCollisions()
    {
        if (simulation.isCollisionExists()) {
            //countCollisions++;
            //return countCollisions;
            return;
        }

        for (SimulationObject object : simulation.getAuxiliaryObjects().subList(fromIndex, toIndex)) {
            if (simulation.isCollisionExists()) {
                break;
            }
            for (SimulationObject object1 : simulation.getAuxiliaryObjects()) {
                if (object == object1) {
                    continue;
                }
                // distance between centres
                Number distance = simulation.calculateDistance(object, object1);

                if (distance.compareTo(object.getRadius().add(object1.getRadius())) <= 0) {
                    simulation.upCollisionExists();
                    break;
                }
            }
        }
    }

}
