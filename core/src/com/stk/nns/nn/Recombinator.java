package com.stk.nns.nn;

import com.stk.nns.snake.Snake;

import java.util.*;

public class Recombinator {

    private static Comparator<Snake> lifeSpanComparator = (o1, o2) -> o2.getFinalLifeSpan().compareTo(o1.getFinalLifeSpan());
    private static Comparator<Snake> distanceToFoodAtDeathComparator = (o1, o2) -> o1.getDistanceToFoodAtDeath().compareTo(o2.getDistanceToFoodAtDeath());
    private static Comparator<Snake> feedingComparator = (o1, o2) -> o2.getNumberOfFeedings().compareTo(o1.getNumberOfFeedings());

    public static List<Network> recombine(List<Snake> snakes, int generation) {
/*        snakes.sort(lifeSpanComparator);*/
/*        snakes.sort(distanceToFoodAtDeathComparator);*/
        snakes.sort(feedingComparator);

        List<Network> childNetworks = new ArrayList<>();
        if (snakes.size() == 1) {
            childNetworks.add(new Network(snakes.get(0).getNetwork(), snakes.get(0).getNetwork()));
            return childNetworks;
        }
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%% TOP 10 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            for (int i = 0; i < 10; i++) {
                Snake snake = snakes.get(i);
                System.out.println("#" + (i + 1) + ": " + snake.getNumberOfFeedings() + " feedings, " + snake.getFinalLifeSpan() / 1000d + " s lifespan, distance to next food: " + snake.getDistanceToFoodAtDeath());
            }





        int pass = 0;
        while (childNetworks.size() < snakes.size()) {
            for (int i = 0; i < snakes.size(); i++) {
                boolean hasBred = false;
                if (snakes.get(i).getNumberOfFeedings() > 0 && shouldBreed(i)) {
                    if (pass == 0 && i == 0) { // One perfect clone of winner
                        childNetworks.add(snakes.get(i).getNetwork());
                        hasBred = true;
                    }

                   for (int partner = 0; !hasBred && partner < snakes.size(); partner++) {
                        if (partner != i && shouldBreed(partner)) {
                            childNetworks.add(new Network(snakes.get(i).getNetwork(), snakes.get(partner).getNetwork()));
                            hasBred = true;
                        }

                    }

                }
            }
            pass++;
        }

/*        Collections.reverse(childNetworks);*/

        return childNetworks;
    }

    private static boolean shouldBreed(int i) {
        Random rnd = new Random();
        float score = (i + 1) / 10f;
        float attractiveness = .75f / score / 10f;
        float roll = rnd.nextFloat();
        if (i == 0) {
            attractiveness = 1.0f;
        }
        return roll <= attractiveness;

    }
}
