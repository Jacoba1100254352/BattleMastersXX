package Scene.support;

import Items.Item;
import Player.Player;

import java.util.List;
import java.util.Scanner;

public class ShopSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private ShopSystem() {
	}

	public static void showShopsInArea(Player player, List<Shop> shops) {
		if (shops.isEmpty()) {
			System.out.println("There are no shops open here.");
			return;
		}
		System.out.println("Shops available:");
		for (int i = 0; i < shops.size(); i++) {
			System.out.printf("%d) %s%n", i + 1, shops.get(i).getName());
		}
		System.out.print("Visit which shop? (number or 'back'): ");
		String choice = SCANNER.nextLine().trim();
		if (choice.equalsIgnoreCase("back")) {
			return;
		}
		try {
			int index = Integer.parseInt(choice) - 1;
			if (index >= 0 && index < shops.size()) {
				handleShop(player, shops.get(index));
			} else {
				System.out.println("Invalid selection.");
			}
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number.");
		}
	}

	private static void handleShop(Player player, Shop shop) {
		boolean browsing = true;
		while (browsing) {
			List<Shop.ShopItem> inventory = shop.createInventory(player.getLevel());
			System.out.println("\nWelcome to " + shop.getName() + "! Gold: " + player.getGold());
			for (int i = 0; i < inventory.size(); i++) {
				Shop.ShopItem entry = inventory.get(i);
				Item item = entry.item();
				System.out.printf("%d) %s - %d gold%n", i + 1, item.getName(), entry.price());
			}
			System.out.println("B) Back");
			System.out.print("Buy which item? ");
			String choice = SCANNER.nextLine().trim();
			if (choice.equalsIgnoreCase("b") || choice.equalsIgnoreCase("back")) {
				browsing = false;
				continue;
			}
			try {
				int selection = Integer.parseInt(choice) - 1;
				if (selection >= 0 && selection < inventory.size()) {
					Shop.ShopItem entry = inventory.get(selection);
					if (player.getGold() >= entry.price()) {
						player.subtractGold(entry.price());
						player.addToInventory(entry.item());
						System.out.println("Thank you for your purchase!");
					} else {
						System.out.println("You can't afford that item.");
					}
				} else {
					System.out.println("Invalid selection.");
				}
			} catch (NumberFormatException ex) {
				System.out.println("Please enter a number.");
			}
		}
	}
}
