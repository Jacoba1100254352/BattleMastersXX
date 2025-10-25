package Items;

import Player.Player;

/**
 * Enhanced Items.Consumable class with cooldowns and stacking effects.
 *
 * The consumable hierarchy is used throughout the project (combat, quests,
 * merchants) so it has to be public for callers in other packages.
 */
public abstract class Consumable extends Item {
	protected int cooldown; // Seconds before can use again
	protected boolean canStack; // Can multiple effects stack
	protected String effectType; // Type of effect for stacking rules
	
	public Consumable(String name, String description, int value, int weight) {
		super(name, description, "Consumable", value, weight);
		this.cooldown = 0;
		this.canStack = false;
		this.effectType = "basic";
	}
	
	public abstract void use(Player player);
	
	public int getCooldown() { return cooldown; }
	
	public boolean canStack() { return canStack; }
	
	public String getEffectType() { return effectType; }
	
	protected void setCooldown(int cooldown) { this.cooldown = cooldown; }
	
	protected void setCanStack(boolean canStack) { this.canStack = canStack; }
	
	protected void setEffectType(String effectType) { this.effectType = effectType; }
}
