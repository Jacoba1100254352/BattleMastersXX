package Items;


/**
 * Enhanced Health Potion with different tiers
 */
class HealthPotion extends Consumable
{
	private int healAmount;
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
		if (overTimeHealing) {
			player.addStatusEffect("regeneration", new RegenerationEffect(duration, healAmount / duration));
			System.out.println("You feel a warm healing energy flowing through you.");
		} else {
			player.heal(healAmount);
		}
	}
	
	public void setOverTimeHealing(int duration) {
		this.overTimeHealing = true;
		this.duration = duration;
		this.description = String.format("Restores %d HP over %d turns", healAmount, duration);
	}
}
