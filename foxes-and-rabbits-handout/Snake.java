import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * Eagles age, move, eat other animals, die, spread disease 
 * and take other actions.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public class Snake extends Predator
{
    // Characteristics shared by all snakes (class variables).

    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 6;

    // The age to which a snake can live.
    private static final int MAX_AGE = 120;

    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.075;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    // The food value of a single snake. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int FOOD_VALUE = 60;

    private static final boolean IS_NOCTURNAL = false;

    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The snake's age.
    private int age;

    // The snake's food level, which is increased by eating.
    private int foodLevel;

    // The current disease status of a snake.
    private boolean isDiseased;

    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location)
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
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Get the current disease status of a snake.
     * @return boolean of disease status.
     */
    protected boolean getIsDiseased() {
        return isDiseased;
    }

    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newActors A list to return newly born snakes.
     */
    @Override
    protected void giveBirth(List<Actor> newActors)
    {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        if (mateNearby()) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Actor young = new Snake(false, field, loc);
                newActors.add(young);
            }
        }
    }

    /**
     * Check whether there are any nearby snakes of the opposite gender.
     * @return boolean of any nearby opposite gender snakes.
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
            if(object instanceof Snake && getIsMale()) {
                Snake snake = (Snake) object;
                if(snake.isAlive()) { 
                    return true;  
                }
            }
        }
        return false;        
    }

    /**
     * Get the nocturnal status of a snake.
     * @return boolean of nocturnal status.
     */
    @Override
    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    /**
     * Determine what objects are nearby to the snake 
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
            Object meal = field.getObjectAt(location);
            if(meal instanceof Mouse) {
                Mouse mouse = (Mouse) meal;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel = mouse.getFoodValue();
                    return location;
                }
            }
        }
        return null;
    }

    /**
     * Increase the age. This could result in the snake's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this snake more hungry. This could result in the snake's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Get the max age of a snake.
     * @return int of max age.
     */
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Get the max breeding age of a snake.
     * @return int of max breeding age.
     */
    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Get the breeding probability of a snake.
     * @return double of breeding probability.
     */
    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Get the max litter size of a snake.
     * @return int of max litter size.
     */
    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Get the food value of a snake.
     * @return int of food value.
     */
    @Override
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Get the age of a snake.
     * @param int of age.
     */
    @Override
    protected int getAge()
    {
        return age;
    }

    /** 
     * Set the current age of a snake.
     * @param age The current age of the specific eagle.
     */
    @Override
    protected void setAge(int age)
    {
        this.age = age;
    }

    /** 
     * Set the current food level of a snake.
     * @param foodValue The curretn food value of the specific eagle.
     */
    @Override
    protected void setFoodLevel(int foodValue) {
        this.foodLevel = foodValue;
    }
}