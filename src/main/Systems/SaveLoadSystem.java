package Systems;

import Player.Player;
import Scene.GameWorld;
import Scene.TimeWeatherSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public final class SaveLoadSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private SaveLoadSystem() {
	}

	public static void showSaveLoadMenu(Player player, GameWorld gameWorld, TimeWeatherSystem timeWeather) {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Save/Load ===");
			System.out.println("1) Save game\n2) Load game\n3) Delete save\n4) Back");
			System.out.print("Choose: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> SaveSystem.quickSave(player, gameWorld, timeWeather);
				case "2" -> loadFlow(player, timeWeather);
				case "3" -> deleteFlow();
				case "4", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	private static void loadFlow(Player player, TimeWeatherSystem timeWeather) {
		try {
			List<Path> saves = SaveSystem.listSaves();
			if (saves.isEmpty()) {
				System.out.println("No save files found.");
				return;
			}
			Path choice = promptForSave(saves, "Load which save?");
			if (choice == null) return;
			SaveSystem.SaveData data = SaveSystem.load(choice);
			player.restoreFromSnapshot(data.playerSnapshot());
			timeWeather.restoreFromSnapshot(data.timeSnapshot());
			System.out.println("Game loaded from " + choice.getFileName());
		} catch (IOException ex) {
			System.out.println("Failed to load: " + ex.getMessage());
		}
	}

	private static void deleteFlow() {
		try {
			List<Path> saves = SaveSystem.listSaves();
			if (saves.isEmpty()) {
				System.out.println("No save files to delete.");
				return;
			}
			Path choice = promptForSave(saves, "Delete which save?");
			if (choice == null) return;
			SaveSystem.delete(choice);
			System.out.println("Deleted " + choice.getFileName());
		} catch (IOException ex) {
			System.out.println("Failed to delete save: " + ex.getMessage());
		}
	}

	private static Path promptForSave(List<Path> saves, String prompt) {
		for (int i = 0; i < saves.size(); i++) {
			System.out.printf("%d) %s%n", i + 1, saves.get(i).getFileName());
		}
		System.out.print(prompt + " (number or 'cancel'): ");
		String input = SCANNER.nextLine().trim();
		if (input.equalsIgnoreCase("cancel")) {
			return null;
		}
		try {
			int index = Integer.parseInt(input) - 1;
			if (index >= 0 && index < saves.size()) {
				return saves.get(index);
			}
		} catch (NumberFormatException ignored) {
		}
		System.out.println("Invalid selection.");
		return null;
	}
}
