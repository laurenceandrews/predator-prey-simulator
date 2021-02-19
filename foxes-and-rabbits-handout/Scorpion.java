import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Scorpion extends Predator
{
    // Characteristics shared by all foxes (class variables).

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a fox can live.
    private static final int MAX_AGE = 10;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.03;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int FOOD_VALUE = 5;
    
    private static final boolean IS_NOCTURNAL = true;

    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Scorpion(boolean randomAge, Field field, Location location)
    {    
        super(randomAge, field, location);
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Animal> newScorpions) {
        if(isAlive()) {
            giveBirth(newScorpions);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    @Override
    protected void giveBirth(List<Animal> newScorpions)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Scorpion young = new Scorpion(false, field, loc);
            newScorpions.add(young);
        }
    }

    @Override
    protected boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            Object meal = field.getObjectAt(location);
            if(meal instanceof Mouse) {
                Mouse mouse = (Mouse) meal;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel = mouse.getFoodValue();
                    return location;
                }
            } else if(meal instanceof Cricket) {
                Cricket cricket = (Cricket) meal;
                if(cricket.isAlive()) { 
                    cricket.setDead();
                    foodLevel = cricket.getFoodValue();
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
