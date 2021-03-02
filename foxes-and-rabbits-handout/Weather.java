import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * A class representing the weather system of the simulation.
 * 
 * @author Benedict Morley and Laurence Andrews
 * @version (2)
 */
public class Weather

{

    // A stack to keep track of past weathers
    public Stack<String> weatherRecord;
    
    private Random rand;
    
    public double stateProbability;

    // The probability that fog will occur.
    private static final double FOG_PROBABILITY = 0.1;

    // The probability that rain will occur.
    private static final double RAIN_PROBABILITY = 0.3;

    // The probability that sun will occur.
    private static final double SUN_PROBABILITY = 1;
    

    /**
     * Construct a weather system. The initial state of the simulation 
     * is "sun".
     */
    public Weather()
    { 
        weatherRecord = new Stack<String>();
        weatherRecord.push("Sun");
    }

    /**
     * Set the current weather state of the system depending
     * on a random value that is generated.
     */
    public void setWeatherState()
    {
        rand = Randomizer.getRandom();
        stateProbability = rand.nextDouble();
        if (stateProbability < FOG_PROBABILITY) {
            weatherRecord.push("Fog");
        } else if (stateProbability < RAIN_PROBABILITY) {
            weatherRecord.push("Rain");
        } else {
            weatherRecord.push("Sun");
        }
    }

    /**
     * Check the top entity of the stack of past weathers.
     * @return The latest weather to occur.
     */
    protected String checkLastWeather()
    {
        String weatherCheck = weatherRecord.peek();
        return weatherCheck;
    }

    /**
     * Check the past weather, if it is sunny then pop that from 
     * the top of the stack and check the next weather then push 
     * the current weather back again.
     * @return The past two days weather as a concatenated string.
     */
    protected String twoDayReport()
    {
        String today = checkLastWeather();
        String yesterday = "";
        if (today.equals("Sun")) {
            weatherRecord.pop();
            yesterday = checkLastWeather();
            weatherRecord.push(today);
        }

        return (yesterday + today);
    }

    /**
     * Reset the stack of weathers by popping all current values
     * within it.
     */
    protected void reset() {
        while (weatherRecord.empty() == false) {
            weatherRecord.pop();
        }
    }

}
