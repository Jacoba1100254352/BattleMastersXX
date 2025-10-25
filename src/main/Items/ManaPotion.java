package Items;

import Effects.SpellPowerBoostEffect;
import Player.Player;

/**
 * Enhanced Mana Potion with different effects.
 */
public class ManaPotion extends Consumable {
	private final int manaAmount;
	private boolean boostSpellPower;
	private int boostDuration;
	
	public ManaPotion(String name, int manaAmount) {
		super(name, String.format("Restores %d mana", manaAmount), manaAmount * 4, 1);
		this.manaAmount = manaAmount;
		this.boostSpellPower = false;
		this.boostDuration = 0;
		this.effectType = "mana";
	}
	
	@Override
	public void use(Player player) {
		player.restoreMana(manaAmount);
		
		if (boostSpellPower && boostDuration > 0) {
			player.addStatusEffect("spell_power", new SpellPowerBoostEffect(boostDuration, 25));
			System.out.println("Your magical abilities feel enhanced!");
		}
	}
	
	public void setSpellPowerBoost(int duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Spell power boost duration must be positive.");
		}
		this.boostSpellPower = true;
		this.boostDuration = duration;
		this.description = String.format("%s and boosts spell power", description);
	}
}
