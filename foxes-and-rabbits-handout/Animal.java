import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;

    private boolean isMale;
    
    private int age;

    private static final double GENDER_PROBABILITY = 0.5;

    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    private List<Location> freeAdjacentLocations;

    private List<Object> nearbyPredators;

    private List<Object> nearbyPrey;
    
    private int BREEDING_AGE = 1;
    
    private int MAX_AGE = 1;
    
    private double BREEDING_PROBABILITY = 1;
    
    private int MAX_LITTER_SIZE = 1;
    
    private int FOOD_VALUE = 1;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);

        freeAdjacentLocations = new ArrayList<>();
        nearbyPredators = new ArrayList<Object>();
        nearbyPrey = new ArrayList<Object>();

        int maleOrFemale = rand.nextInt(1);
        if (maleOrFemale < GENDER_PROBABILITY) {
            isMale = true;
        }

        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
        else {
            age = 0;
        }
        
        incrementAge();
    }

    public void act(List<Animal> newRabbits) {
        incrementAge();
        if(isAlive()) {
            giveBirth(newRabbits);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    abstract void incrementAge();

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

    protected void giveBirth(List<Animal> newAnimal)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();

        if (mateNearby()) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Cricket young = new Cricket(false, field, loc);
                newAnimal.add(young);
            }
        }
    }

    boolean mateNearby(Animal animalClass)
    {      
        for (int i = 0; i < getField().adjacentLocations(getLocation()).size(); i++) {
            if ((field.getObjectAt(field.adjacentLocations(getLocation()).get(i)) == animalClass) &&
            (field.getObjectAt(field.adjacentLocations(getLocation()).get(i)) == animalClass) != getIsMale()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    protected boolean getIsMale() {
        return isMale;
    }

    abstract boolean mateNearby();

    @Override
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    protected List<Object> predatorsNearby()
    {      
        if (freeAdjacentLocations.size() > 0) {
            for (int i = 0; i < freeAdjacentLocations.size(); i++) {
                if (field.getObjectAt(freeAdjacentLocations.get(i)) instanceof Predator) {
                    nearbyPredators.add(field.getObjectAt(freeAdjacentLocations.get(i)));
                }
            }
        }
        return nearbyPredators;
    }

    protected List<Object> preyNearby()
    {
        if (freeAdjacentLocations.size() > 0) {
            for (int i = 0; i < freeAdjacentLocations.size(); i++) {
                if (field.getObjectAt(freeAdjacentLocations.get(i)) instanceof Prey) {
                    nearbyPrey.add(field.getObjectAt(freeAdjacentLocations.get(i)));
                }
            }
        }
        return nearbyPredators;
    }
    
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
}
