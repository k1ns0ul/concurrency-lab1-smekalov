package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class DiningSimulationTest {
    @Test
    void distributesAllPortionsAlmostEqually(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(15),
                () -> new DiningSimulation().run(7, 7001, 2)
        );

        assertEquals(7001, result.getEaten());
        assertEquals(6994, result.getDiscussions());
        assertEquals(6994, result.getDelivered());
        assertEquals(0, result.getRemaining());
        assertEquals(1001, result.getEatenBy(0));

        for(int i = 1; i < 7; i++){
            assertEquals(1000, result.getEatenBy(i));
        }

    }

    @Test
    void finishesWhenOnlyInitialPortionsExist(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(10),
                () -> new DiningSimulation().run(7, 7, 12)
        );

        assertEquals(7, result.getEaten());
        assertEquals(0, result.getDiscussions());
        assertEquals(0, result.getDelivered());
    }

    @Test
    void worksWithTwoProgrammersAndOneOficiant(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(10),
                () -> new DiningSimulation().run(2, 101, 1)
        );

        assertEquals(51, result.getEatenBy(0));
        assertEquals(50, result.getEatenBy(1));
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
