package Systems;

import Items.Item;
import Items.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class TreasureSystem {
	private TreasureSystem() {
	}

	public static List<Item> generateRandomTreasure(int level) {
		ItemFactory.initialize();
		List<Item> loot = new ArrayList<>();
		int itemCount = ThreadLocalRandom.current().nextInt(1, 4);
		for (int i = 0; i < itemCount; i++) {
			double roll = ThreadLocalRandom.current().nextDouble();
			if (roll < 0.4) {
				loot.add(ItemFactory.createRandomTreasure(level));
			} else if (roll < 0.7) {
				loot.add(ItemFactory.createHealthPotion("Explorer's Draught", 40 + level * 2));
			} else {
				loot.add(ItemFactory.createManaPotion("Explorer's Elixir", 30 + level));
			}
		}
		return loot;
	}
}
