package org.labs;

class Oficiant implements Runnable {
    private final Kitchen kitchen;
    private final Stage stage;
    private int deliveredCount;

    Oficiant(Kitchen kitchen, Stage stage){
        this.kitchen = kitchen;
        this.stage = stage;
    }

    int getDeliveredCount(){
        return deliveredCount;
    }

    public void run(){
        while(true){
            Request request = stage.getRequest();

            if(request == null){
                return;
            }

            boolean res = kitchen.getOneFood();
            if(res){
                deliveredCount++;
            }
            request.sendResponse(res);
        }
    }
}
