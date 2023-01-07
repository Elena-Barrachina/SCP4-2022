package info.trekto.jos.core.impl.single_precision;

import info.trekto.jos.core.impl.arbitrary_precision.SimulationLogicAP;
import info.trekto.jos.core.model.ImmutableSimulationObject;
import info.trekto.jos.core.model.SimulationObject;
import info.trekto.jos.core.model.impl.TripleNumber;
import info.trekto.jos.core.numbers.Number;

import java.util.List;

public class ThreadSimulation extends Thread
{
    private final int startParticle;
    private final int endParticle;
    private final SimulationLogicFloat logic;

    public ThreadSimulation(SimulationLogicFloat logic, int startParticle, int endParticle) {
        this.logic = logic;
        this.startParticle = startParticle;
        this.endParticle = endParticle;
    }

    public void run(){
        for(int i = startParticle; i < endParticle; i++) {
            runObject(i);
        }
    }

    private void runObject(int i){
        if (!logic.deleted[i]) {
            /* Speed is scalar, velocity is vector. Velocity = speed + direction. */

            /* Time T passed */

            /* Calculate acceleration */
            /* For the time T, forces accelerated the objects (changed their velocities).
             * Forces are calculated having the positions of the objects at the beginning of the period,
             * and these forces are applied for time T. */
            float newAccelerationX = 0;
            float newAccelerationY = 0;
            for (int j = 0; j < logic.readOnlyPositionX.length; j++) {
                if (i != j && !logic.readOnlyDeleted[j]) {
                    /* Calculate force */
                    float distance = SimulationLogicFloat.calculateDistance(logic.positionX[i], logic.positionY[i], logic.readOnlyPositionX[j], logic.readOnlyPositionY[j]);
                    float force = SimulationLogicFloat.calculateForce(logic.mass[i], logic.readOnlyMass[j], distance);
                    //       Fx = F*x/r;
                    float forceX = force * (logic.readOnlyPositionX[j] - logic.positionX[i]) / distance;
                    float forceY = force * (logic.readOnlyPositionY[j] - logic.positionY[i]) / distance;

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
