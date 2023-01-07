package info.trekto.jos.core.impl.arbitrary_precision;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static info.trekto.jos.core.Controller.C;

/**
 * @author Trayan Momkov
 * 2017-Aug-5 21:22:53
 */
class SimulationRecursiveAction {

    public static int threshold = 20;
    private final int fromIndex;
    private final int toIndex;
    private final SimulationAP simulation;

    public SimulationRecursiveAction(int fromIndex, int toIndex, SimulationAP simulation) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.simulation = simulation;
    }

    public void compute() {
        simulation.getSimulationLogic().calculateNewValues(fromIndex, toIndex);
    }

}
