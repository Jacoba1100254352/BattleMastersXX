package Systems;

import Player.Player;

import java.util.Map;
import java.util.Scanner;

public final class SkillSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private SkillSystem() {
	}

	public static void showSkillMenu(Player player) {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Skills ===");
			for (Map.Entry<String, Integer> entry : player.getSkills().entrySet()) {
				System.out.printf("%s: %d%n", entry.getKey(), entry.getValue());
			}
			System.out.println("Skill points available: " + player.getSkillPoints());
			System.out.println("1) Train skill\n2) Back");
			System.out.print("Choose: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> trainSkill(player);
				case "2", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	private static void trainSkill(Player player) {
		if (player.getSkillPoints() <= 0) {
			System.out.println("You need more skill points. Level up to earn them.");
			return;
		}
		System.out.print("Train which skill? (Combat/Magic/Crafting/Gathering): ");
		String skill = capitalize(SCANNER.nextLine().trim());
		if (!player.getSkills().containsKey(skill)) {
			System.out.println("Unknown skill.");
			return;
		}
		System.out.print("Spend how many points (1-" + player.getSkillPoints() + ")? ");
		try {
			int points = Integer.parseInt(SCANNER.nextLine().trim());
			if (points <= 0 || points > player.getSkillPoints()) {
				System.out.println("Invalid amount.");
				return;
			}
			player.trainSkill(skill, points);
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number.");
		}
	}

	private static String capitalize(String value) {
		if (value.isEmpty()) return value;
		return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}
}
