package Scene.support;

import Items.Item;
import Items.ItemFactory;

import java.util.ArrayList;
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

	public List<ShopItem> createInventory(int playerLevel) {
		List<ShopItem> items = new ArrayList<>();
		items.add(new ShopItem(ItemFactory.createHealthPotion("Healing Draught", 45 + playerLevel), 120 + playerLevel * 5));
		items.add(new ShopItem(ItemFactory.createManaPotion("Mystic Infusion", 35 + playerLevel), 140 + playerLevel * 6));
		items.add(new ShopItem(ItemFactory.createRandomTreasure(playerLevel), 160 + playerLevel * 8));
		return items;
	}

	public record ShopItem(Item item, int price) { }
}
