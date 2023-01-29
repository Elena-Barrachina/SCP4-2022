package info.trekto.jos.core.impl.double_precision;

import info.trekto.jos.core.impl.single_precision.SimulationLogicFloat;

import java.util.concurrent.Semaphore;

public class ThreadSimulation extends Thread
{
    private final SimulationLogicDouble logic;
    private int startParticle;
    private int endParticle;
    private final QueueWork queue;

    public ThreadSimulation(SimulationLogicDouble logic, QueueWork queue) {
        this.logic = logic;
        this.startParticle = 0;
        this.endParticle = 0;
        this.queue = queue;
    }

    public void setStartParticle(int startParticle) {
        this.startParticle = startParticle;
    }

    public void setEndParticle(int endParticle) {
        this.endParticle = endParticle;
    }

    public void run(){
        while(queue.dynamicUpdate(this)>0){
            for(int i = startParticle; i < endParticle; i++) {
                runObject(i);
            }
        }
    }

    private void showPartialStats(){
        System.out.println("I'm some stats.\n");
    }

    private void runObject(int i){
        if (!logic.deleted[i]) {
            /* Speed is scalar, velocity is vector. Velocity = speed + direction. */

            /* Time T passed */

            /* Calculate acceleration */
            /* For the time T, forces accelerated the objects (changed their velocities).
             * Forces are calculated having the positions of the objects at the beginning of the period,
             * and these forces are applied for time T. */
            double newAccelerationX = 0;
            double newAccelerationY = 0;
            for (int j = 0; j < logic.readOnlyPositionX.length; j++) {
                if (i != j && !logic.readOnlyDeleted[j]) {
                    /* Calculate force */
                    double distance = SimulationLogicDouble.calculateDistance(logic.positionX[i], logic.positionY[i], logic.readOnlyPositionX[j], logic.readOnlyPositionY[j]);
                    double force = SimulationLogicDouble.calculateForce(logic.mass[i], logic.readOnlyMass[j], distance);
                    //       Fx = F*x/r;
                    double forceX = force * (logic.readOnlyPositionX[j] - logic.positionX[i]) / distance;
                    double forceY = force * (logic.readOnlyPositionY[j] - logic.positionY[i]) / distance;

                    /* Add to current acceleration */
                    // ax = Fx / m
                    newAccelerationX = newAccelerationX + forceX / logic.mass[i];
                    newAccelerationY = newAccelerationY + forceY / logic.mass[i];
                }
            }

            /* Move objects */
            /* For the time T, velocity moved the objects (changed their positions).
             * New objects positions are calculated having the velocity at the beginning of the period,
             * and these velocities are applied for time T. */
            logic.positionX[i] = logic.positionX[i] + logic.velocityX[i] * logic.secondsPerIteration;
            logic.positionY[i] = logic.positionY[i] + logic.velocityY[i] * logic.secondsPerIteration;

            /* Change velocity */
            /* For the time T, accelerations changed the velocities.
             * Velocities are calculated having the accelerations of the objects at the beginning of the period,
             * and these accelerations are applied for time T. */
            logic.velocityX[i] = logic.velocityX[i] + logic.accelerationX[i] * logic.secondsPerIteration;
            logic.velocityY[i] = logic.velocityY[i] + logic.accelerationY[i] * logic.secondsPerIteration;

            /* Change the acceleration */
            logic.accelerationX[i] = newAccelerationX;
            logic.accelerationY[i] = newAccelerationY;

            /* Bounce from screen borders */
            if (logic.screenWidth != 0 && logic.screenHeight != 0) {
                logic.bounceFromScreenBorders(i);
            }
        }
    }
}
