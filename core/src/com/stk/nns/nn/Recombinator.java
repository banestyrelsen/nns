package com.stk.nns.nn;

import com.stk.nns.snake.Snake;

import java.util.*;

public class Recombinator {

    private static Comparator<Snake> lifeSpanComparator = (o1, o2) -> o2.getFinalLifeSpan().compareTo(o1.getFinalLifeSpan());
    private static Comparator<Snake> feedingComparator = (o1, o2) -> o2.getNumberOfFeedings().compareTo(o1.getNumberOfFeedings());
    private static Comparator<Snake> distanceToFoodAtDeathComparator = (o1, o2) -> o1.getDistanceToFoodAtDeath().compareTo(o2.getDistanceToFoodAtDeath());

    public static List<Network> recombine(List<Snake> snakes, int generation) {
/*        snakes.sort(lifeSpanComparator);*/
        snakes.sort(distanceToFoodAtDeathComparator);
        snakes.sort(feedingComparator);

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%% TOP 10 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < 10; i++) {
            Snake snake = snakes.get(i);
            System.out.println("#" + (i + 1) + ": " + snake.getNumberOfFeedings() + " feedings, " + snake.getFinalLifeSpan() / 1000d + " s lifespan, distance to next food: " + snake.getDistanceToFoodAtDeath());
        }

        List<Network> childNetworks = new ArrayList<>();



        while (childNetworks.size() < snakes.size()) {
            for (int i = 0; i < snakes.size(); i++) {
                boolean hasBred = false;
                if (shouldBreed(i)) {
                    for (int partner = 0; !hasBred && partner < snakes.size(); partner++) {
                        if (partner != i && shouldBreed(partner)) {
                            childNetworks.add(new Network(snakes.get(i).getNetwork(), snakes.get(partner).getNetwork()));
                            hasBred = true;
                        }

                    }

                }
            }
        }

/*        Collections.reverse(childNetworks);*/

        return childNetworks;
    }

    private static boolean shouldBreed(int i) {
        Random rnd = new Random();
        float score = (i + 1) / 10f;
        float attractiveness = .75f / score / 10f;
        float roll = rnd.nextFloat();
        return roll <= attractiveness;

    }
}
