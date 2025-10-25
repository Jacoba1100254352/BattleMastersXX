package Systems;

import Player.Player;
import Scene.GameWorld;
import Scene.TimeWeatherSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

public final class SaveSystem {
	private static final Path SAVE_DIR = Paths.get("saves");
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	private SaveSystem() {
	}

	public static void quickSave(Player player, GameWorld gameWorld, TimeWeatherSystem timeWeather) {
		try {
			Files.createDirectories(SAVE_DIR);
			Player.Snapshot snapshot = player.createSnapshot();
			TimeWeatherSystem.Snapshot timeSnapshot = timeWeather.createSnapshot();
			Properties props = buildProperties(snapshot, timeSnapshot);
			String safeName = snapshot.playerName.replaceAll("\\s+", "_");
			Path file = SAVE_DIR.resolve(safeName + "_" + FORMATTER.format(LocalDateTime.now()) + ".properties");
			try (BufferedWriter writer = Files.newBufferedWriter(file)) {
				props.store(writer, "BattleMastersXX save");
			}
			System.out.println("Game saved to " + file);
		} catch (IOException ex) {
			System.out.println("Failed to save the game: " + ex.getMessage());
		}
	}

	public static SaveData load(Path file) throws IOException {
		Properties props = new Properties();
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			props.load(reader);
		}
		Player.Snapshot playerSnapshot = parsePlayerSnapshot(props);
		TimeWeatherSystem.Snapshot timeSnapshot = parseTimeSnapshot(props);
		return new SaveData(playerSnapshot, timeSnapshot);
	}

	public static List<Path> listSaves() throws IOException {
		if (!Files.exists(SAVE_DIR)) {
			return List.of();
		}
		List<Path> files = new ArrayList<>();
		try (var stream = Files.list(SAVE_DIR)) {
			stream.filter(path -> path.getFileName().toString().endsWith(".properties"))
				.sorted()
				.forEach(files::add);
		}
		return files;
	}

	public static void delete(Path file) throws IOException {
		Files.deleteIfExists(file);
	}

	private static Properties buildProperties(Player.Snapshot snapshot, TimeWeatherSystem.Snapshot timeSnapshot) {
		Properties props = new Properties();
		props.setProperty("name", snapshot.playerName);
		props.setProperty("level", Integer.toString(snapshot.level));
		props.setProperty("exp", Integer.toString(snapshot.exp));
		props.setProperty("hp", Integer.toString(snapshot.hp));
		props.setProperty("maxHp", Integer.toString(snapshot.maxHp));
		props.setProperty("mana", Integer.toString(snapshot.mana));
		props.setProperty("maxMana", Integer.toString(snapshot.maxMana));
		props.setProperty("stamina", Integer.toString(snapshot.stamina));
		props.setProperty("maxStamina", Integer.toString(snapshot.maxStamina));
		props.setProperty("prestige", Integer.toString(snapshot.prestige));
		props.setProperty("gold", Integer.toString(snapshot.gold));
		props.setProperty("totalGoldEarned", Integer.toString(snapshot.totalGoldEarned));
		props.setProperty("reputation", Integer.toString(snapshot.reputation));
		props.setProperty("skillPoints", Integer.toString(snapshot.skillPoints));
		props.setProperty("enemiesDefeated", Integer.toString(snapshot.enemiesDefeated));
		props.setProperty("questsCompleted", Integer.toString(snapshot.questsCompleted));
		props.setProperty("location", snapshot.location);
		props.setProperty("domain", snapshot.currentDomain);
		props.setProperty("mythicMode", Boolean.toString(snapshot.mythicMode));
		props.setProperty("equippedWeapon", snapshot.equippedWeaponName == null ? "" : snapshot.equippedWeaponName);
		props.setProperty("equippedArmor", snapshot.equippedArmorName == null ? "" : snapshot.equippedArmorName);
		props.setProperty("horseName", snapshot.horseName == null ? "" : snapshot.horseName);
		props.setProperty("horseSpeed", Double.toString(snapshot.horseSpeed));
		props.setProperty("inventory", join(snapshot.inventoryNames));
		props.setProperty("skills", joinMap(snapshot.skillLevels));
		props.setProperty("spells", join(snapshot.knownSpells));
		props.setProperty("achievements", join(snapshot.achievements));
		props.setProperty("companions", join(snapshot.companionNames));
		props.setProperty("time.totalMinutes", Integer.toString(timeSnapshot.totalMinutes()));
		props.setProperty("time.weather", timeSnapshot.currentWeather());
		props.setProperty("time.hoursUntilChange", Integer.toString(timeSnapshot.hoursUntilWeatherChange()));
		return props;
	}

	private static Player.Snapshot parsePlayerSnapshot(Properties props) {
		String name = props.getProperty("name", "Hero");
		int level = Integer.parseInt(props.getProperty("level", "1"));
		int exp = Integer.parseInt(props.getProperty("exp", "0"));
		int hp = Integer.parseInt(props.getProperty("hp", "100"));
		int maxHp = Integer.parseInt(props.getProperty("maxHp", "100"));
		int mana = Integer.parseInt(props.getProperty("mana", "50"));
		int maxMana = Integer.parseInt(props.getProperty("maxMana", "50"));
		int stamina = Integer.parseInt(props.getProperty("stamina", "100"));
		int maxStamina = Integer.parseInt(props.getProperty("maxStamina", "100"));
		int prestige = Integer.parseInt(props.getProperty("prestige", "0"));
		int gold = Integer.parseInt(props.getProperty("gold", "0"));
		int totalGold = Integer.parseInt(props.getProperty("totalGoldEarned", "0"));
		int reputation = Integer.parseInt(props.getProperty("reputation", "0"));
		int skillPoints = Integer.parseInt(props.getProperty("skillPoints", "0"));
		int enemiesDefeated = Integer.parseInt(props.getProperty("enemiesDefeated", "0"));
		int questsCompleted = Integer.parseInt(props.getProperty("questsCompleted", "0"));
		String location = props.getProperty("location", "Town Square");
		String domain = props.getProperty("domain", "Central Domain");
		boolean mythicMode = Boolean.parseBoolean(props.getProperty("mythicMode", "false"));
		List<String> inventory = split(props.getProperty("inventory", ""));
		Map<String, Integer> skills = splitMap(props.getProperty("skills", ""));
		List<String> spells = split(props.getProperty("spells", ""));
		List<String> achievements = split(props.getProperty("achievements", ""));
		List<String> companions = split(props.getProperty("companions", ""));
		String weapon = emptyToNull(props.getProperty("equippedWeapon", ""));
		String armor = emptyToNull(props.getProperty("equippedArmor", ""));
		String horseName = emptyToNull(props.getProperty("horseName", ""));
		double horseSpeed = Double.parseDouble(props.getProperty("horseSpeed", "1.0"));
		return new Player.Snapshot(name, level, exp, hp, maxHp, mana, maxMana, stamina, maxStamina,
			prestige, gold, totalGold, reputation, skillPoints, enemiesDefeated, questsCompleted,
			location, domain, mythicMode, inventory, skills, spells, achievements, companions,
			weapon, armor, horseName, horseSpeed);
	}

	private static TimeWeatherSystem.Snapshot parseTimeSnapshot(Properties props) {
		int minutes = Integer.parseInt(props.getProperty("time.totalMinutes", "480"));
		String weather = props.getProperty("time.weather", "Clear skies");
		int hours = Integer.parseInt(props.getProperty("time.hoursUntilChange", "3"));
		return new TimeWeatherSystem.Snapshot(minutes, weather, hours);
	}

	private static String join(List<String> values) {
		StringJoiner joiner = new StringJoiner("|");
		for (String value : values) {
			if (value != null && !value.isEmpty()) {
				joiner.add(value.replace("|", "/"));
			}
		}
		return joiner.toString();
	}

	private static String joinMap(Map<String, Integer> map) {
		StringJoiner joiner = new StringJoiner("|");
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			joiner.add(entry.getKey().replace("|", "/") + ":" + entry.getValue());
		}
		return joiner.toString();
	}

	private static List<String> split(String value) {
		if (value == null || value.isBlank()) {
			return List.of();
		}
		String[] parts = value.split("\\|");
		List<String> list = new ArrayList<>();
		for (String part : parts) {
			if (!part.isBlank()) {
				list.add(part);
			}
		}
		return list;
	}

	private static Map<String, Integer> splitMap(String value) {
		Map<String, Integer> map = new java.util.HashMap<>();
		if (value == null || value.isBlank()) {
			return map;
		}
		String[] parts = value.split("\\|");
		for (String part : parts) {
			String[] pair = part.split(":");
			if (pair.length == 2) {
				try {
					map.put(pair[0], Integer.parseInt(pair[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}
		return map;
	}

	private static String emptyToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	public record SaveData(Player.Snapshot playerSnapshot, TimeWeatherSystem.Snapshot timeSnapshot) { }
}
