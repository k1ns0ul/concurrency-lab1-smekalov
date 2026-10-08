package org.labs;

import java.util.concurrent.Semaphore;

public class DiningSimulation {
    public SimulationResult run(int n, int f, int w) throws InterruptedException {
        if(n < 2 || f < n || w < 1){
            throw new IllegalArgumentException("Expected n >= 2, f >= n and w >= 1");
        }

        Spoon[] spoon = new Spoon[n];
        for(int i = 0; i < spoon.length; i++){
            spoon[i] = new Spoon(i);
        }

        Semaphore kitSem = new Semaphore(1, true);
        Kitchen kitchen = new Kitchen(f - n, kitSem);
        Semaphore room = new Semaphore(n - 1, true);
        Semaphore stageReq = new Semaphore(0, true);

        Thread[] p = new Thread[n];
        Thread[] o = new Thread[w];
        Thread[] workers = new Thread[n + w];
        Stage stage = new Stage(stageReq, w, workers);
        Programmist[] programmists = new Programmist[n];
        Oficiant[] oficiants = new Oficiant[w];

        for(int i = 0; i < o.length; i++){
            oficiants[i] = new Oficiant(kitchen, stage);
            o[i] = new Thread(oficiants[i], "oficiant-" + i);
            workers[i] = o[i];
        }

        for(int i = 0; i < p.length; i++){
            Spoon spoonLeft = spoon[i];
            Spoon spoonRight = spoon[(i + 1) % n];
            programmists[i] = new Programmist(spoonLeft, spoonRight, stage, room);
            p[i] = new Thread(programmists[i], "programmist-" + i);
            workers[w + i] = p[i];
        }

        for(int i = 0; i < o.length; i++){
            o[i].start();
        }

        for(int i = 0; i < p.length; i++){
            p[i].start();
        }

        try {
            for(int i = 0; i < p.length; i++){
                p[i].join();
            }

            if(stage.getFailure() != null){
                throw stage.getFailure();
            }

            stage.close();

            for(int i = 0; i < o.length; i++){
                o[i].join();
            }

            if(stage.getFailure() != null){
                throw stage.getFailure();
            }
        } catch (InterruptedException e) {
            stage.cancel(e);
            for(Thread worker : workers){
                if(worker != null){
                    while(worker.isAlive()){
                        try {
                            worker.join();
                        } catch (InterruptedException ignored) {
                            stage.cancel(e);
                        }
                    }
                }
            }
            Thread.currentThread().interrupt();
            throw e;
        }

        int[] eatenBy = new int[n];
        int eaten = 0;
        int discussions = 0;
        int delivered = 0;

        for(int i = 0; i < programmists.length; i++){
            eatenBy[i] = programmists[i].getEatenCount();
            eaten += eatenBy[i];
            discussions += programmists[i].getDiscussionCount();
        }

        for(int i = 0; i < oficiants.length; i++){
            delivered += oficiants[i].getDeliveredCount();
        }

        return new SimulationResult(eaten, discussions, delivered, kitchen.getFoodCount(), eatenBy);
    }
}
