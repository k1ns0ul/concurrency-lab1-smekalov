package org.labs;

import java.util.concurrent.Semaphore;

class Programmist implements Runnable {
    private final Spoon spoonLeft;
    private final Spoon spoonRight;
    private final Stage stage;
    private final Semaphore room;
    private final int num;
    private int eatenCount;
    private int discussionCount;

    Programmist(int num, Spoon spoonLeft, Spoon spoonRight, Stage stage, Semaphore room){
        this.num = num;
        this.spoonLeft = spoonLeft;
        this.spoonRight = spoonRight;
        this.stage = stage;
        this.room = room;
    }

    private boolean getFood(){
        Request request = new Request(num);
        stage.putRequest(request);
        return request.getResponse();
    }

    private void eat(){
        room.acquireUninterruptibly();
        try {
            spoonLeft.take();
            try {
                spoonRight.take();
                try {
                    eatenCount++;
                } finally {
                    spoonRight.put();
                }
            } finally {
                spoonLeft.put();
            }
        } finally {
            room.release();
        }
    }

    private void discussTeachers(){
        discussionCount++;
    }

    int getEatenCount(){
        return eatenCount;
    }

    int getDiscussionCount(){
        return discussionCount;
    }

    public void run(){
        while(true){
            eat();
            discussTeachers();

            if(!getFood()){
                return;
            }
        }
    }
}
