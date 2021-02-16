
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
public abstract class Predator extends Animal
{
    // Characteristics shared by all prey
    
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    

    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newPredators)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPredators);            
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

    abstract int getAge();
    
    abstract int setAge();
    
    abstract int getBreedingAge();

    abstract int getMaxAge();

    abstract double getBreedingProbability();

    abstract int getMaxLitterSize();

    abstract int getFoodValue();
    
    abstract Location findFood();
    
}
