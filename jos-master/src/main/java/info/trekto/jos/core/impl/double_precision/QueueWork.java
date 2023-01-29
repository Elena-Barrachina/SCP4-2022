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
    //TODO: Create specific instance in caller class

    public void updateQueueWork(QueueWork queue){
        lock.lock();
        try {
            // Update particles in queue
            // TODO:Create queue
            queue.startParticle = 0;
            // TODO: What is nShared here? We need the num. of particles
            queue.endParticle = nShared;
        } catch (Exception e){
            // TODO: Define exception
        } finally {
            lock.unlock();
        }
    }
    //2. Update Queue Work
    //3. Thread Simulation
    //    Param: QueueWork
    //4. Dynamic Update
    /*int dynamicUpdate(struct ParticleParameters *Params){

        pthread_mutex_lock(&queue.mutex);

        Params->startParticle = queue.startParticle;
        int end = queue.startParticle + queue.endParticle/20;

        if(end > queue.endParticle) { end = queue.endParticle; }

        Params->endParticle = end;
        queue.startParticle = Params->endParticle;

        //thread.set(startparticle);
        //thread.set(endparticle);

        pthread_mutex_unlock(&queue.mutex);
        return Params -> endParticle - Params -> startParticle;
    }*/
}
