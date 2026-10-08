package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class KitchenTest {
    @Test
    void doesNotGiveTheSamePortionToMultipleOficiants(){
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            Kitchen kitchen = new Kitchen(1000, new Semaphore(1));
            AtomicInteger delivered = new AtomicInteger();
            AtomicReference<InterruptedException> failure = new AtomicReference<>();
            Thread[] o = new Thread[8];

            for(int i = 0; i < o.length; i++){
                o[i] = new Thread(() -> {
                    try {
                        while(kitchen.getOneFood()){
                            delivered.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        failure.set(e);
                        Thread.currentThread().interrupt();
                    }
                });
                o[i].start();
            }

            for(int i = 0; i < o.length; i++){
                o[i].join();
            }

            assertEquals(1000, delivered.get());
            assertEquals(0, kitchen.getFoodCount());
            assertNull(failure.get());
        });
    }
}
