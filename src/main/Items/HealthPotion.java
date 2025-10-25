package Items;

import Effects.RegenerationEffect;
import Player.Player;

/**
 * Enhanced Health Potion with different tiers.
 */
public class HealthPotion extends Consumable {
	private final int healAmount;
	private boolean overTimeHealing;
	private int duration;
	
	public HealthPotion(String name, int healAmount) {
		super(name, String.format("Restores %d HP", healAmount), healAmount * 3, 1);
		this.healAmount = healAmount;
		this.overTimeHealing = false;
		this.duration = 0;
		this.effectType = "healing";
	}
	
	@Override
	public void use(Player player) {
		if (overTimeHealing && duration > 0) {
			player.addStatusEffect("regeneration", new RegenerationEffect(duration, Math.max(1, healAmount / duration)));
			System.out.println("You feel a warm healing energy flowing through you.");
		} else {
			player.heal(healAmount);
		}
	}
	
	public void setOverTimeHealing(int duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration must be positive for over-time healing.");
		}
		this.overTimeHealing = true;
		this.duration = duration;
		this.description = String.format("Restores %d HP over %d turns", healAmount, duration);
	}
}
