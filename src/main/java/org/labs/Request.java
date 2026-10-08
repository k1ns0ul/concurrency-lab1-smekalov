package org.labs;

import java.util.concurrent.Semaphore;

class Request {
    private final Semaphore res = new Semaphore(0);
    private final int num;
    private boolean reserved;
    private boolean isFoodAvailable;

    Request(int num){
        this.num = num;
    }

    int getNum(){
        return num;
    }

    void setReserved(boolean reserved){
        this.reserved = reserved;
    }

    boolean isReserved(){
        return reserved;
    }

    void sendResponse(boolean res){
        isFoodAvailable = res;
        this.res.release();
    }

    boolean getResponse(){
        res.acquireUninterruptibly();
        return isFoodAvailable;
    }
}
