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
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    private int age;

    private boolean isMale;

    private int foodLevel;

    private List<Location> freeAdjacentLocations;

    private List<Object> nearbyPredators;

    private List<Object> nearbyPrey;

    private static final Random rand = Randomizer.getRandom();

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
        setRandomAge(randomAge);

        freeAdjacentLocations = new ArrayList<>();
        nearbyPredators = new ArrayList<Object>();
        nearbyPrey = new ArrayList<Object>();
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

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

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
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

        Animal animal = newAnimal.get(0);

        if (mateNearby(animal)) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);       
                newAnimal.add(createYoung(animal, loc));
            }
        }
    }

    private Animal createYoung(Animal animal, Location loc) {
        Animal young;
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
    
    private Animal checkAnimalType(Animal animal)
    {
        
    }
    
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    abstract int getBreedingAge();

    abstract int getMaxAge();

    abstract double getBreedingProbability();

    abstract int getMaxLitterSize();

    abstract int getFoodValue(); 
    
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Scorpion) {
                Scorpion scorpion = (Scorpion) animal;
                if(scorpion.isAlive()) { 
                    scorpion.setDead();
                    getFoodValue();
                    return where;
                }
            }

            else if(animal instanceof Snake) {
                Snake snake = (Snake) animal;
                if(snake.isAlive()) { 
                    snake.setDead();
                    getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }

    protected void setRandomAge(boolean randomAge) {
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getFoodValue());
        }
        else {
            age = 0;
            foodLevel = getFoodValue();
        }
    }
    
    
    
}
