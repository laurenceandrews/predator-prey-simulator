import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Plant implements Actor

{
    private static final Random rand = Randomizer.getRandom();

    // Whether the plant is alive or not.
    private boolean alive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;

    private boolean drawable;

    private List<Location> freeAdjacentLocations;

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a fox can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int FOOD_VALUE = 5;

    private static final boolean IS_NOCTURNAL = false;

    private int age;

    private int diseaseCount;

    /**
     * Create a new plant at location in field give it a disease count
     * and random age.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The starting age of a plant
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        diseaseCount = 0;

        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            age = 0;
        }

        freeAdjacentLocations = new ArrayList<>();
    }

    @Override
    /**
     * This method overrides the actor interface's act method, age is increased for each step and plants are
     * able to give birth which acts as the plant's growth.
     * @param newActors A list of new actors within the simulation.
     */
    public void act(List<Actor> newActors) {
        incrementAge();
        if(isAlive()) {
            giveBirth(newActors);            
        }
    }

    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    protected Field getField()
    {
        return field;
    }

    
    /**
     * Check if there are any objects in the locations surrounding an entity.
     */
    protected boolean surroundingsEmpty()
    {
        freeAdjacentLocations = field.getFreeAdjacentLocations(location);
        return freeAdjacentLocations.size() <= 0;
    }

    /**
     * A plant can give birth only if every location surrounding it is empty. This means they form clusters
     * of 9 that then can't grow any larger.
     * @param newActors A list of new actors within the simulation.
     */
    protected void giveBirth(List<Actor> newActors)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        int births = breed();

        if (free.size() == 8) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Plant seedling = new Plant(false, field, loc);
                newActors.add(seedling);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * A plant can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     * Increase the age. This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /** 
     * Getter method allowing the retrieval of max age.
     * @return int The max age of a plant
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /** 
     * Getter method allowing the retrieval of the breeding age of a plant
     * @return int The breeding age of a plant.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /** 
     * Getter method allowing the retrieval of the max litter size of a plant.
     * @return int The maximum litter size a plant can produce.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /** 
     * Getter method allowing the retrieval of the current age of a plant
     * @return int The current age of the specific plant.
     */
    protected int getAge()
    {
        return age;
    }

    /** 
     * Setter method allowing the assinging of age to a specific plant.
     * @param age The age of the plant
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /** 
     * Getter method allowing the retrieval of nocturnal status.
     * @return boolean value of nocturnal status.
     */
    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    /** 
     * Getter method allowing the retrieval of the food value of the plant.
     * @return int of the food value.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /** 
     * Getter method allowing the retrieval of disease status.
     * @return boolean value of the disease status.
     */
    public boolean getIsDiseased() {
        return false;
    }
}
