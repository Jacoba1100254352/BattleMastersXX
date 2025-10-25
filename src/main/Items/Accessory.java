package Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced accessory class with set bonuses and enchantments.
 */
public class Accessory extends Item {
	private final Map<String, Integer> bonuses;
	private final List<String> effects;
	private final String accessoryType; // Ring, Amulet, Charm, etc.
	private String setName;
	private int enchantmentLevel;
	
	public Accessory(String name, String description, String accessoryType) {
		super(name, description, "Accessory", 150, 1);
		this.accessoryType = accessoryType;
		this.bonuses = new HashMap<>();
		this.effects = new ArrayList<>();
		this.enchantmentLevel = 0;
	}
	
	public void addBonus(String stat, int amount) {
		bonuses.put(stat, bonuses.getOrDefault(stat, 0) + amount);
	}
	
	public void addEffect(String effect) {
		effects.add(effect);
	}
	
	public void enchant(int level) {
		this.enchantmentLevel += level;
		System.out.println(name + " has been enchanted to level " + enchantmentLevel + "!");
	}
	
	// Getters
	public Map<String, Integer> getBonuses() { return bonuses; }
	
	public List<String> getEffects() { return effects; }
	
	public String getAccessoryType() { return accessoryType; }
	
	public String getSetName() { return setName; }
	
	public int getEnchantmentLevel() { return enchantmentLevel; }
	
	public void setSetName(String setName) { this.setName = setName; }
}
