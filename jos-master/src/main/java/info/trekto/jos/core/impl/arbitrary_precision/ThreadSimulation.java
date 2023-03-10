package info.trekto.jos.core.impl.arbitrary_precision;

import info.trekto.jos.core.model.ImmutableSimulationObject;
import info.trekto.jos.core.model.SimulationObject;
import info.trekto.jos.core.model.impl.TripleNumber;
import info.trekto.jos.core.numbers.Number;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSimulation extends Thread
{
    private SimulationLogicAP logicAP;
    private List<SimulationObject> oldObjects;
    private List<SimulationObject> newObjects;
    private QueueWork queue;
    boolean search;
    Semaphore semProgress;
    Semaphore semIter;
    Semaphore semPartials;
    Semaphore semGlobals;
    private Lock lock;
    private Condition condGlobals;

    public int mergedParticles = 0;
    public int processedParticles = 0;
    int imbalance = 0;
    int threadId;
    long startTime = System.currentTimeMillis();
    long computeTime = 0;


    public ThreadSimulation(int threadId, SimulationLogicAP logicAP, QueueWork queue, Semaphore semProgress, Semaphore semIter, Semaphore semPartials, Semaphore semGlobals, Lock lock, Condition conGlobals) {
        this.logicAP = logicAP;
        this.oldObjects = null;
        this.newObjects = null;
        this.queue = queue;
        this.search = true;
        this.semPartials = semPartials;
        this.semProgress = semProgress;
        this.semIter = semIter;
        this.lock = lock;
        this.condGlobals = conGlobals;
        this.threadId = threadId;
    }

    public void setOldObjects(List<SimulationObject> oldObjects) {
        this.oldObjects = oldObjects;
    }

    public void setNewObjects(List<SimulationObject> newObjects) {
        this.newObjects = newObjects;
        for (int i = 0; i < newObjects.size(); i++){
            newObjects.get(i).setThreadId(threadId);
        }
    }

    public void setSearch(boolean search){ this.search = search; }

    public void run(){
        int iter = 0;
        try {
            semProgress.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(search){
            if(queue.dynamicUpdate(this)>0){
                for(int i = 0; i < oldObjects.size(); i++) {
                    runObject(oldObjects.get(i), newObjects.get(i));
                    processedParticles += 1;
                }
            } else {
                semIter.release();
                iter++;
                if(iter%logicAP.numberMStats == 0){ showStats(); }

                try {
                    semProgress.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(iter%logicAP.numberMStats != 0){ showStats(); }
    }

    private void runObject(ImmutableSimulationObject oldObject, SimulationObject newObject){
        /* Speed is scalar, velocity is vector. Velocity = speed + direction. */

        /* Time T passed */

        /* Calculate acceleration */
        /* For the time T, forces accelerated the objects (changed their velocities).
         * Forces are calculated having the positions of the objects at the beginning of the period,
         * and these forces are applied for time T. */
        TripleNumber acceleration = new TripleNumber();
        for (ImmutableSimulationObject tempObject : logicAP.simulation.getObjects()) {
            if (tempObject == oldObject) {
                continue;
            }
            /* Calculate force */
            Number distance = logicAP.calculateDistance(oldObject, tempObject);
            TripleNumber force = logicAP.simulation.getForceCalculator().calculateForceAsVector(oldObject, tempObject, distance);

            /* Add to current acceleration */
            acceleration = logicAP.calculateAcceleration(oldObject, acceleration, force);
        }

        /* Move objects */
        /* For the time T, velocity moved the objects (changed their positions).
         * New objects positions are calculated having the velocity at the beginning of the period,
         * and these velocities are applied for time T. */
        logicAP.moveObject(newObject);

        /* Change velocity */
        /* For the time T, accelerations changed the velocities.
         * Velocities are calculated having the accelerations of the objects at the beginning of the period,
         * and these accelerations are applied for time T. */
        newObject.setVelocity(logicAP.calculateVelocity(oldObject));

        /* Change the acceleration */
        newObject.setAcceleration(acceleration);

        /* Bounce from screen borders */
        /* Only change the direction of the velocity */
        if (logicAP.simulation.getProperties().isBounceFromScreenBorders()) {
            logicAP.bounceFromScreenBorders(newObject);
        }
    }


    void showStats(){
        printStats(this);
        // Wait till all threads have finished printing partial stats to show global stats
        lock.lock();
        // Wait for global stats to be printed
        try {
            semPartials.release();
            condGlobals.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        lock.unlock();
    }
    static synchronized void printStats(ThreadSimulation threadSimulation){
        threadSimulation.computeTime = System.currentTimeMillis() - threadSimulation.startTime;
        int average = threadSimulation.logicAP.globalProcessedParticles();
        threadSimulation.imbalance = (average - threadSimulation.processedParticles)/ threadSimulation.processedParticles;
        // (f-i)/i
        System.out.println("--- PARTIAL STATISTICS OF THREAD " + threadSimulation.threadId + " ---");
        System.out.println("Compute Time: " + threadSimulation.computeTime);
        System.out.println("Load Imbalance %: " + threadSimulation.imbalance + " %");
        System.out.println("Processed Particles: " + threadSimulation.processedParticles);
        System.out.println("Merged Particles: " + threadSimulation.mergedParticles);
        System.out.println("");
    };
}
