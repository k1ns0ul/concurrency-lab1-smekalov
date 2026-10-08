package org.labs;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageTest {
    @Test
    void givesEachRequestItsOwnResponseAndClosesWaitingOficiants(){
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Stage stage = new Stage(new Semaphore(0), 3, 4, 2);
            Request first = new Request(0);
            Request fast = new Request(0);
            Request second = new Request(1);
            Request third = new Request(2);
            Request noFood = new Request(1);

            stage.putRequest(first);
            assertSame(first, stage.getRequest());
            assertTrue(first.isReserved());

            stage.putRequest(fast);
            stage.putRequest(second);
            stage.putRequest(third);

            assertSame(second, stage.getRequest());
            assertSame(third, stage.getRequest());
            assertSame(fast, stage.getRequest());
            assertTrue(fast.isReserved());

            stage.putRequest(noFood);
            assertSame(noFood, stage.getRequest());
            assertFalse(noFood.isReserved());

            first.sendResponse(true);
            noFood.sendResponse(false);

            assertTrue(first.getResponse());
            assertFalse(noFood.getResponse());

            stage.close();

            assertNull(stage.getRequest());
            assertNull(stage.getRequest());
        });
    }
}
