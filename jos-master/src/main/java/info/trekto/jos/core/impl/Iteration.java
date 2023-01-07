package info.trekto.jos.core.impl;

import info.trekto.jos.core.model.SimulationObject;

import java.util.List;

public class Iteration {
    private long cycle;
    private int numberOfObjects;
    private List<SimulationObject> objects;
    long runTime;

    public Iteration(long cycle, int numberOfObjects, List<SimulationObject> objects, long runTime) {
        this.cycle = cycle;
        this.numberOfObjects = numberOfObjects;
        this.objects = objects;
        this.runTime = runTime;
    }

    public long getCycle() {
        return cycle;
    }

    public void setCycle(long cycle) {
        this.cycle = cycle;
    }

    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    public void setNumberOfObjects(int numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
    }

    public List<SimulationObject> getObjects() {
        return objects;
    }

    public void setObjects(List<SimulationObject> objects) {
        this.objects = objects;
    }

    public long getRunTime() {
        return runTime;
    }
}
