
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class to hold methods common to all predatory creatures.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public abstract class Predator extends Animal
{

    /**
     * Create a new predator. A predator may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the predator will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Abstract method to get the breeding age of the predator.
     * @return int The predators breeding age.
     */
    abstract int getBreedingAge();

    /**
     * Abstract method to get the max age of the predator.
     * @return int The max age of the predator.
     */
    abstract int getMaxAge();

    /**
     * Abstract method to get the breeding probability of the predator.
     * @return double The breeding probability of the predator.
     */
    abstract double getBreedingProbability();

    /**
     * Abstract method to get the maximum litter size of a specific predator.
     * @return int The maximum litter size of a specific predator.
     */
    abstract int getMaxLitterSize();

    /**
     * Abstract method to get the food value of a specific predator.
     * @return int The food value of the specific predator.
     */
    abstract int getFoodValue();
    
    /**
     * Abstract method to get the age of the specific predator.
     * @return int The age of a specific predator.
     */
    abstract int getAge();
    
    /**
     * Abstract method to get the nocturnal status of predator.
     * @param getIsNocturnal The time of day predator acts during. 
     */
    abstract public boolean getIsNocturnal();
    
    /**
     * Abstract method to get the status of any nearby mates to the predator.
     * @return boolean nearby mate presence.
     */
    abstract boolean mateNearby();
}
