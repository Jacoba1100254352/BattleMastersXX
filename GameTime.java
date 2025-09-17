 // ============== GameTime.java ==============
 
import java.util.Random;

public class GameTime {
    private int hour = 6; // Start at 6 AM
    private int day = 1;
    private Weather currentWeather;
    private Random random = new Random();
    
    public GameTime() {
        this.currentWeather = new Weather();
    }
    
    public void advanceTime(int hours) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.hour %= 24;
            this.day++;
            maybeChangeWeather();
        }
    }
    
    public String getTimeOfDay() {
        if (hour >= 6 && hour < 12) return "Morning";
        else if (hour >= 12 && hour < 18) return "Afternoon";
        else if (hour >= 18 && hour < 21) return "Evening";
        else return "Night";
    }
    
    private void maybeChangeWeather() {
        if (currentWeather.getDuration() <= 0 || random.nextDouble() < 0.3) {
            currentWeather.changeWeather();
            
            // Special weather announcements
            String weatherType = currentWeather.getType();
            if (weatherType.equals("Aurora") || weatherType.equals("Eclipse") || 
                weatherType.equals("Meteor Shower")) {
                System.out.println("\n*** SPECIAL WEATHER EVENT: " + weatherType + "! ***");
            } else if (weatherType.equals("Toxic Rain") || weatherType.equals("Acid Fog") || 
                      weatherType.equals("Flame Storm")) {
                System.out.println("\n*** DANGEROUS WEATHER: " + weatherType + "! Be careful! ***");
            }
        } else {
            currentWeather.decreaseDuration();
        }
    }
    
    // Getters
    public int getHour() { return hour; }
    public int getDay() { return day; }
    public Weather getCurrentWeather() { return currentWeather; }
}