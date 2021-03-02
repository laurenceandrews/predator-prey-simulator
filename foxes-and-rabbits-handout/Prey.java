import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class to hold methods common to all prey ceratures.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public abstract class Prey extends Animal
{    
   

    /**
     * Create a new prey. A prey may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the prey will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Prey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Abstact method to determine if a prey is scared.
     * @return boolean of scraed status
     */
    abstract boolean isScared();
    
    /**
     * Abstract method to get the breeding age of the prey.
     * @return int The preys breeding age.
     */
    abstract int getBreedingAge();

    /**
     * Abstract method to get the max age of the prey.
     * @return int The max age of the prey.
     */
    abstract int getMaxAge();

    /**
     * Abstract method to get the breeding probability of the prey.
     * @return double The breeding probability of the prey.
     */
    abstract double getBreedingProbability();

    /**
     * Abstract method to get the maximum litter size of a specific prey.
     * @return int The maximum litter size of a specific prey.
     */
    abstract int getMaxLitterSize();

    /**
     * Abstract method to get the food value of a specific prey.
     * @return int The food value of the specific prey.
     */
    abstract int getFoodValue();
    
    /**
     * Abstract method to get the age of the specific prey.
     * @return int The age of a specific prey.
     */
    abstract int getAge();
    
    /**
     * Abstract method to get the nocturnal status of prey.
     * @param getIsNocturnal The time of day prey acts during. 
     */
    abstract public boolean getIsNocturnal();
    
    /**
     * Abstract method to get the status of any nearby mates to the prey.
     * @return boolean nearby mate presence.
     */
    abstract boolean mateNearby();
}
