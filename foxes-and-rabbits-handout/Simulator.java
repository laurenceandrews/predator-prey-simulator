import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // The probability that a fox will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.004;
    // The probability that a rabbit will be created in any given grid position.

    private static final double SNAKE_CREATION_PROBABILITY = 0.01; 

    private static final double SCORPION_CREATION_PROBABILITY = 0.012; 

    private static final double MOUSE_CREATION_PROBABILITY = 0.03; 
    private static final double CRICKET_CREATION_PROBABILITY = 0.06;
    private static final double PLANT_CREATION_PROBABILITY = 0.08;
    // List of animals in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;

    private List<Drawable> drawables;

    private boolean isDay;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        drawables = new ArrayList<>();

        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Plant.class, Color.GREEN);
        view.setColor(Cricket.class, Color.GRAY);
        view.setColor(Mouse.class, Color.BLUE);
        view.setColor(Scorpion.class, Color.ORANGE);
        view.setColor(Snake.class, Color.MAGENTA);
        view.setColor(Eagle.class, Color.RED);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }

    public void setDay(boolean isDay) {
        this.isDay = isDay;
    }

    public void dayOrNight () {
        if (step % 2 == 0) {
            setDay(false);
        } else {
            setDay(true);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        dayOrNight();
        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();
        
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if (!isDay) {
                actNight(actor, newActors);
            } else {
                actDay(actor, newActors);
            }
            if(!actor.isAlive()) {
                it.remove();
            }
        }

        for(Iterator<Drawable> it = drawables.iterator(); it.hasNext(); ) {
            Drawable drawable = it.next();
            drawable.draw(drawables);
        }

        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field);
    }

    public void actDay(Actor actor, List<Actor> newActors) {
        if (actor.getIsNocturnal()) {
            actor.act(newActors);
        }

        if (actor.isRaining()) {
            actor.act(newActors);
        }
    }

    public void actNight (Actor actor, List<Actor> newActors) {
        if (!actor.getIsNocturnal()) {
            actor.act(newActors);
        }

    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    actors.add(eagle);
                } else if(rand.nextDouble() <= SCORPION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Scorpion scorpion = new Scorpion(true, field, location);
                    actors.add(scorpion);
                    // else leave the location empty.
                } else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location);
                    actors.add(snake);
                    // else leave the location empty.
                } else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location);
                    actors.add(mouse);
                    // else leave the location empty.
                } else if(rand.nextDouble() <= CRICKET_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cricket cricket = new Cricket(true, field, location);
                    actors.add(cricket);
                    // else leave the location empty.
                } else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location);
                    actors.add(plant);
                    // else leave the location empty.
                }
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
