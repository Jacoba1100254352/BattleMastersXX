import Player.Player;

public final class CraftingSystem {
	private CraftingSystem() {}

	public static void showCraftingMenu(Player player) {
		System.out.println("Crafting is not yet implemented, but you sort through your materials.");
		System.out.println("Inventory size: " + player.getInventory().size());
	}
}
