package Items;


/**
 * Enhanced Mana Potion with different effects
 */
class ManaPotion extends Consumable
{
	private int manaAmount;
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
		
		if (boostSpellPower) {
			player.addStatusEffect("spell_power", new SpellPowerBoostEffect(boostDuration, 25));
			System.out.println("Your magical abilities feel enhanced!");
		}
	}
	
	public void setSpellPowerBoost(int duration) {
		this.boostSpellPower = true;
		this.boostDuration = duration;
		this.description += " and boosts spell power";
	}
}
