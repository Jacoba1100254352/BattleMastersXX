package Scene;

import Player.Player;

import java.util.List;
import java.util.Random;

/**
 * Tracks in-game time and dynamically evolving weather.
 */
public class TimeWeatherSystem {
	private final Random random = new Random();
	private int totalMinutes;
	private String currentWeather;
	private int hoursUntilWeatherChange;
	
	private static final List<String> WEATHER_TYPES = List.of(
		"Clear skies", "Overcast", "Light rain", "Heavy rain",
		"Thunderstorm", "Foggy", "Snowfall", "Blizzard", "Sunny"
	);
	
	public TimeWeatherSystem() {
		this.totalMinutes = 8 * 60; // Start at 8:00
		this.currentWeather = rollWeather();
		this.hoursUntilWeatherChange = 3 + random.nextInt(4);
	}
	
	public void advanceTime(int hours) {
		if (hours <= 0) return;
		totalMinutes += hours * 60;
		hoursUntilWeatherChange -= hours;
		
		if (hoursUntilWeatherChange <= 0) {
			currentWeather = rollWeather();
			hoursUntilWeatherChange = 3 + random.nextInt(4);
		}
	}
	
	public int getTotalHours() {
		return totalMinutes / 60;
	}
	
	public String getCurrentTimeString() {
		int day = totalMinutes / (24 * 60) + 1;
		int minutesOfDay = totalMinutes % (24 * 60);
		int hours = minutesOfDay / 60;
		int minutes = minutesOfDay % 60;
		String period = hours < 12 ? "AM" : "PM";
		int displayHour = hours % 12;
		if (displayHour == 0) displayHour = 12;
		return String.format("Day %d - %02d:%02d %s", day, displayHour, minutes, period);
	}
	
	public String getCurrentWeather() {
		return currentWeather;
	}

	private String rollWeather() {
		return WEATHER_TYPES.get(random.nextInt(WEATHER_TYPES.size()));
	}

	public String getTotalTimeString() {
		int days = totalMinutes / (24 * 60);
		int hours = (totalMinutes / 60) % 24;
		int minutes = totalMinutes % 60;
		return String.format("%d days %02d:%02d hours", days, hours, minutes);
	}

	public void triggerRandomWeatherEvent(Player player) {
		System.out.println("A sudden change in weather affects the surroundings...");
		if ("Heavy rain".equals(currentWeather) || "Thunderstorm".equals(currentWeather)) {
			player.addStatusEffect("soaked", new WeatherDebuffEffect());
		} else if ("Sunny".equals(currentWeather) || "Clear skies".equals(currentWeather)) {
			player.restoreStamina(5);
			System.out.println("The pleasant weather rejuvenates you slightly.");
		}
	}

	private static class WeatherDebuffEffect extends Effects.StatusEffect {
		WeatherDebuffEffect() {
			super("Soaked", 2, 0, false);
		}

		@Override
		public void apply(Object target) {
			if (target instanceof Player player) {
				player.consumeStamina(5);
				System.out.println("The rain saps your stamina.");
			}
		}
	}
}
