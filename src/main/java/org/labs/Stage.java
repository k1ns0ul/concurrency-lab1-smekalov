package org.labs;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class Stage {
    private final Semaphore req;
    private final Semaphore access = new Semaphore(1, true);
    private final Queue<Request> requests = new ArrayDeque<>();
    private final int[] served;
    private final int w;
    private int availableFood;
    private boolean closed;

    Stage(Semaphore req, int n, int availableFood, int w){
        this.req = req;
        this.served = new int[n];
        this.availableFood = availableFood;
        this.w = w;

        for(int i = 0; i < served.length; i++){
            served[i] = 1;
        }
    }

    void putRequest(Request request){
        access.acquireUninterruptibly();
        try {
            if(closed){
                throw new IllegalStateException("Stage is closed");
            }

            requests.add(request);
            req.release();
        } finally {
            access.release();
        }
    }

    Request getRequest(){
        while(true){
            access.acquireUninterruptibly();
            try {
                if(closed && requests.isEmpty()){
                    return null;
                }

                if(!requests.isEmpty()){
                    if(availableFood == 0){
                        Request request = requests.poll();
                        if(!requests.isEmpty()){
                            req.release();
                        }
                        return request;
                    }

                    int min = Integer.MAX_VALUE;
                    for(int i = 0; i < served.length; i++){
                        min = Math.min(min, served[i]);
                    }

                    Iterator<Request> iterator = requests.iterator();
                    while(iterator.hasNext()){
                        Request request = iterator.next();
                        int num = request.getNum();

                        if(served[num] == min){
                            iterator.remove();
                            served[num]++;
                            availableFood--;
                            request.setReserved(true);
                            if(!requests.isEmpty()){
                                req.release();
                            }
                            return request;
                        }
                    }
                }
            } finally {
                access.release();
            }

            req.acquireUninterruptibly();
        }
    }

    void close(){
        access.acquireUninterruptibly();
        try {
            closed = true;
            req.release(w);
        } finally {
            access.release();
        }
    }
}
