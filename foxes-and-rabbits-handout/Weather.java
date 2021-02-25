import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Weather

{

    public Stack<String> weatherRecord;
    
    private Random rand;
    
    public double stateProbability;

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

    public void setWeatherState()
    {
        weatherRecord = new Stack<String>();
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

    protected String checkLastWeather()
    {
        String weatherCheck = weatherRecord.peek();
        return weatherCheck;
    }

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

    protected void reset() {
        while (weatherRecord.empty() == false) {
            weatherRecord.pop();
        }
    }

}
