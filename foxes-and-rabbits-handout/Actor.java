import java.util.List;

/**
 * An interface allowing the act based method to be abstracted from other
 * classes.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version (2)
 */
public interface Actor
{
    /**
     * Perform the actor's regular behavior.
     * @param newActors A list for storing newly created
     * actors.
     */
    void act(List<Actor> newActors);

    /**
     * Is the actor still active?
     * @return true if still active, false if not.
     */
    boolean isAlive();

    boolean getIsNocturnal();
}