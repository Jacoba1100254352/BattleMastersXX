import Items.Item;
import Items.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class TreasureSystem {
	private TreasureSystem() {}

	public static List<Item> generateRandomTreasure(int level) {
		ItemFactory.initialize();
		List<Item> loot = new ArrayList<>();
		int count = ThreadLocalRandom.current().nextInt(1, 3);
		for (int i = 0; i < count; i++) {
			loot.add(ItemFactory.createRandomTreasure(level));
		}
		return loot;
	}
}
