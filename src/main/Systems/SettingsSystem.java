package Systems;

import java.util.Scanner;

public final class SettingsSystem {
	private static final Scanner SCANNER = new Scanner(System.in);
	private static boolean autoSaveEnabled = true;
	private static boolean detailedCombatLog = true;
	private static int textSpeed = 1; // 0=slow,1=normal,2=fast

	private SettingsSystem() {
	}

	public static void showSettingsMenu() {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Settings ===");
			System.out.println("1) Toggle autosave (currently " + (autoSaveEnabled ? "ON" : "OFF") + ")");
			System.out.println("2) Toggle detailed combat log (currently " + (detailedCombatLog ? "ON" : "OFF") + ")");
			System.out.println("3) Change text speed (currently " + speedLabel(textSpeed) + ")");
			System.out.println("4) Back");
			System.out.print("Choose: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> autoSaveEnabled = !autoSaveEnabled;
				case "2" -> detailedCombatLog = !detailedCombatLog;
				case "3" -> cycleSpeed();
				case "4", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	public static boolean isAutoSaveEnabled() {
		return autoSaveEnabled;
	}

	public static boolean isDetailedCombatLog() {
		return detailedCombatLog;
	}

	public static int getTextSpeed() {
		return textSpeed;
	}

	private static void cycleSpeed() {
		textSpeed = (textSpeed + 1) % 3;
		System.out.println("Text speed set to " + speedLabel(textSpeed));
	}

	private static String speedLabel(int speed) {
		return switch (speed) {
			case 0 -> "Slow";
			case 1 -> "Normal";
			case 2 -> "Fast";
			default -> "Unknown";
		};
	}
}
