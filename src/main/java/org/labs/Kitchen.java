package org.labs;

import java.util.concurrent.Semaphore;

class Kitchen {
    private int f;
    private final Semaphore kitSem;

    Kitchen(int f, Semaphore kitSem){
        this.f = f;
        this.kitSem = kitSem;
    }

    boolean getOneFood(){
        kitSem.acquireUninterruptibly();
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

    int getFoodCount(){
        kitSem.acquireUninterruptibly();
        try {
            return f;
        } finally {
            kitSem.release();
        }
    }
}
