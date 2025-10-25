package Quests;

import Items.Item;
import Items.ItemFactory;
import Player.Player;

import java.util.List;
import java.util.Scanner;

public class WanderingMerchant {
	private static final Scanner SCANNER = new Scanner(System.in);

	public void interact(Player player) {
		System.out.println("A wandering merchant sets up a small stall.");
		List<Item> wares = List.of(
			ItemFactory.createHealthPotion("Traveler's Tonic", 40),
			ItemFactory.createManaPotion("Arcane Brew", 30),
			ItemFactory.createWeapon("Gilded Dagger", 45, "physical")
		);
		int price = 150;
		for (int i = 0; i < wares.size(); i++) {
			Item item = wares.get(i);
			System.out.printf("%d. %s (Type: %s, Price: %d gold)%n", i + 1, item.getName(), item.getType(), price + i * 50);
		}
		System.out.print("Buy which item? (number or 'leave'): ");
		String choice = SCANNER.nextLine().trim().toLowerCase();
		if (choice.equals("leave")) {
			System.out.println("You decline the offer for now.");
			return;
		}
		try {
			int selection = Integer.parseInt(choice) - 1;
			if (selection >= 0 && selection < wares.size()) {
				int cost = price + selection * 50;
				if (player.getGold() >= cost) {
					player.subtractGold(cost);
					player.addToInventory(wares.get(selection));
					System.out.println("Pleasure doing business!");
				} else {
					System.out.println("You don't have enough gold.");
				}
			} else {
				System.out.println("The merchant doesn't have that item.");
			}
		} catch (NumberFormatException ex) {
			System.out.println("The merchant can't decipher that request.");
		}
	}
}
