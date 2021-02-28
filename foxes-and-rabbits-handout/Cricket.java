import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Cricket extends Prey
{
    // Characteristics shared by all foxes (class variables).

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 7;
    // The age to which a fox can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int FOOD_VALUE = 10;

    private static final boolean IS_NOCTURNAL = true;

    private static final Random rand = Randomizer.getRandom();
    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    private boolean isDiseased;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cricket(boolean randomAge, Field field, Location location)
    {    
        super(randomAge, field, location);
        this.foodLevel = FOOD_VALUE;
        isDiseased = false;
    }

    @Override
    public void act(List<Actor> newActors) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newActors);
            increaseDiseaseCount();
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }

    protected boolean getIsDiseased() {
        return isDiseased;
    }

    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    @Override
    protected void giveBirth(List<Actor> newActors)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        if (mateNearby()) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Cricket young = new Cricket(false, field, loc);
                newActors.add(young);
            }
        }
    }

    @Override
    boolean mateNearby()
    {   
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            Object object = field.getObjectAt(location);
            if(object instanceof Cricket && getIsMale()) {
                Cricket cricket = (Cricket) object;
                if(cricket.isAlive()) { 
                    return true;  
                }
            }
        }
        return false;        
    }

    @Override
    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            Object object = field.getObjectAt(location);
            if(object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive()) { 
                    plant.setDead();
                    foodLevel = plant.getFoodValue();
                    return location;
                }
            }
        }
        return null;
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    @Override
    protected int getAge()
    {
        return age;
    }

    @Override
    protected void setAge(int age)
    {
        this.age = age;
    }

    @Override
    protected void setFoodLevel(int foodValue) {
        this.foodLevel = foodValue;
    }
}