package Quests;

import Items.Item;
import Items.ItemFactory;
import Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class WanderingMerchant {
	private static final Scanner SCANNER = new Scanner(System.in);

	public void interact(Player player) {
		System.out.println("A wandering merchant sets up a colourful stall. " +
				"\"Care to trade, " + player.getName() + "?\"");
		List<MerchantItem> wares = generateWares(player.getLevel());
		boolean trading = true;
		while (trading) {
			System.out.println("\n--- Merchant Goods ---");
			int discount = Math.min(30, player.getReputation() / 5);
			for (int i = 0; i < wares.size(); i++) {
				MerchantItem mi = wares.get(i);
				int price = Math.max(20, mi.price - discount);
				System.out.printf("%d) %s (%s) - %d gold%n", i + 1, mi.item.getName(), mi.item.getType(), price);
			}
			System.out.println("B) Sell an item\nL) Leave");
			System.out.print("Your choice: ");
			String choice = SCANNER.nextLine().trim().toLowerCase();
			switch (choice) {
				case "l", "leave" -> {
					System.out.println("The merchant tips his hat and moves on.");
					trading = false;
				}
				case "b" -> sellItem(player);
				default -> handlePurchase(choice, player, wares, discount);
			}
		}
	}

	private void handlePurchase(String choice, Player player, List<MerchantItem> wares, int discount) {
		try {
			int index = Integer.parseInt(choice) - 1;
			if (index < 0 || index >= wares.size()) {
				System.out.println("That item isn't on display.");
				return;
			}
			MerchantItem selection = wares.get(index);
			int finalPrice = Math.max(20, selection.price - discount);
			if (player.getGold() < finalPrice) {
				System.out.println("You don't have enough gold.");
				return;
			}
			player.subtractGold(finalPrice);
			player.addToInventory(selection.item);
			System.out.println("You purchase " + selection.item.getName() + " for " + finalPrice + " gold.");
			// Remove purchased item and generate a new one to keep stock fresh
			wares.remove(index);
			wares.add(generateSingleItem(player.getLevel()));
		} catch (NumberFormatException ex) {
			System.out.println("The merchant doesn't understand that request.");
		}
	}

	private void sellItem(Player player) {
		List<Item> inventory = new ArrayList<>(player.getInventory());
		if (inventory.isEmpty()) {
			System.out.println("You have nothing to sell.");
			return;
		}
		System.out.println("Items you can sell:");
		for (int i = 0; i < inventory.size(); i++) {
			Item item = inventory.get(i);
			System.out.printf("%d) %s%n", i + 1, item.getName());
		}
		System.out.print("Sell which item? (number or 'back'): ");
		String choice = SCANNER.nextLine().trim().toLowerCase();
		if (choice.equals("back")) return;
		try {
			int index = Integer.parseInt(choice) - 1;
			if (index < 0 || index >= inventory.size()) {
				System.out.println("Not a valid item.");
				return;
			}
			Item item = inventory.get(index);
			int value = Math.max(20, item.getValue() / 2);
			player.removeFromInventory(item);
			player.addGold(value);
			System.out.println("Sold " + item.getName() + " for " + value + " gold.");
		} catch (NumberFormatException ex) {
			System.out.println("The merchant shrugs, confused.");
		}
	}

	private List<MerchantItem> generateWares(int playerLevel) {
		List<MerchantItem> items = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			items.add(generateSingleItem(playerLevel));
		}
		return items;
	}

	private MerchantItem generateSingleItem(int playerLevel) {
		double roll = ThreadLocalRandom.current().nextDouble();
		Item item;
		int basePrice;
		if (roll < 0.4) {
			item = ItemFactory.createHealthPotion("Traveler's Tonic", 35 + playerLevel * 2);
			basePrice = 150;
		} else if (roll < 0.7) {
			item = ItemFactory.createManaPotion("Arcane Brew", 25 + playerLevel * 2);
			basePrice = 170;
		} else {
			item = ItemFactory.createRandomTreasure(playerLevel);
			basePrice = 190;
		}
		basePrice += ThreadLocalRandom.current().nextInt(-20, 40);
		return new MerchantItem(item, Math.max(60, basePrice));
	}

	private record MerchantItem(Item item, int price) { }
}
