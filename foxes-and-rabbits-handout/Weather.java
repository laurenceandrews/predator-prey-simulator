import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Weather implements Actor

{
    private static final Random rand = Randomizer.getRandom();

    private boolean drawable;

    private boolean isRaining;

    private boolean isSunny;

    private boolean isFoggy;

    private static final double FOG_PROBABILITY = 0.1;

    private static final double RAIN_PROBABILITY = 0.3;

    private static final double SUN_PROBABILITY = 1;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Weather()
    {
    }

    public void act(List<Actor> newActors) {
        reset();
        change();
    }

    protected Weather change()
    {
        double state = rand.nextDouble();
        if (state < FOG_PROBABILITY) {
            isFoggy = true;
        } else if (state < RAIN_PROBABILITY) {
            isRaining = true;
        } else if (state < SUN_PROBABILITY) {
            isSunny = true;
        }
        return this;
    }

    protected void reset() {
        isRaining = false;
        isSunny = false;
        isFoggy = false;
    }

    @Override
    public boolean isFoggy() {
        return isFoggy;
    }

    @Override
    public boolean isRaining() {
        return isRaining;
    }

    @Override
    public boolean isSunny() {
        return isSunny;
    }
}
