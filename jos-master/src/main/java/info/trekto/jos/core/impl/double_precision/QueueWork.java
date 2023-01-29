package info.trekto.jos.core.impl.double_precision;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueWork {
    int startParticle;
    int endParticle;
    Lock lock;

    public QueueWork(int startParticle, int endParticle){
        this.startParticle = startParticle;
        this.endParticle = endParticle;
        this.lock = new ReentrantLock();
    }

    public void updateQueueWork(int objectsLeft){
        lock.lock();
        this.startParticle = 0;
        this.endParticle = objectsLeft;
        lock.unlock();
    }
    int dynamicUpdate(ThreadSimulation threadSimulation){
        lock.lock();

        int start = this.startParticle;
        threadSimulation.setStartParticle(start);
        int end = start + (this.endParticle/20);

        if(end > this.endParticle){
            end = this.endParticle;
        }
        threadSimulation.setEndParticle(end);
        this.startParticle = end;

        lock.unlock();

        return end - start;
    }
}
