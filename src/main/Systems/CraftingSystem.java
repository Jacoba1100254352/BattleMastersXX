package Systems;

import Items.Item;
import Items.ItemFactory;
import Items.Material;
import Player.Player;

import java.util.List;
import java.util.Scanner;

public final class CraftingSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private CraftingSystem() {
	}

	public static void showCraftingMenu(Player player) {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Workshop ===");
			long materials = player.getItemsByType(Material.class).size();
			System.out.println("Materials available: " + materials);
			System.out.println("1) Brew potion (1 material)\n2) Forge weapon (3 materials)\n3) Forge armor (3 materials)\n4) Back");
			System.out.print("Choose: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> brewPotion(player);
				case "2" -> forgeWeapon(player);
				case "3" -> forgeArmor(player);
				case "4", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	private static void brewPotion(Player player) {
		if (!player.removeItemsByType(Material.class, 1)) {
			System.out.println("You need at least one crafting material.");
			return;
		}
		Item potion = ItemFactory.createHealthPotion("Crafted Health Potion", 60);
		player.addToInventory(potion);
		player.gainSkillExperience("Crafting", 20);
	}

	private static void forgeWeapon(Player player) {
		if (!player.removeItemsByType(Material.class, 3)) {
			System.out.println("You need three materials to forge a weapon.");
			return;
		}
		int level = player.getLevel();
		Item weapon = ItemFactory.createRandomWeapon(level, "physical");
		player.addToInventory(weapon);
		player.gainSkillExperience("Crafting", 40);
	}

	private static void forgeArmor(Player player) {
		if (!player.removeItemsByType(Material.class, 3)) {
			System.out.println("You need three materials to forge armor.");
			return;
		}
		Item armor = ItemFactory.createArmor("Crafted Armor", 40 + player.getLevel() * 2, "Medium");
		player.addToInventory(armor);
		player.gainSkillExperience("Crafting", 40);
	}
}
