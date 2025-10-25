package Player;

import Effects.StatusEffect;
import Enemy.Enemy;

/**
 * Base companion type used for combat interactions.
 */
public abstract class Companion {
	private final String name;
	private final double speed;
	
	public Companion(String name, double speed) {
		this.name = name;
		this.speed = speed;
	}
	
	public String getName() { return name; }
	
	public double getSpeed() { return speed; }
	
	public void executeCommand(String command, Player player, Enemy enemy) {
		switch (command) {
			case "1", "attack" -> attack(player, enemy);
			case "2", "heal" -> heal(player);
			case "3", "defend" -> defend(player);
			case "4", "special" -> special(player, enemy);
			default -> idle(player);
		}
	}
	
	public void interruptEnemyAction(Player player, Enemy enemy) {
		// Default behaviour: small chance to deal extra damage
		System.out.println(name + " distracts the enemy!");
		enemy.takeDamage(10 + player.getLevel(), "physical");
	}
	
	protected void attack(Player player, Enemy enemy) {
		if (enemy == null) return;
		int damage = 5 + player.getLevel();
		enemy.takeDamage(damage, "physical");
		System.out.println(name + " strikes for " + damage + " damage!");
	}
	
	protected void heal(Player player) {
		player.heal(10 + player.getLevel());
		System.out.println(name + " helps restore your vitality.");
	}
	
	protected void defend(Player player) {
		player.addStatusEffect(name + "_guard", new CompanionGuardEffect());
		System.out.println(name + " guarding you this turn.");
	}
	
	protected void special(Player player, Enemy enemy) {
		// Default special does a modest buff
		player.restoreStamina(10);
		System.out.println(name + " inspires you, restoring stamina.");
	}
	
	protected void idle(Player player) {
		System.out.println(name + " stays on standby.");
	}

	private static class CompanionGuardEffect extends StatusEffect {
		CompanionGuardEffect() {
			super("Companion Guard", 1, 30, true);
		}

		@Override
		public void apply(Object target) {
			// Passive guard bonus handled in Player.defend
		}
	}
}
