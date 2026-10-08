package org.labs;

import java.util.concurrent.Semaphore;

class Request {
    private final Semaphore res = new Semaphore(0);
    private boolean isFoodAvailable;

    void sendResponse(boolean res){
        isFoodAvailable = res;
        this.res.release();
    }

    boolean getResponse(){
        res.acquireUninterruptibly();
        return isFoodAvailable;
    }
}
