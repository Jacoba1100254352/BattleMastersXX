package Scene.support;

import Items.Item;
import Items.ItemFactory;
import Player.Player;

import java.util.List;

public class Shop {
	private final String name;
	private final String location;

	public Shop(String name, String location) {
		this.name = name;
		this.location = location;
	}

	public String getName() { return name; }
	public String getLocation() { return location; }

	public void showInventory(Player player) {
		System.out.println(name + " shop inventory:");
		List<Item> wares = List.of(
			ItemFactory.createHealthPotion("Healing Draught", 45),
			ItemFactory.createManaPotion("Mystic Infusion", 35)
		);
		for (int i = 0; i < wares.size(); i++) {
			Item item = wares.get(i);
			System.out.printf("%d. %s (%s)%n", i + 1, item.getName(), item.getType());
		}
	}
}
