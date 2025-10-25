package Effects;

import Enemy.Enemy;
import Player.Player;

/**
 * Regeneration effect for healing over time.
 */
public class RegenerationEffect extends StatusEffect {
	public RegenerationEffect(int duration, int healPerTurn) {
		super("Regeneration", duration, healPerTurn, true);
	}
	
	@Override
	public void apply(Object target) {
		if (target instanceof Player player) {
			player.heal(strength);
			System.out.println("Regeneration heals you for " + strength + " HP.");
		} else if (target instanceof Enemy enemy) {
			enemy.heal(strength);
		}
	}
}
