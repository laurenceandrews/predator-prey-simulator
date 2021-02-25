import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Plant implements Actor

{
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    
    private boolean drawable;

    private List<Location> freeAdjacentLocations;

    private static final int BREEDING_AGE = 1;
    // The age to which a fox can live.
    private static final int MAX_AGE = 70;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single rabbit. In effect, this is the

    private static final int FOOD_VALUE = 10;

    private static final boolean IS_NOCTURNAL = true;
    
    private int age;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);

        freeAdjacentLocations = new ArrayList<>();
    }
    

    @Override
    public void act(List<Actor> newActors) {
        incrementAge();
        if(isAlive()) {
            giveBirth(newActors);            
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */

    public boolean isAlive()
    {
        return alive;
    }
    

    /**
     * Indicate that the animal is no longer alive.
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
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    protected boolean surroundingsEmpty()
    {
        freeAdjacentLocations = field.getFreeAdjacentLocations(location);
        return freeAdjacentLocations.size() <= 0;
    }

    protected void giveBirth(List<Actor> newActors)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        if (free.size() == 8) {
            for(int b = 0; b < MAX_LITTER_SIZE && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Plant seedling = new Plant(false, field, loc);
                newActors.add(seedling);
            }
        }
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= getBreedingAge();
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

    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    protected int getAge()
    {
        return age;
    }

    protected void setAge(int age)
    {
        this.age = age;
    }

    public boolean getIsNocturnal() {
        return IS_NOCTURNAL;
    }

    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }
}
