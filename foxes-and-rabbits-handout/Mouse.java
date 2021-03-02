import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a mouse.
 * mouse age, move, eat other animals, die, spread disease 
 * and take other actions.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public class Mouse extends Prey
{
    // Characteristics shared by all Mice (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 4;

    // The age to which a mouse can live.
    private static final int MAX_AGE = 85;

    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.17;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    // The food value of a single mouse. In effect, this is the
    // number of steps a mouse can go before it has to eat again.
    private static final int FOOD_VALUE = 35;

    private static final boolean IS_NOCTURNAL = true;

    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The mouse's age.
    private int age;
    // The mouse's food level, which is increased by eating.
    private int foodLevel;

    // The current disease status of a mouse.
    private boolean isDiseased;

    // The current fear level of a mouse.
    private int fear;

    /**
     * Create a mouse. A mouse can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the mouse will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {    
        super(randomAge, field, location);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        } else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
    }

    /**
     * Make this mouse act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born actors.
     */
    public void act(List<Actor> newActors) {
        incrementAge();
        incrementHunger();
        incrementDiseaseCount();
        if (getDiseaseCount() > 3) {
            setDead();
        }
        if(isAlive()) {
            if (!isScared()) {
                giveBirth(newActors); 
            }
            if (diseasedNearby()) {
                setIsDiseased();
            }
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

    /**
     * Get the current disease status of a mouse.
     * @return boolean of disease status.
     */
    protected boolean getIsDiseased() {
        return isDiseased;
    }

    /**
     * Get the current fear status of a mouse.
     * @return boolean of scraed status.
     */
    @Override
    protected boolean isScared()
    {
        return fear > 3;
    }

    /**
     * Increase the current fear level of a mouse.
     */
    private void increaseFear()
    {
        if (predatorsNearby().size() > 2) {
            fear++;
        }
    }

    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newActors A list to return newly born mice.
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
                Mouse young = new Mouse(false, field, loc);
                newActors.add(young);
            }
        }
    }

    /**
     * Check whether there are any nearby mice of the opposite gender.
     * @return boolean of any nearby opposite gender mice.
     */
    @Override
    boolean mateNearby()
    {   
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            Object object = field.getObjectAt(location);
            if(object instanceof Mouse && getIsMale()) {
                Mouse mouse = (Mouse) object;
                if(mouse.isAlive()) { 
                    return true;  
                }
            }
        }
        return false;        
    }

    /**
     * Get the nocturnal status of a mouse.
     * @return boolean of nocturnal status.
     */
    @Override
    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    /**
     * Determine what objects are nearby to the mouse 
     * and whether or not they are edible.
     * @return Location of nearby food.
     */
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
     * Increase the age. This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this mouse more hungry. This could result in the mouse's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Get the max age of a mouse.
     * @return int of max age.
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Get the max breeding age of a mouse.
     * @return int of max breeding age.
     */
    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Get the breeding probability of a mouse.
     * @return double of breeding probability.
     */
    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Get the max litter size of a mouse.
     * @return int of max litter size.
     */
    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Get the food value of a mouse.
     * @return int of food value.
     */
    @Override
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Get the age of a mouse.
     * @param int of age.
     */
    @Override
    protected int getAge()
    {
        return age;
    }

    /** 
     * Set the current age of a mouse.
     * @param age The current age of the specific eagle.
     */
    @Override
    protected void setAge(int age)
    {
        this.age = age;
    }

    /** 
     * Set the current food level of a mouse.
     * @param foodValue The curretn food value of the specific eagle.
     */
    @Override
    protected void setFoodLevel(int foodValue) {
        this.foodLevel = foodValue;
    }
}