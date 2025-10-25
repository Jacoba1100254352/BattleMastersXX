package Effects;


import Enemy.Enemy;


/**
 * Poison effect for damage over time
 */
class PoisonEffect extends StatusEffect
{
	public PoisonEffect(int duration, int damagePerTurn) {
		super("Poison", duration, damagePerTurn, false);
	}
	
	@Override
	public void apply(Object target) {
		if (target instanceof Player player) {
			player.takeDamage(strength, "poison");
			System.out.println("Poison deals " + strength + " damage!");
		} else if (target instanceof Enemy enemy) {
			enemy.takeDamage(strength, "poison");
		}
	}
}
