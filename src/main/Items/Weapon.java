package Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced weapon class with elemental damage and special effects.
 */
public class Weapon extends Item {
	private final int power;
	private final String element;
	private final Map<String, Integer> bonuses;
	private final List<String> specialEffects;
	private int durability;
	private final int maxDurability;
	private double criticalChance;
	
	public Weapon(String name, int power, String element, int weight) {
		super(name, String.format("A %s weapon with %d power", element, power), "Weapon", power * 12, weight);
		this.power = power;
		this.element = element;
		this.bonuses = new HashMap<>();
		this.specialEffects = new ArrayList<>();
		this.maxDurability = 100;
		this.durability = maxDurability;
		this.criticalChance = 0.05; // 5% base crit chance
	}
	
	/**
	 * Use weapon and reduce durability.
	 */
	public void use() {
		if (durability > 0) {
			durability--;
			if (durability == 0) {
				System.out.println("⚠️ " + name + " has broken!");
			} else if (durability < 20) {
				System.out.println("⚠️ " + name + " is badly damaged and needs repair!");
			}
		}
	}
	
	/**
	 * Repair weapon.
	 */
	public void repair(int amount) {
		durability = Math.min(maxDurability, durability + amount);
		System.out.println(name + " has been repaired!");
	}
	
	/**
	 * Get effective power based on durability.
	 */
	public int getEffectivePower() {
		if (durability == 0) return 0;
		return (int) (power * (durability / (double) maxDurability));
	}
	
	// Getters and setters
	public int getPower() { return power; }
	
	public String getElement() { return element; }
	
	public Map<String, Integer> getBonuses() { return bonuses; }
	
	public List<String> getSpecialEffects() { return specialEffects; }
	
	public int getDurability() { return durability; }
	
	public int getMaxDurability() { return maxDurability; }
	
	public double getCriticalChance() { return criticalChance; }
	
	public void addBonus(String stat, int amount) { bonuses.put(stat, bonuses.getOrDefault(stat, 0) + amount); }
	
	public void addSpecialEffect(String effect) { specialEffects.add(effect); }
	
	public void setCriticalChance(double chance) { this.criticalChance = chance; }
}
