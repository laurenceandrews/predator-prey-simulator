import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Eagles, Scorpions, Snakes, Rabbits, Crickets and Plants.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // The probability that an eagle will be created in any given grid position.
   private static final double EAGLE_CREATION_PROBABILITY = 0.01;
    
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.025;

    // The probability that a scorpion will be created in any given grid position.
    private static final double SCORPION_CREATION_PROBABILITY = 0.025;

    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.04;
    
    // The probability that a cricket will be created in any given grid position.
    private static final double CRICKET_CREATION_PROBABILITY = 0.05;
    
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.06;
    
    // List of animals in the field.
    private List<Actor> actors;
    
    // The current state of the field.
    private Field field;
    
    // The current step of the simulation.
    private int step;
    
    // A graphical view of the simulation.
    private SimulatorView view;

    
    private Weather weather;

    private String currentWeather;

    private boolean isDay;

    /**
     * Construct a simulation field with default size and a Weather 
     * system.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
        weather = new Weather();
    }

    /**
     * Create a simulation field with the given size and a weather 
     * system, as well as assinging colours to entities of the field.
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
        weather = new Weather();

        actors = new ArrayList<>();

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
     * (1500 steps).
     */
    public void runLongSimulation()
    {
        simulate(1500);
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

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * actor as well as changing the weather and the day/night cycle.
     * Nocturnal actors act during the night, the others act during
     * the night.
     */
    public void simulateOneStep()
    {
        step++;
        dayOrNight();
        weather.setWeatherState();
        currentWeather = weather.checkLastWeather();
        plantRegrowth();
        
        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();

        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if (!fogStopAction(actor)) {
                if (!isDay) {
                    actNight(actor, newActors);
                } else {
                    actDay(actor, newActors);
                }
            }
            if(!actor.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born actors to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field, isDay, currentWeather);
    }
    

    /**
     * A getter to return the current steps.
     * @return the current step of the system.
     */
    public int getSteps() {
        return step;
    }

    /**
     * Set the current day of the simulation.
     * @param isDay the curretn day of the simulation.
     */
    public void setDay(boolean isDay) {
        this.isDay = isDay;
    }

    /**
     * Change simulation to day if steps are even, else set to night. 
     */
    public void dayOrNight () {
        if (step % 2 == 0) {
            setDay(true);
        } else {
            setDay(false);
        }
    }

    /**
     * If the current weather is sunny and the past day was raining 
     * we assume plants would be able to grow, therefore they fill
     * available spaces within the simulation.
     */
    public void plantRegrowth()
    {
        Random rand = Randomizer.getRandom();
        if (weather.twoDayReport().equals("RainSun")) {
            for(int row = 0; row < field.getDepth(); row++) {
                for(int col = 0; col < field.getWidth(); col++) {
                    if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY){ 
                        Location location = new Location(row, col);
                        Plant plant = new Plant(true, field, location);
                        actors.add(plant);
                    }
                }
            }
        }
    }

    /**
     * If the current weather is foggy and an actor is a predator,
     * they will not act for that step.
     * @param actor An actor within the simulation
     * @return A boolean value of whether an action should cease.
     */
    public boolean fogStopAction(Actor actor)
    {
        Random rand = Randomizer.getRandom();
        if (weather.checkLastWeather().equals("Fog") && actor instanceof Predator) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Non-nocturnal actors act for that step.
     * @param actor An actor within the simulation
     * @param newActors A list of the new actors within the simulation.
     */
    public void actDay(Actor actor, List<Actor> newActors) {
        if (!actor.getIsNocturnal()) {
            actor.act(newActors);
        }
    }

    /**
     * Nocturnal actors act for that step.
     * @param actor An actor within the simulation
     * @param newActors A list of the new actors within the simulation.
     */
    public void actNight (Actor actor, List<Actor> newActors) {
        if (actor.getIsNocturnal()) {
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
        view.showStatus(step, field, isDay, currentWeather);
    }

    /**
     * Randomly populate the field with drawable entities.
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
