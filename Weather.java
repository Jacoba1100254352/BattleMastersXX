// ============== Weather.java ==============

import java.util.Random;

public class Weather {
    private static final String[] WEATHER_STATES = {
        "Clear", "Rain", "Storm", "Fog", "Snow", "Sunny", "Windy", "Blizzard", 
        "Sandstorm", "Thunderstorm", "Hail", "Tornado", "Aurora", "Eclipse", 
        "Meteor Shower", "Toxic Rain", "Acid Fog", "Flame Storm", "Ice Storm"
    };
    
    private String type = "Clear";
    private int duration = 0;
    private Random random = new Random();
    
    public void changeWeather() {
        this.type = WEATHER_STATES[random.nextInt(WEATHER_STATES.length)];
        this.duration = random.nextInt(3) + 1;
    }
    
    public void decreaseDuration() {
        this.duration--;
    }
    
    // Getters
    public String getType() { return type; }
    public int getDuration() { return duration; }
}