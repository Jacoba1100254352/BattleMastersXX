package Scene.support;

import Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NPCSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private NPCSystem() {
	}

	public static List<NPC> getNPCsForLocation(String location) {
		List<NPC> npcs = new ArrayList<>();
		switch (location) {
			case "Town Square" -> npcs.add(new NPC("Elder Rowan", location, NPC.Role.QUEST_GIVER));
			case "Magic Shop" -> npcs.add(new NPC("Arcanist Lyra", location, NPC.Role.TRAINER));
			case "Blacksmith" -> npcs.add(new NPC("Forge Master Duran", location, NPC.Role.SCOUT));
			case "Hospital" -> npcs.add(new NPC("Sister Mirren", location, NPC.Role.HEALER));
			case "Stable" -> npcs.add(new NPC("Caretaker Elin", location, NPC.Role.COMPANION_HANDLER));
			default -> { }
		}
		return npcs;
	}

	public static void interactWithNPCs(Player player, List<NPC> npcs) {
		if (npcs.isEmpty()) {
			System.out.println("No notable NPCs are present right now.");
			return;
		}
		System.out.println("NPCs in the area:");
		for (int i = 0; i < npcs.size(); i++) {
			System.out.printf("%d) %s%n", i + 1, npcs.get(i).getName());
		}
		System.out.print("Talk to who? (number or 'back'): ");
		String choice = SCANNER.nextLine().trim();
		if (choice.equalsIgnoreCase("back")) {
			return;
		}
		try {
			int index = Integer.parseInt(choice) - 1;
			if (index >= 0 && index < npcs.size()) {
				npcs.get(index).interact(player);
			} else {
				System.out.println("They don't seem to be here.");
			}
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number.");
		}
	}
}
