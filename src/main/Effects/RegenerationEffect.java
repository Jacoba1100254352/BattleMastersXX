package Effects;


import Enemy.Enemy;


/**
 * Regeneration effect for healing over time
 */
class RegenerationEffect extends StatusEffect
{
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
