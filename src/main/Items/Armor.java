package Items;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced armor class with set bonuses and durability.
 */
public class Armor extends Item {
	private final int defense;
	private final Map<String, Integer> resistances;
	private final Map<String, Integer> bonuses;
	private final String armorType; // Light, Medium, Heavy
	private int durability;
	private final int maxDurability;
	private String setName; // For set bonuses
	
	public Armor(String name, int defense, String armorType, int weight) {
		super(name, String.format("%s armor providing %d defense", armorType, defense), "Armor", defense * 10, weight);
		this.defense = defense;
		this.armorType = armorType;
		this.resistances = new HashMap<>();
		this.bonuses = new HashMap<>();
		this.maxDurability = 150;
		this.durability = maxDurability;
	}
	
	/**
	 * Take damage and reduce durability.
	 */
	public void takeDamage(int damage) {
		int durabilityLoss = Math.max(1, damage / 10);
		durability = Math.max(0, durability - durabilityLoss);
		
		if (durability == 0) {
			System.out.println("⚠️ " + name + " has been destroyed!");
		} else if (durability < 30) {
			System.out.println("⚠️ " + name + " is severely damaged!");
		}
	}
	
	/**
	 * Get effective defense based on durability.
	 */
	public int getEffectiveDefense() {
		if (durability == 0) return 0;
		return (int) (defense * (durability / (double) maxDurability));
	}
	
	// Getters and setters
	public int getDefense() { return defense; }
	
	public Map<String, Integer> getResistances() { return resistances; }
	
	public Map<String, Integer> getBonuses() { return bonuses; }
	
	public String getArmorType() { return armorType; }
	
	public int getDurability() { return durability; }
	
	public int getMaxDurability() { return maxDurability; }
	
	public String getSetName() { return setName; }
	
	public void addResistance(String element, int amount) { resistances.put(element, resistances.getOrDefault(element, 0) + amount); }
	
	public void addBonus(String stat, int amount) { bonuses.put(stat, bonuses.getOrDefault(stat, 0) + amount); }
	
	public void setSetName(String setName) { this.setName = setName; }
	
	public void repair(int amount) {
		durability = Math.min(maxDurability, durability + amount);
		System.out.println(name + " has been repaired!");
	}
}
