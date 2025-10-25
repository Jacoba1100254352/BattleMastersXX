package Combat;


import java.util.ArrayList;
import java.util.List;


/**
 * Combat.Spell class representing individual spells
 */
class Spell {
	private String name;
	private String school;
	private int manaCost;
	private int levelRequirement;
	private String description;
	private List<String> effects;
	
	public Spell(String name, String school, int manaCost, int levelRequirement,
	             String description, List<String> effects) {
		this.name = name;
		this.school = school;
		this.manaCost = manaCost;
		this.levelRequirement = levelRequirement;
		this.description = description;
		this.effects = new ArrayList<>(effects);
	}
	
	/**
	 * Check if this is a combat spell
	 */
	public boolean isCombatSpell() {
		return effects.stream().anyMatch(effect ->
				                                 effect.startsWith("damage") ||
						                                 effect.startsWith("healing") ||
						                                 effect.startsWith("defense_buff") ||
						                                 effect.contains("chance_"));
	}
	
	// Getters
	public String getName() { return name; }
	public String getSchool() { return school; }
	public int getManaCost() { return manaCost; }
	public int getLevelRequirement() { return levelRequirement; }
	public String getDescription() { return description; }
	public List<String> getEffects() { return effects; }
}
