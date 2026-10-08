package org.labs;

import java.util.concurrent.Semaphore;

class Kitchen {
    private int f;
    private final Semaphore kitSem;

    Kitchen(int f, Semaphore kitSem){
        this.f = f;
        this.kitSem = kitSem;
    }

    boolean getOneFood() throws InterruptedException {
        kitSem.acquire();
        try {
            if(f > 0){
                f--;
                return true;
            }

            return false;
        } finally {
            kitSem.release();
        }
    }

    int getFoodCount() throws InterruptedException {
        kitSem.acquire();
        try {
            return f;
        } finally {
            kitSem.release();
        }
    }
}
