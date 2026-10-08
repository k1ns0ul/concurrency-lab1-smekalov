package org.labs;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class Stage {
    private final Semaphore req;
    private final Semaphore access = new Semaphore(1, true);
    private final Queue<Request> requests = new ArrayDeque<>();
    private final int w;
    private boolean closed;

    Stage(Semaphore req, int w){
        this.req = req;
        this.w = w;
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
        req.acquireUninterruptibly();
        access.acquireUninterruptibly();
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
