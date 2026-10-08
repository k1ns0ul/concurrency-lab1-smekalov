package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class KitchenTest {
    @Test
    void doesNotGiveTheSamePortionToMultipleOficiants(){
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            Kitchen kitchen = new Kitchen(1000, new Semaphore(1));
            AtomicInteger delivered = new AtomicInteger();
            Thread[] o = new Thread[8];

            for(int i = 0; i < o.length; i++){
                o[i] = new Thread(() -> {
                    while(kitchen.getOneFood()){
                        delivered.incrementAndGet();
                    }
                });
                o[i].start();
            }

            for(int i = 0; i < o.length; i++){
                o[i].join();
            }

            assertEquals(1000, delivered.get());
            assertEquals(0, kitchen.getFoodCount());
        });
    }
}
