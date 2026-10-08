package org.labs;

import java.util.concurrent.Semaphore;

class Spoon {
    private final int num;
    private final Semaphore semaphore = new Semaphore(1, true);

    Spoon(int num){
        this.num = num;
    }

    int getNum(){
        return num;
    }

    void take(){
        semaphore.acquireUninterruptibly();
    }

    void put(){
        semaphore.release();
    }
}
