package Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Faction {
	private final String name;
	private final int expBonus;
	private final int goldBonus;
	private final List<String> combatAbilities;

	public Faction(String name) {
		this(name, 0, 0, List.of());
	}

	public Faction(String name, int expBonus, int goldBonus, List<String> combatAbilities) {
		this.name = name;
		this.expBonus = expBonus;
		this.goldBonus = goldBonus;
		this.combatAbilities = new ArrayList<>(combatAbilities);
	}

	public String getName() { return name; }
	public int getExpBonus() { return expBonus; }
	public int getGoldBonus() { return goldBonus; }
	public List<String> getCombatAbilities() { return Collections.unmodifiableList(combatAbilities); }

	public void triggerRandomEvent(Player player) {
		System.out.println("Faction " + name + " sends you encouraging words.");
		player.addReputation(5);
	}
}
