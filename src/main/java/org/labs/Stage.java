package org.labs;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

class Stage {
    private final Semaphore req;
    private final Semaphore access = new Semaphore(1, true);
    private final Queue<Request> requests = new ArrayDeque<>();
    private final int w;
    private final Thread[] workers;
    private final AtomicReference<InterruptedException> failure = new AtomicReference<>();
    private boolean closed;

    Stage(Semaphore req, int w){
        this(req, w, new Thread[0]);
    }

    Stage(Semaphore req, int w, Thread[] workers){
        this.req = req;
        this.w = w;
        this.workers = workers;
    }

    void putRequest(Request request) throws InterruptedException {
        access.acquire();
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

    Request getRequest() throws InterruptedException {
        req.acquire();
        access.acquire();
        try {
            Request request = requests.poll();

            if(request != null){
                return request;
            }

            if(closed){
                return null;
            }

            throw new IllegalStateException("Signal received without a request");
        } finally {
            access.release();
        }
    }

    void close() throws InterruptedException {
        access.acquire();
        try {
            closed = true;
            req.release(w);
        } finally {
            access.release();
        }
    }

    void cancel(InterruptedException e){
        if(failure.compareAndSet(null, e)){
            for(Thread worker : workers){
                if(worker != null){
                    worker.interrupt();
                }
            }
        }
    }

    InterruptedException getFailure(){
        return failure.get();
    }
}
