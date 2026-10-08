package org.labs;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scan = new Scanner(System.in);

        int n = scan.nextInt();
        int f = scan.nextInt();
        int w = scan.nextInt();

        SimulationResult result = new DiningSimulation().run(n, f, w);

        System.out.println("food=" + f);
        System.out.println("eaten=" + result.getEaten());
        System.out.println("discussions=" + result.getDiscussions());
        System.out.println("delivered=" + result.getDelivered());
        System.out.println("remaining=" + result.getRemaining());

        for(int i = 0; i < n; i++){
            System.out.println("programmer_" + i + "=" + result.getEatenBy(i));
        }

    }
}
