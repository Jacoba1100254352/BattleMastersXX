package Factions;

import Player.Faction;
import Player.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class FactionSystem {
	private static final Map<String, Faction> FACTIONS = new LinkedHashMap<>();
	private static final Scanner SCANNER = new Scanner(System.in);
	private static boolean initialized = false;

	private FactionSystem() {
	}

	public static void initialize() {
		if (initialized) return;
		initialized = true;
		FACTIONS.put("Knights of the Phoenix", new Faction(
			"Knights of the Phoenix", 50, 50, java.util.List.of("Phoenix Strike")));
		FACTIONS.put("Circle of Mages", new Faction(
			"Circle of Mages", 30, 20, java.util.List.of("Arcane Surge")));
		FACTIONS.put("Shadow Syndicate", new Faction(
			"Shadow Syndicate", 40, 80, java.util.List.of("Shadowstep")));
	}

	public static void showFactionMenu(Player player) {
		if (!initialized) initialize();

		System.out.println("\n=== Faction Council ===");
		if (player.getFaction() != null) {
			System.out.println("Current faction: " + player.getFaction().getName());
			System.out.println("Exp Bonus: " + player.getFaction().getExpBonus());
			System.out.println("Gold Bonus: " + player.getFaction().getGoldBonus());
			System.out.println("Abilities: " + player.getFaction().getCombatAbilities());
			System.out.println("Leave current faction? (yes/no): ");
			String choice = SCANNER.nextLine().trim().toLowerCase();
			if (choice.startsWith("y")) {
				player.setFaction(null);
				System.out.println("You leave your current faction.");
			}
		}

		System.out.println("Available factions:");
		int index = 1;
		for (Map.Entry<String, Faction> entry : FACTIONS.entrySet()) {
			Faction faction = entry.getValue();
			System.out.printf("%d. %s (EXP +%d, Gold +%d)%n", index++, faction.getName(), faction.getExpBonus(), faction.getGoldBonus());
		}
		System.out.print("Join which faction? (number or 'back'): ");
		String selection = SCANNER.nextLine().trim().toLowerCase();
		if ("back".equals(selection)) return;
		try {
			int chosen = Integer.parseInt(selection);
			if (chosen >= 1 && chosen <= FACTIONS.size()) {
				Faction faction = FACTIONS.values().stream().skip(chosen - 1L).findFirst().orElse(null);
				if (faction != null) {
					player.setFaction(faction);
					System.out.println("Joined faction: " + faction.getName());
				}
			}
		} catch (NumberFormatException ignored) {
			System.out.println("Invalid selection.");
		}
	}
}
