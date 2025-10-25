package Scene.support;

import Player.Player;

import java.util.List;

public class ShopSystem {
	private ShopSystem() {
	}

	public static void showShopsInArea(Player player, List<Shop> shops) {
		if (shops.isEmpty()) {
			System.out.println("There are no shops open here.");
			return;
		}
		System.out.println("Shops available in this area:");
		for (Shop shop : shops) {
			System.out.println(" - " + shop.getName());
			shop.showInventory(player);
		}
	}
}
