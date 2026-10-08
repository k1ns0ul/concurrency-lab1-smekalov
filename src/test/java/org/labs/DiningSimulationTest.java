package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiningSimulationTest {
    @Test
    void distributesAllPortionsAlmostEqually(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(15),
                () -> new DiningSimulation().run(7, 7001, 2)
        );

        assertEquals(7001, result.getEaten());
        assertEquals(7001, result.getDiscussions());
        assertEquals(6994, result.getDelivered());
        assertEquals(0, result.getRemaining());

        int extra = 0;
        for(int i = 0; i < 7; i++){
            int count = result.getEatenBy(i);
            assertTrue(count == 1000 || count == 1001);
            if(count == 1001){
                extra++;
            }
        }
        assertEquals(1, extra);
    }

    @Test
    void finishesWhenOnlyInitialPortionsExist(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(10),
                () -> new DiningSimulation().run(7, 7, 12)
        );

        assertEquals(7, result.getEaten());
        assertEquals(7, result.getDiscussions());
        assertEquals(0, result.getDelivered());
    }

    @Test
    void worksWithTwoProgrammersAndOneOficiant(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(10),
                () -> new DiningSimulation().run(2, 101, 1)
        );

        assertEquals(101, result.getEaten());
        assertEquals(101, result.getDiscussions());
        assertEquals(1, Math.abs(result.getEatenBy(0) - result.getEatenBy(1)));
        assertEquals(99, result.getDelivered());
        assertEquals(0, result.getRemaining());
    }

    @Test
    void rejectsInputsThatCannotGiveEveryoneInitialSoup(){
        DiningSimulation simulation = new DiningSimulation();

        assertThrows(IllegalArgumentException.class, () -> simulation.run(1, 10, 2));
        assertThrows(IllegalArgumentException.class, () -> simulation.run(7, 6, 2));
        assertThrows(IllegalArgumentException.class, () -> simulation.run(7, 10, 0));
    }
}
