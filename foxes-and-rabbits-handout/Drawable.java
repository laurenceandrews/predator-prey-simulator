import java.util.List;

public interface Drawable
{
    /**
     * Perform the actor's regular behavior.
     * @param newActors A list for storing newly created
     * actors.
     */
    void draw(List<Drawable> newDrawables);
}