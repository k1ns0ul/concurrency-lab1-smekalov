package org.labs;

public class SimulationResult {
    private final int eaten;
    private final int discussions;
    private final int delivered;
    private final int remaining;
    private final int[] eatenBy;

    SimulationResult(int eaten, int discussions, int delivered, int remaining, int[] eatenBy){
        this.eaten = eaten;
        this.discussions = discussions;
        this.delivered = delivered;
        this.remaining = remaining;
        this.eatenBy = eatenBy.clone();
    }

    public int getEaten(){
        return eaten;
    }

    public int getDelivered(){
        return delivered;
    }

    public int getDiscussions(){
        return discussions;
    }

    public int getRemaining(){
        return remaining;
    }

    public int getEatenBy(int i){
        return eatenBy[i];
    }

}
