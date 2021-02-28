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
public abstract class Prey extends Animal
{    
    private int fear;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Prey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        fear = 0;
    }
    
    private void increaseFear()
    {
        if (predatorsNearby().size() > 2) {
            fear++;
        }
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean isScared()
    {
        return fear > 3;
    }

    abstract int getBreedingAge();

    abstract int getMaxAge();

    abstract double getBreedingProbability();

    abstract int getMaxLitterSize();

    abstract int getFoodValue();
    
    abstract public boolean getIsNocturnal();
    
    abstract boolean mateNearby();
}
