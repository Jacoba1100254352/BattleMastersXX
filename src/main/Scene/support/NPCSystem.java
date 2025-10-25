package Scene.support;

import Player.Player;

import java.util.ArrayList;
import java.util.List;

public class NPCSystem {
	private NPCSystem() {
	}

	public static List<NPC> getNPCsForLocation(String location) {
		List<NPC> npcs = new ArrayList<>();
		switch (location) {
			case "Town Square" -> npcs.add(new NPC("Elder Rowan", location));
			case "Magic Shop" -> npcs.add(new NPC("Arcanist Lyra", location));
			case "Blacksmith" -> npcs.add(new NPC("Forge Master Duran", location));
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
			System.out.printf("%d. %s%n", i + 1, npcs.get(i).getName());
		}
		// For now automatically interact with the first NPC
		npcs.get(0).interact(player);
	}
}
