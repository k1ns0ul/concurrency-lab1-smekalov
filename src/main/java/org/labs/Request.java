package org.labs;

import java.util.concurrent.Semaphore;

class Request {
    private final Semaphore res = new Semaphore(0, true);
    private boolean isFoodAvailable;

    void sendResponse(boolean res){
        isFoodAvailable = res;
        this.res.release();
    }

    boolean getResponse() throws InterruptedException {
        res.acquire();
        return isFoodAvailable;
    }
}
