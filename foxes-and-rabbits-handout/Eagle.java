import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an eagle.
 * Eagles age, move, eat other animals, die, spread disease and take other actions.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public class Eagle extends Predator
{   
    // Characteristics shared by all eagles (class variables).

    // The age at which an eagle can start to breed.
    private static final int BREEDING_AGE = 12;

    // The age to which an eagle can live.
    private static final int MAX_AGE = 200;

    // The likelihood of an eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.064;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    // The food value of an eagle. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int FOOD_VALUE = 150;

    // The nocturnal status of an eagle.
    private static final boolean IS_NOCTURNAL = false;

    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The eagle's age.
    private int age;

    // The eagle's food level, which is increased by eating.
    private int foodLevel;

    // The current diseased status of an eagle.
    private boolean isDiseased;

    /**
     * Create an eagle. An eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location)
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
     * Make this eagle act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born eagles.
     */
    public void act(List<Actor> newActors) {
        incrementAge();
        incrementHunger();
        incrementDiseaseCount();
        if (getDiseaseCount() > 3) {
            setDead();
        }
        if(isAlive()) {
            giveBirth(newActors); 
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
     * Get the current disease status of an eagle.
     * @return boolean of disease status.
     */
    protected boolean getIsDiseased() {
        return isDiseased;
    }

    /**
     * Check whether or not this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newActors A list to return newly born eagles.
     */
    @Override
    protected void giveBirth(List<Actor> newActors)
    {
        // New eagles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        if (mateNearby()) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Eagle young = new Eagle(false, field, loc);
                newActors.add(young);
            }
        }
    }

    /**
     * Check whether there are any nearby eagles of the opposite gender.
     * @return boolean of any nearby opposite gender eagles.
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
            if(object instanceof Eagle && getIsMale()) {
                Eagle eagle = (Eagle) object;
                if(eagle.isAlive()) { 
                    return true;  
                }
            }
        }
        return false;        
    }

    /**
     * Get the nocturnal status of an eagle
     * @return boolean of nocturnal status.
     */
    @Override
    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    /**
     * Determine what objects are nearby to the eagle and whether or not 
     * they are edible.
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
            if(object instanceof Snake) {
                Snake snake = (Snake) object;
                if(snake.isAlive()) { 
                    snake.setDead();
                    foodLevel = snake.getFoodValue();
                    return location;
                }
            } else if (object instanceof Scorpion) {
                Scorpion scorpion = (Scorpion) object;
                if(scorpion.isAlive()) { 
                    scorpion.setDead();
                    foodLevel = scorpion.getFoodValue();
                    return location;
                }
            }
        }
        return null;
    }

    /**
     * Increase the age. This could result in the eagle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this eagle more hungry. This could result in the eagle's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Get the max age of an eagle.
     * @return int of max age.
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Get the max breeding age of an eagle.
     * @return int of max breeding age.
     */
    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Get the breeding probability of an eagle.
     * @return double of breeding probability.
     */
    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Get the max litter size of an eagle.
     * @return int of max litter size.
     */
    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Get the food value of an eagle.
     * @return int of food value.
     */
    @Override
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Get the age of an eagle.
     * @param int of age.
     */
    @Override
    protected int getAge()
    {
        return age;
    }

    /** 
     * Set the current age of an eagle.
     * @param age The current age of the specific eagle.
     */
    @Override
    protected void setAge(int age)
    {
        this.age = age;
    }

    /** 
     * Set the current food level of an eagle.
     * @param foodValue The curretn food value of the specific eagle.
     */
    @Override
    protected void setFoodLevel(int foodValue) {
        this.foodLevel = foodValue;
    }
}