import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Actor

{
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;

    private boolean isMale;

    private boolean drawable;

    private boolean isDiseased;

    private int diseaseCount;

    private static final double GENDER_PROBABILITY = 0.5;
    private static final double DISEASED_PROBABILITY = 0.05;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    private List<Location> freeAdjacentLocations;

    private List<Object> nearbyPredators;

    private List<Object> nearbyPrey;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        diseaseCount = 0;

        freeAdjacentLocations = new ArrayList<>();
        nearbyPredators = new ArrayList<Object>();
        nearbyPrey = new ArrayList<Object>();

        int maleOrFemale = rand.nextInt(1);
        if (maleOrFemale < GENDER_PROBABILITY) {
            isMale = true;
        }

        int diseasedOrHealthy = rand.nextInt(1);
        if (diseasedOrHealthy < DISEASED_PROBABILITY) {
            isDiseased = true;
        }
    }

    abstract public void act(List<Actor> newActors);

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

    abstract void giveBirth(List<Actor> newActors);

    abstract boolean mateNearby();

    boolean diseasedNearby()
    {   
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location location = it.next();
            Object object = field.getObjectAt(location);
            if(object instanceof Animal && getIsDiseased()) {
                return true;
            }
        }
        return false;        
    }

    protected void setIsDiseased() {
        int diseasedOrHealthy = rand.nextInt(1);
        if (diseasedOrHealthy < DISEASED_PROBABILITY) {
            isDiseased = true;
        }
    }

    protected abstract boolean getIsDiseased();

    protected boolean getIsMale() {
        return isMale;
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
        return getAge() >= getBreedingAge();
    }

    public void increaseDiseaseCount() {
        diseaseCount++;
    }

    public int getDiseaseCount() {
        return diseaseCount;
    }

    public void diseased() {
        if (diseasedNearby()) {
            setIsDiseased();
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

    abstract public boolean getIsNocturnal();
}
