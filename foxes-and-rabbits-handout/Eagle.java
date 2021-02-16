import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Eagle extends Predator
{   
    // Characteristics shared by all foxes (class variables).

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a fox can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.03;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int FOOD_VALUE = 20;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location)
    {    
        super(randomAge, field, location);
    }

    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    @Override
    protected void giveBirth(List<Animal> newEagles)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Eagle young = new Eagle(false, field, loc);
            newEagles.add(young);
        }
    }

    @Override
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Scorpion) {
                Scorpion scorpion = (Scorpion) animal;
                if(scorpion.isAlive()) { 
                    scorpion.setDead();
                    getFoodValue();
                    return where;
                }
            }

            else if(animal instanceof Snake) {
                Snake snake = (Snake) animal;
                if(snake.isAlive()) { 
                    snake.setDead();
                    getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }

    protected int setEagleMaxAge()
    {
        getMaxAge();
    }

    protected int setEagleBreedingAge()
    {
        getBreedingAge();
    }

    protected double setEagleBreedingProbability()
    {
        getBreedingProbability();
    }

    protected int setEagleMaxLitterSize()
    {
        getMaxLitterSize();
    }

    @Override
    protected int setFoodValue(Animal animalClass)
    {
        foodValue = getFoodValue();
    }
}
