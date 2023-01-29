package info.trekto.jos.core.impl.arbitrary_precision;

import info.trekto.jos.core.model.SimulationObject;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueWork {
    private List<SimulationObject> oldObjects;
    private List<SimulationObject> newObjects;
    private int processed;
    Lock lock;

    public QueueWork(){
        this.oldObjects = null;
        this.newObjects = null;
        this.processed = 0;
        this.lock = new ReentrantLock();
    }

    public void updateQueueWork(List<SimulationObject> oldObjects, List<SimulationObject> newObjects){
        lock.lock();
        this.oldObjects = oldObjects;
        this.newObjects = newObjects;
        this.processed = 0;
        lock.unlock();
    }
    int dynamicUpdate(ThreadSimulation threadSimulation){
        lock.lock();

        int start = processed;
        int end = start + (this.newObjects.size()/20);
        if(end > newObjects.size()){
            end = newObjects.size();
        }
        threadSimulation.setOldObjects(this.oldObjects.subList(start, end));
        threadSimulation.setNewObjects(this.newObjects.subList(start, end));

        processed = end;

        lock.unlock();

        return end - start;
    }
}
