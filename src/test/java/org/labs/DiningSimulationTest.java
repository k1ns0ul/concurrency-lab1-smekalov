package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiningSimulationTest {
    @Test
    void consumesAllPortionsThroughSharedKitchen(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(15),
                () -> new DiningSimulation().run(7, 7001, 2)
        );

        assertEquals(7001, result.getEaten());
        assertEquals(7001, result.getDiscussions());
        assertEquals(6994, result.getDelivered());
        assertEquals(0, result.getRemaining());

        int sum = 0;
        for(int i = 0; i < 7; i++){
            int count = result.getEatenBy(i);
            assertTrue(count >= 1);
            sum += count;
        }
        assertEquals(7001, sum);
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
        for(int i = 0; i < 7; i++){
            assertEquals(1, result.getEatenBy(i));
        }
    }

    @Test
    void worksWithTwoProgrammersAndOneOficiant(){
        SimulationResult result = assertTimeoutPreemptively(
                Duration.ofSeconds(10),
                () -> new DiningSimulation().run(2, 101, 1)
        );

        assertEquals(101, result.getEaten());
        assertEquals(101, result.getDiscussions());
        assertTrue(result.getEatenBy(0) >= 1);
        assertTrue(result.getEatenBy(1) >= 1);
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

    @Test
    void stopsWorkersWhenSimulationIsInterrupted() throws InterruptedException {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread simulation = new Thread(() -> {
            Thread.currentThread().interrupt();
            try {
                new DiningSimulation().run(7, 1000000, 2);
            } catch (InterruptedException e) {
                failure.set(e);
            }
        });
        simulation.setDaemon(true);
        simulation.start();
        simulation.join(5000);

        assertFalse(simulation.isAlive());
        assertInstanceOf(InterruptedException.class, failure.get());
    }
}
