import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator; 
import java.util.List;

public abstract class Actor
{
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    
    private static final Random rand = Randomizer.getRandom();

    public Actor(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        setInitialState(randomAge);
    }

    abstract public void act(List<Actor> newActors);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
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

    abstract Field getField();

    abstract int breed();

    /**
     * A fox can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    protected void setInitialState(boolean randomAge) {
        if(randomAge) {
            setAge(rand.nextInt(getMaxAge()));
            setFoodLevel(rand.nextInt(getFoodValue()));
        }
        else {
            setAge(0);
            setFoodLevel(getFoodValue());
        }
    }
    
    abstract int getBreedingAge();

    abstract int getMaxAge();

    abstract double getBreedingProbability();

    abstract int getMaxLitterSize();

    abstract int getFoodValue(); 

    abstract int getAge();

    abstract void setAge(int age);

    abstract void setFoodLevel(int foodValue);

    abstract boolean getIsNocturnal();
}