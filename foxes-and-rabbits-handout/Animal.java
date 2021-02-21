import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator; 

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Actor
{
    // Whether the animal is alive or not.
    private boolean isMale;

    private List<Object> nearbyPredators;

    private List<Object> nearbyPrey;

    private Field field;

    private List<Location> freeAdjacentLocations;

    private static final Random rand = Randomizer.getRandom();
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        nearbyPredators = new ArrayList<Object>();
        nearbyPrey = new ArrayList<Object>();
    }

    @Override
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

    protected boolean getIsMale() {
        return isMale;
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

    protected void giveBirth(List<Animal> newAnimal)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();

        Animal animal = this;
        //

        if (mateNearby(animal)) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);       
                newAnimal.add(createYoung(animal, loc));
            }
        }
    }

    private Animal createYoung(Animal animal, Location loc) {
        Animal young = null;
        if (animal instanceof Eagle) {
            young = new Eagle(false, field, loc);
        } else if (animal instanceof Scorpion) {
            young = new Scorpion(false, field, loc);
        } else if (animal instanceof Snake) {
            young = new Snake(false, field, loc);
        } else if (animal instanceof Mouse) {
            young = new Mouse(false, field, loc);
        } else if (animal instanceof Cricket) {
            young = new Cricket(false, field, loc);
        }
        return young;
    }

    protected boolean surroundingsEmpty()
    {
        freeAdjacentLocations = field.getFreeAdjacentLocations(location);
        return freeAdjacentLocations.size() <= 0;
    }

    @Override
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
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
