import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * An abstarct class representing shared characteristics of animals.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Actor

{
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;

    // Whether the animal is male.
    private boolean isMale;

    private boolean drawable;

    private boolean isDiseased;

    private int diseaseCount;

    private static final double GENDER_PROBABILITY = 0.5;

    private static final double DISEASED_PROBABILITY = 0.005;

    // The animal's field.
    private Field field;

    // The animal's position in the field.
    private Location location;

    private List<Location> freeAdjacentLocations;

    private List<Object> nearbyPredators;

    private List<Object> nearbyPrey;

    /**
     * Create a new animal with a random age at location in field as well as giving it a gender and a 
     * chance to be diseased.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The age of the animal
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

        double diseasedOrHealthy = rand.nextDouble();
        if (diseasedOrHealthy < DISEASED_PROBABILITY) {
            isDiseased = true;
        }
    }

    /**
     * @param newActors The newly created entities
     */
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

    /**
     * Check for any surrounding objects.
     * @return boolean value of empty surroundings
     */
    protected boolean surroundingsEmpty()
    {
        freeAdjacentLocations = field.getFreeAdjacentLocations(location);
        return freeAdjacentLocations.size() <= 0;
    }

    /**
     * Check for any surrounding predators of the specific object.
     * @return A list of objects, all of which will be predators
     */
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

    /**
     * Check for any surrounding prey of the specific object.
     * @return A list of objects, all of which will be prey.
     */
    protected List<Object> preyNearby()
    {
        if (freeAdjacentLocations.size() > 0) {
            for (int i = 0; i < freeAdjacentLocations.size(); i++) {
                if (field.getObjectAt(freeAdjacentLocations.get(i)) instanceof Prey) {
                    nearbyPrey.add(field.getObjectAt(freeAdjacentLocations.get(i)));
                }
            }
        }
        return nearbyPrey;
    }

    /**
     * Abstract give birth
     * @param newActors The newly created entities
     */
    abstract void giveBirth(List<Actor> newActors);

    /**
     * Abstract mate nearby
     * @return boolean of mate presence.
     */
    abstract boolean mateNearby();

    /**
     * Check for any nearby objects that have a disease status of true.
     * @return boolean value of any nearby diseased objects.
     */
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

    /**
     * Setter to assign the diseased attribute to an animal.
     */
    protected void setIsDiseased() {
        double diseasedOrHealthy = rand.nextDouble();
        if (diseasedOrHealthy < DISEASED_PROBABILITY) {
            isDiseased = true;
        }
    }

    /**
     * Abstract method to get the diseased status of an animal.
     * @return boolean of disease status.
     */
    protected abstract boolean getIsDiseased();

    /**
     * Get the gender of the current animal.
     * @return boolean animal's gender
     */
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
     * An animal can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     * If diesased then increment the disease count by 1.
     */
    public void incrementDiseaseCount() {
        if (getIsDiseased()) {
            diseaseCount++;
        }
    }

    /**
     * Get the current disease count of an animal.
     * @return int the current disease count
     */
    public int getDiseaseCount() {
        return diseaseCount;
    }

    /**
     * If there are nearby objects that are diseased then make the current object diseased also.
     */
    public void diseased() {
        if (diseasedNearby()) {
            setIsDiseased();
        }
    }

    /**
     * Abstract method to get the breeding age of the animal.
     * @return int The animals breeding age.
     */
    abstract int getBreedingAge();

    /**
     * Abstract method to get the max age of the animal.
     * @return int The max age of the animal.
     */
    abstract int getMaxAge();

    /**
     * Abstract method to get the breeding probability of the animal.
     * @return double The breeding probability of the animal.
     */
    abstract double getBreedingProbability();

    /**
     * Abstract method to get the maximum litter size of a specific animal.
     * @return int The maximum litter size of a specific animal.
     */
    abstract int getMaxLitterSize();

    /**
     * Abstract method to get the food value of a specific animal.
     * @return int The food value of the specific animal.
     */
    abstract int getFoodValue();

    /**
     * Abstract method to get the age of the specific animal.
     * @return int The age of a specific animal.
     */
    abstract int getAge();

    /**
     * Abstract method to get the nocturnal status of an animal.
     * @param getIsNocturnal The time of day an animal acts during. 
     */
    abstract public boolean getIsNocturnal();

    /**
     * Abstract method to set the age of a specific animal.
     * @param age the age of the animal.
     */
    abstract void setAge(int age);

    /**
     * Abstract method to set the food level of the specific animal.
     * @param food value The food an animal requires.
     */
    abstract void setFoodLevel(int foodValue);
}
