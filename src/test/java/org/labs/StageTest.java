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
            Stage stage = new Stage(new Semaphore(0), 2);
            Request first = new Request();
            Request second = new Request();

            stage.putRequest(first);
            stage.putRequest(second);

            assertSame(first, stage.getRequest());
            assertSame(second, stage.getRequest());

            second.sendResponse(false);
            first.sendResponse(true);

            assertTrue(first.getResponse());
            assertFalse(second.getResponse());

            stage.close();

            assertNull(stage.getRequest());
            assertNull(stage.getRequest());
        });
    }
}
