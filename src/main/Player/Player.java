package Player;

import Effects.StatusEffect;
import Items.BasicItem;
import Items.Consumable;
import Items.Item;
import Items.ItemFactory;
import Items.Weapon;
import Items.Armor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Core player model that exposes the behaviours exercised across the game
 * systems (combat, quests, prestige, achievements, etc).
 */
public class Player {
	private Map<String, Object> currentQuest;
	private final List<Map<String, Object>> npcQuests;
	private final String name;
	private int hp;
	private int maxHp;
	private int mana;
	private int maxMana;
	private int stamina;
	private int maxStamina;
	private int gold;
	private int totalGoldEarned;
	private int exp;
	private int level;
	private int prestige;
	private int reputation;
	private String location;
	private String currentDomain;
	private final List<Item> inventory;
	private Weapon equippedWeapon;
	private Armor equippedArmor;
	private final List<Companion> companions;
	private final int MAX_COMPANIONS = 3;
	private Horse horse;
	private boolean mythicMode;
	private Faction faction;
	private StartQuest startQuest;
	private final Map<String, StatusEffect> statusEffects;
	private final Map<String, Integer> skills;
	private final Set<String> knownSpells;
	private final Set<String> unlockedAchievements;
	private int enemiesDefeated;
	private int questsCompleted;
	private final List<Map<String, Object>> enemyTypes;
	private int skillPoints;
	private final Random random = new Random();
	
	public Player(String playerName) {
		this.name = playerName;
		this.hp = 100;
		this.maxHp = 100;
		this.mana = 50;
		this.maxMana = 50;
		this.stamina = 100;
		this.maxStamina = 100;
		this.gold = 0;
		this.totalGoldEarned = 0;
		this.exp = 0;
		this.level = 1;
		this.prestige = 0;
		this.reputation = 0;
		this.location = "Town Square";
		this.currentDomain = "Central Domain";
		this.inventory = new ArrayList<>();
		this.equippedWeapon = null;
		this.equippedArmor = null;
		this.companions = new ArrayList<>();
		this.horse = null;
		this.mythicMode = false;
		this.faction = null;
		this.startQuest = null;
		this.statusEffects = new HashMap<>();
		this.skills = new HashMap<>();
		this.knownSpells = new HashSet<>();
		this.unlockedAchievements = new HashSet<>();
		this.enemiesDefeated = 0;
		this.questsCompleted = 0;
		this.currentQuest = null;
		this.npcQuests = new ArrayList<>();
		this.skillPoints = 0;
		
		initialiseSkills();
		this.enemyTypes = initialiseEnemyTypes();
	}
	
	private void initialiseSkills() {
		skills.put("Combat", 1);
		skills.put("Magic", 1);
		skills.put("Crafting", 1);
		skills.put("Gathering", 1);
	}
	
	private List<Map<String, Object>> initialiseEnemyTypes() {
		return List.of(
			Map.of("name", "Fire Elemental", "hp", 150, "attack", 45, "exp", 400, "gold", 150, "is_boss", false, "weight", 5, "resist", List.of("fire"), "weak", List.of("ice")),
			Map.of("name", "Ice Giant", "hp", 180, "attack", 50, "exp", 500, "gold", 180, "is_boss", false, "weight", 4, "resist", List.of("ice"), "weak", List.of("fire")),
			Map.of("name", "Undead Warrior", "hp", 90, "attack", 35, "exp", 250, "gold", 100, "is_boss", false, "weight", 8, "resist", List.of("physical"), "weak", List.of("fire", "holy")),
			Map.of("name", "Shadow Beast", "hp", 120, "attack", 40, "exp", 350, "gold", 120, "is_boss", false, "weight", 6, "resist", List.of("dark"), "weak", List.of("light")),
			Map.of("name", "Goblin", "hp", 30, "attack", 10, "exp", 50, "gold", 20, "is_boss", false, "weight", 40),
			Map.of("name", "Orc", "hp", 50, "attack", 15, "exp", 100, "gold", 40, "is_boss", false, "weight", 15),
			Map.of("name", "Troll", "hp", 80, "attack", 20, "exp", 150, "gold", 70, "is_boss", false, "weight", 10),
			Map.of("name", "Skeleton", "hp", 40, "attack", 12, "exp", 60, "gold", 30, "is_boss", false, "weight", 15),
			Map.of("name", "Bandit", "hp", 35, "attack", 14, "exp", 70, "gold", 25, "is_boss", false, "weight", 15),
			Map.of("name", "Serpent", "hp", 100, "attack", 30, "exp", 300, "gold", 60, "is_boss", false, "weight", 30),
			Map.of("name", "Robber", "hp", 50, "attack", 15, "exp", 100, "gold", 40, "is_boss", false, "weight", 15),
			Map.of("name", "Dark Elf", "hp", 70, "attack", 25, "exp", 200, "gold", 80, "is_boss", false, "weight", 12),
			Map.of("name", "Dragon", "hp", 200, "attack", 40, "exp", 1000, "gold", 500, "is_boss", true, "weight", 5),
			Map.of("name", "Dark Knight", "hp", 150, "attack", 35, "exp", 800, "gold", 400, "is_boss", true, "weight", 5),
			Map.of("name", "Goblin Lord", "hp", 350, "attack", 50, "exp", 1100, "gold", 650, "is_boss", true, "weight", 3),
			Map.of("name", "Central Guardian", "hp", 1500, "attack", 120, "exp", 3000, "gold", 1500, "is_boss", true, "weight", 0.1),
			Map.of("name", "Forest Titan", "hp", 2500, "attack", 180, "exp", 5000, "gold", 2500, "is_boss", true, "weight", 0.08),
			Map.of("name", "Eternal Emperor", "hp", 25000, "attack", 1000, "exp", 50000, "gold", 25000, "is_boss", true, "weight", 0.001)
		);
	}
	
	// -------------------------------------------------------------------------
	// Core stats and combat helpers
	// -------------------------------------------------------------------------
	
	public int attackValue() {
		return calculateAttackPower();
	}
	
	public int calculateAttackPower() {
		int base = 10 + level * 2 + prestige * 10;
		if (equippedWeapon != null) {
			base += equippedWeapon.getEffectivePower();
		}
		return base;
	}
	
	public int defend(int incomingDamage) {
		int defenseValue = prestige * 5;
		if (equippedArmor != null) {
			defenseValue += equippedArmor.getEffectiveDefense();
		}
		if (statusEffects.containsKey("defending")) {
			defenseValue += 25;
		}
		if (faction != null && "Knights of the Phoenix".equals(faction.getName())) {
			defenseValue += 80;
		}
		int realDamage = Math.max(0, incomingDamage - defenseValue);
		if (equippedArmor != null) {
			equippedArmor.takeDamage(realDamage);
		}
		hp = Math.max(0, hp - realDamage);
		System.out.println("You received " + realDamage + " damage (after armor, prestige, and status bonuses).");
		return realDamage;
	}
	
	public void takeDamage(int damage, String element) {
		int applied = defend(damage);
		if (applied > 0 && hp == 0) {
			System.out.println("You have fallen in battle...");
		}
	}
	
	public void heal(int amount) {
		if (amount <= 0) return;
		int before = hp;
		hp = Math.min(maxHp, hp + amount);
		System.out.println("Recovered " + (hp - before) + " HP.");
	}
	
	public void restoreMana(int amount) {
		if (amount == 0) return;
		mana = Math.max(0, Math.min(maxMana, mana + amount));
		if (amount > 0) {
			System.out.println("Recovered " + amount + " mana.");
		}
	}
	
	public void restoreStamina(int amount) {
		if (amount <= 0) return;
		stamina = Math.min(maxStamina, stamina + amount);
	}
	
	public void consumeStamina(int amount) {
		if (amount <= 0) return;
		stamina = Math.max(0, stamina - amount);
		if (stamina == 0) {
			System.out.println("You are exhausted!");
		}
	}
	
	public boolean isAlive() {
		return hp > 0;
	}
	
	// -------------------------------------------------------------------------
	// Progression
	// -------------------------------------------------------------------------
	
	public void gainExperience(int amount) {
		if (amount <= 0) return;
		exp += amount;
		int needed = level * 100;
		while (exp >= needed) {
			exp -= needed;
			level++;
			maxHp += 20;
			hp = maxHp;
			maxMana += 10;
			mana = maxMana;
			maxStamina += 5;
			stamina = maxStamina;
			skillPoints += 1;
			System.out.println("*** You leveled up! You are now level " + level + "! ***");
			needed = level * 100;
		}
		if (level >= 50) {
			System.out.println("*** PRESTIGE AVAILABLE! You can now prestige at level 50! ***");
			System.out.println("Visit the Prestige Hall to prestige and gain exclusive rewards!");
		}
	}
	
	public void gainExp(int amount) {
		gainExperience(amount);
	}
	
	public boolean canPrestige() {
		return level >= 50;
	}
	
	public boolean doPrestige() {
		if (!canPrestige()) {
			System.out.println("You must reach level 50 to prestige!");
			return false;
		}
		prestige++;
		level = 1;
		exp = 0;
		maxHp = 100 + (prestige * 50);
		hp = maxHp;
		maxMana = 50 + (prestige * 25);
		mana = maxMana;
		maxStamina = 100 + (prestige * 20);
		stamina = maxStamina;
		System.out.println("*** PRESTIGE " + prestige + " ACHIEVED! ***");
		System.out.println("Level reset to 1, but you gained permanent bonuses!");
		System.out.println("New HP: " + maxHp + ", New Mana: " + maxMana);
		System.out.println("Attack bonus: " + (prestige * 10) + ", Defense bonus: " + (prestige * 5));
		if (prestige >= 10 && !mythicMode) {
			mythicMode = true;
			System.out.println("*** MYTHIC PRESTIGE UNLOCKED! ***");
			System.out.println("You now have access to the Mythic Gate and Mythic Challenges!");
		}
		return true;
	}
	
	// -------------------------------------------------------------------------
	// Inventory management
	// -------------------------------------------------------------------------
	
	public void addToInventory(Item item) {
		addToInventory(item, true, true);
	}

	public void addToInventory(Item item, boolean autoEquip) {
		addToInventory(item, autoEquip, true);
	}

	public void addToInventory(Item item, boolean autoEquip, boolean announce) {
		if (item == null) return;
		inventory.add(item);
		if (announce) {
			System.out.println(item.getName() + " added to inventory.");
		}
		if (autoEquip) {
			if (equippedWeapon == null && item instanceof Weapon weapon) {
				equipWeapon(weapon);
			} else if (equippedArmor == null && item instanceof Armor armor) {
				equipArmor(armor);
			}
		}
	}
	
	public void removeFromInventory(Item item) {
		inventory.remove(item);
	}

	public void clearInventory() {
		inventory.clear();
	}

	public boolean removeItemsByName(String itemName, int count) {
		if (count <= 0) return true;
		int removed = 0;
		for (int i = inventory.size() - 1; i >= 0 && removed < count; i--) {
			Item item = inventory.get(i);
			if (item.getName().equals(itemName)) {
				inventory.remove(i);
				removed++;
			}
		}
		return removed == count;
	}

	public boolean removeItemsByType(Class<? extends Item> type, int count) {
		if (count <= 0) return true;
		int removed = 0;
		for (int i = inventory.size() - 1; i >= 0 && removed < count; i--) {
			Item item = inventory.get(i);
			if (type.isInstance(item)) {
				inventory.remove(i);
				removed++;
			}
		}
		return removed == count;
	}

	public List<Item> getItemsByType(Class<? extends Item> type) {
		List<Item> result = new ArrayList<>();
		for (Item item : inventory) {
			if (type.isInstance(item)) {
				result.add(item);
			}
		}
		return result;
	}
	
	public List<Item> getInventory() {
		return Collections.unmodifiableList(inventory);
	}
	
	public void showInventory() {
		if (inventory.isEmpty()) {
			System.out.println("Inventory is empty.");
			return;
		}
		System.out.println("Inventory:");
		for (int i = 0; i < inventory.size(); i++) {
			Item item = inventory.get(i);
			System.out.printf("%d. %s (%s)%n", i + 1, item.getName(), item.getType());
		}
	}
	
	public void useItem() {
		for (Item item : new ArrayList<>(inventory)) {
			if (item instanceof Consumable consumable) {
				consumable.use(this);
				removeFromInventory(item);
				System.out.println("Used " + item.getName() + "!");
				return;
			}
		}
		System.out.println("No usable consumables available.");
	}
	
	public Weapon getEquippedWeapon() {
		return equippedWeapon;
	}
	
	public Armor getEquippedArmor() {
		return equippedArmor;
	}
	
	public void equipWeapon(Weapon weapon) {
		equipWeapon(weapon, true);
	}

	public void equipWeapon(Weapon weapon, boolean announce) {
		if (weapon == null) return;
		if (!inventory.contains(weapon)) {
			addToInventory(weapon, false, false);
		}
		this.equippedWeapon = weapon;
		if (announce) {
			System.out.println("Equipped weapon: " + weapon.getName());
		}
	}

	public void equipArmor(Armor armor) {
		equipArmor(armor, true);
	}

	public void equipArmor(Armor armor, boolean announce) {
		if (armor == null) return;
		if (!inventory.contains(armor)) {
			addToInventory(armor, false, false);
		}
		this.equippedArmor = armor;
		if (announce) {
			System.out.println("Equipped armor: " + armor.getName());
		}
	}

	public void unequipWeapon() {
		this.equippedWeapon = null;
		System.out.println("You stow your weapon.");
	}

	public void unequipArmor() {
		this.equippedArmor = null;
		System.out.println("You remove your equipped armor.");
	}
	
	// -------------------------------------------------------------------------
	// Status effects & skills
	// -------------------------------------------------------------------------
	
	public Map<String, StatusEffect> getStatusEffects() {
		return Collections.unmodifiableMap(statusEffects);
	}
	
	public void addStatusEffect(String key, StatusEffect effect) {
		if (effect == null) return;
		statusEffects.put(key, effect);
	}
	
	public void removeStatusEffect(String key) {
		statusEffects.remove(key);
	}
	
	public void processStatusEffects() {
		List<String> expired = new ArrayList<>();
		for (Map.Entry<String, StatusEffect> entry : statusEffects.entrySet()) {
			StatusEffect effect = entry.getValue();
			effect.apply(this);
			effect.decrementDuration();
			if (effect.isExpired()) {
				expired.add(entry.getKey());
			}
		}
		for (String key : expired) {
			statusEffects.remove(key);
			System.out.println("Status effect " + key + " has expired.");
		}
	}
	
	public Map<String, Integer> getSkills() {
		return Collections.unmodifiableMap(skills);
	}

	public int getSkillPoints() {
		return skillPoints;
	}

	public void addSkillPoints(int amount) {
		if (amount > 0) {
			skillPoints += amount;
		}
	}

	public boolean trainSkill(String skill, int cost) {
		if (cost <= 0) cost = 1;
		if (skillPoints < cost) {
			System.out.println("Not enough skill points.");
			return false;
		}
		skillPoints -= cost;
		int newValue = skills.merge(skill, cost, Integer::sum);
		System.out.println("" + skill + " increased to " + newValue + ".");
		return true;
	}

	public void setSkillLevel(String skill, int value) {
		skills.put(skill, Math.max(1, value));
	}
	
	public void gainSkillExperience(String skill, int amount) {
		if (amount <= 0) return;
		skills.merge(skill, Math.max(1, amount / 10), Integer::sum);
	}
	
	public Set<String> getKnownSpells() {
		return knownSpells;
	}
	
	// -------------------------------------------------------------------------
	// Quests
	// -------------------------------------------------------------------------
	
	public void startQuest() {
		if (currentQuest != null) {
			System.out.println("You already have an active quest. Complete it before taking a new one.");
			return;
		}
		int effectiveLevel = level + (prestige * 20);
		List<Map<String, Object>> enemyPool = new ArrayList<>();
		for (Map<String, Object> e : enemyTypes) {
			boolean isBoss = (boolean) e.getOrDefault("is_boss", false);
			int enemyHp = ((Number) e.getOrDefault("hp", 0)).intValue();
			if (!isBoss && enemyHp <= effectiveLevel * 30 + 40) {
				enemyPool.add(e);
			}
		}
		if (enemyPool.isEmpty()) {
			for (Map<String, Object> e : enemyTypes) {
				if (!((boolean) e.getOrDefault("is_boss", false))) {
					enemyPool.add(e);
				}
			}
		}
		if (enemyPool.isEmpty()) {
			System.out.println("No available enemies to start a quest.");
			return;
		}
		int randomIndex = random.nextInt(enemyPool.size());
		Map<String, Object> questEnemy = enemyPool.get(randomIndex);
		currentQuest = Map.of(
			"name", questEnemy.get("name"),
			"hp", questEnemy.get("hp"),
			"attack", questEnemy.get("attack"),
			"exp", questEnemy.get("exp"),
			"gold", questEnemy.get("gold")
		);
		System.out.println("A quest has started against: " + questEnemy.get("name"));
		System.out.print("A quest has been assigned: Defeat a " + questEnemy.get("name") + "!");
	}
	
	public boolean completeQuest(String defeatedEnemyName) {
		if (currentQuest == null || currentQuest.isEmpty()) {
			System.out.println("No active quest to complete.");
			return false;
		}
		Object nameObj = currentQuest.get("name");
		if (!(nameObj instanceof String questName)) {
			System.out.println("Invalid current quest data.");
			currentQuest = null;
			return false;
		}
		if (!questName.equals(defeatedEnemyName)) {
			System.out.println("You have not defeated the required enemy: " + questName);
			return false;
		}
		System.out.println("*** Quest Completed: You defeated the " + questName + "! ***");
		int bonusExp = ((Number) currentQuest.getOrDefault("exp", 0)).intValue();
		int bonusGold = ((Number) currentQuest.getOrDefault("gold", 0)).intValue();
		Item bonusItem = randomRewardItem();
		gainExperience(bonusExp);
		addGold(bonusGold);
		addToInventory(bonusItem);
		System.out.println("You earned " + bonusExp + " EXP, " + bonusGold + " gold, and found a " + bonusItem.getName() + "!");
		currentQuest = null;
		incrementQuestsCompleted();
		return true;
	}
	
	private Item randomRewardItem() {
		List<Item> pool = List.of(
			ItemFactory.createHealthPotion("Small Health Potion", 50),
			ItemFactory.createWeapon("Iron Sword", 25, "physical"),
			ItemFactory.createArmor("Leather Armor", 35, "Light"),
			ItemFactory.createManaPotion("Mana Elixir", 30)
		);
		return pool.get(random.nextInt(pool.size()));
	}
	
	public boolean acceptNpcQuest(String npcName, Map<String, Object> questData) {
		for (Map<String, Object> q : npcQuests) {
			Object npc = q.get("npc");
			if (npcName.equals(npc)) {
				System.out.println("You already have a quest from " + npcName + "!");
				return false;
			}
		}
		Map<String, Object> newQuest = new HashMap<>();
		newQuest.put("npc", npcName);
		newQuest.put("type", questData.get("type"));
		newQuest.put("target", questData.getOrDefault("target", null));
		newQuest.put("quantity", ((Number) questData.getOrDefault("quantity", 1)).intValue());
		newQuest.put("progress", 0);
		newQuest.put("reward", questData.get("reward"));
		newQuest.put("description", questData.get("description"));
		npcQuests.add(newQuest);
		System.out.println("Quest accepted from " + npcName + ": " + questData.get("description"));
		return true;
	}
	
	public void updateNpcQuestProgress(String questType, String target) {
		List<Map<String, Object>> toComplete = new ArrayList<>();
		for (Map<String, Object> quest : npcQuests) {
			String type = (String) quest.get("type");
			if (type == null || !type.equals(questType)) continue;
			Object qTarget = quest.get("target");
			int quantity = ((Number) quest.getOrDefault("quantity", 1)).intValue();
			int progress = ((Number) quest.getOrDefault("progress", 0)).intValue();
			boolean match = false;
			if ("find_item".equals(type) && target != null && target.equals(qTarget)) match = true;
			if ("defeat_enemy".equals(type) && target != null && target.equals(qTarget)) match = true;
			if ("deliver_item".equals(type) && target != null && target.equals(qTarget)) match = true;
			if (match) {
				progress += 1;
				quest.put("progress", progress);
				if (progress >= quantity) toComplete.add(quest);
			}
		}
		for (Map<String, Object> q : toComplete) completeNpcQuest(q);
	}
	
	public void completeNpcQuest(Map<String, Object> quest) {
		System.out.println("*** NPC Quest Completed for " + quest.get("npc") + "! ***");
		Object rewardObj = quest.get("reward");
		if (rewardObj instanceof Map<?, ?> reward) {
			if (reward.containsKey("gold")) {
				int g = ((Number) reward.get("gold")).intValue();
				addGold(g);
				System.out.println("You received " + g + " gold!");
			}
			if (reward.containsKey("exp")) {
				int e = ((Number) reward.get("exp")).intValue();
				gainExperience(e);
				System.out.println("You received " + e + " experience!");
			}
			if (reward.containsKey("item")) {
				Item item = convertRewardItem(reward.get("item"));
				if (item != null) {
					addToInventory(item);
					System.out.println("You received " + item.getName() + "!");
				}
			}
		}
		npcQuests.remove(quest);
		incrementQuestsCompleted();
	}
	
	private Item convertRewardItem(Object raw) {
		if (raw instanceof Item item) {
			return item;
		}
		if (raw instanceof Map<?, ?> map) {
			Object nameObj = map.get("name");
			Object descriptionObj = map.get("description");
			Object typeObj = map.get("type");
			Object valueObj = map.get("value");
			Object weightObj = map.get("weight");
			
			String name = nameObj != null ? nameObj.toString() : "Mysterious Item";
			String description = descriptionObj != null ? descriptionObj.toString() : "A special quest reward.";
			String type = typeObj != null ? typeObj.toString() : "Quest";
			int value = valueObj instanceof Number number ? number.intValue() : 0;
			int weight = weightObj instanceof Number number ? number.intValue() : 1;
			return new BasicItem(name, description, type, value, weight);
		}
		return null;
	}
	
	public void checkNpcQuestItems() {
		for (Map<String, Object> quest : new ArrayList<>(npcQuests)) {
			String type = (String) quest.get("type");
			if (!"find_item".equals(type)) continue;
			String target = (String) quest.get("target");
			int quantity = ((Number) quest.getOrDefault("quantity", 1)).intValue();
			long itemCount = inventory.stream()
				.map(Item::getName)
				.filter(name -> name.equals(target))
				.count();
			int progress = ((Number) quest.getOrDefault("progress", 0)).intValue();
			if (itemCount >= quantity && progress < quantity) {
				quest.put("progress", quantity);
				completeNpcQuest(quest);
				break;
			}
		}
	}
	
	public void showQuests() {
		if (currentQuest == null) {
			System.out.println("No active personal quest.");
		} else {
			System.out.println("Current Quest: Defeat " + currentQuest.get("name"));
		}
		if (npcQuests.isEmpty()) {
			System.out.println("No active NPC quests.");
			return;
		}
		System.out.println("NPC Quests:");
		for (Map<String, Object> quest : npcQuests) {
			System.out.printf("- [%s] %s (Progress: %d/%d)%n",
				quest.get("npc"),
				quest.get("description"),
				((Number) quest.getOrDefault("progress", 0)).intValue(),
				((Number) quest.getOrDefault("quantity", 1)).intValue());
		}
	}
	
	// -------------------------------------------------------------------------
	// Economy & stats
	// -------------------------------------------------------------------------
	
	public void addGold(int amount) {
		if (amount <= 0) return;
		gold += amount;
		totalGoldEarned += amount;
	}
	
	public void subtractGold(int amount) {
		if (amount <= 0) return;
		gold = Math.max(0, gold - amount);
	}
	
	public void incrementEnemiesDefeated() {
		enemiesDefeated++;
	}
	
	private void incrementQuestsCompleted() {
		questsCompleted++;
	}
	
	public void showDetailedStats() {
		System.out.println("=== Player Statistics ===");
		System.out.printf("Level %d (Prestige %d)%n", level, prestige);
		System.out.printf("HP: %d/%d | Mana: %d/%d | Stamina: %d/%d%n",
			hp, maxHp, mana, maxMana, stamina, maxStamina);
		System.out.printf("Gold: %d (Total Earned: %d)%n", gold, totalGoldEarned);
		System.out.printf("Reputation: %d%n", reputation);
		System.out.printf("Enemies defeated: %d | Quests completed: %d%n", enemiesDefeated, questsCompleted);
		System.out.println("Skills:");
		skills.forEach((skill, value) -> System.out.printf(" - %s: %d%n", skill, value));
		if (!unlockedAchievements.isEmpty()) {
			System.out.println("Achievements:");
			unlockedAchievements.forEach(ach -> System.out.println(" • " + ach));
		}
	}
	
	// -------------------------------------------------------------------------
	// Accessors
	// -------------------------------------------------------------------------
	
	public String getName() { return name; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public String getCurrentDomain() { return currentDomain; }
	public void setCurrentDomain(String currentDomain) { this.currentDomain = currentDomain; }
	public int getHp() { return hp; }
	public int getMaxHp() { return maxHp; }
	public int getMana() { return mana; }
	public int getMaxMana() { return maxMana; }
	public int getStamina() { return stamina; }
	public int getMaxStamina() { return maxStamina; }
	public int getGold() { return gold; }
	public int getLevel() { return level; }
	public int getPrestige() { return prestige; }
	public int getExp() { return exp; }
	public Horse getHorse() { return horse; }
	public void setHorse(Horse horse) { this.horse = horse; }
	public Faction getFaction() { return faction; }
	public void setFaction(Faction faction) { this.faction = faction; }
	public void setEquippedWeapon(Weapon weapon) { this.equippedWeapon = weapon; }
	public void setEquippedArmor(Armor armor) { this.equippedArmor = armor; }
	public int getReputation() { return reputation; }
	public void addReputation(int amount) { reputation += amount; }
	public List<Companion> getCompanions() { return Collections.unmodifiableList(companions); }
	public Companion getCompanionByName(String name) {
		for (Companion companion : companions) {
			if (companion.getName().equalsIgnoreCase(name)) {
				return companion;
			}
		}
		return null;
	}
	public boolean addCompanion(Companion companion) {
		if (companions.size() >= MAX_COMPANIONS) {
			System.out.println("You can only have " + MAX_COMPANIONS + " companions maximum!");
			return false;
		}
		companions.add(companion);
		System.out.println(companion.getName() + " has joined you.");
		return true;
	}

	public boolean removeCompanion(String name) {
		for (int i = 0; i < companions.size(); i++) {
			if (companions.get(i).getName().equalsIgnoreCase(name)) {
				companions.remove(i);
				System.out.println(name + " has departed.");
				return true;
			}
		}
		System.out.println("No companion named " + name + " found.");
		return false;
	}
	public boolean isMythicMode() { return mythicMode; }
	public void setMythicMode(boolean mythicMode) { this.mythicMode = mythicMode; }
	public int getEnemiesDefeated() { return enemiesDefeated; }
	public int getQuestsCompleted() { return questsCompleted; }
	public int getTotalGoldEarned() { return totalGoldEarned; }
	public Set<String> getUnlockedAchievements() { return Collections.unmodifiableSet(unlockedAchievements); }

	public void addAchievement(String achievementId) {
		unlockedAchievements.add(achievementId);
	}

	public Snapshot createSnapshot() {
		List<String> inventoryNames = inventory.stream().map(Item::getName).toList();
		Map<String, Integer> skillCopy = new HashMap<>(skills);
		List<String> spellCopy = new ArrayList<>(knownSpells);
		List<String> achievementCopy = new ArrayList<>(unlockedAchievements);
		List<String> companionNames = companions.stream().map(Companion::getName).toList();
		String weaponName = equippedWeapon != null ? equippedWeapon.getName() : null;
		String armorName = equippedArmor != null ? equippedArmor.getName() : null;
		String horseName = horse != null ? horse.getName() : null;
		double horseSpeed = horse != null ? horse.getSpeed() : 0.0;
		return new Snapshot(
			name,
			level,
			exp,
			hp,
			maxHp,
			mana,
			maxMana,
			stamina,
			maxStamina,
			prestige,
			gold,
			totalGoldEarned,
			reputation,
			skillPoints,
			enemiesDefeated,
			questsCompleted,
			location,
			currentDomain,
			mythicMode,
			inventoryNames,
			skillCopy,
			spellCopy,
			achievementCopy,
			companionNames,
			weaponName,
			armorName,
			horseName,
			horseSpeed
		);
	}

	public void restoreFromSnapshot(Snapshot snapshot) {
		if (!name.equals(snapshot.playerName)) {
			System.out.println("Loading save for " + snapshot.playerName + " into current character " + name + ".");
		}
		level = snapshot.level;
		exp = snapshot.exp;
		hp = snapshot.hp;
		maxHp = snapshot.maxHp;
		mana = snapshot.mana;
		maxMana = snapshot.maxMana;
		stamina = snapshot.stamina;
		maxStamina = snapshot.maxStamina;
		prestige = snapshot.prestige;
		gold = snapshot.gold;
		totalGoldEarned = snapshot.totalGoldEarned;
		reputation = snapshot.reputation;
		skillPoints = snapshot.skillPoints;
		enemiesDefeated = snapshot.enemiesDefeated;
		questsCompleted = snapshot.questsCompleted;
		location = snapshot.location;
		currentDomain = snapshot.currentDomain;
		mythicMode = snapshot.mythicMode;
		inventory.clear();
		for (String itemName : snapshot.inventoryNames) {
			Item item = ItemFactory.createNamedItem(itemName);
			addToInventory(item, false, false);
		}
		skills.clear();
		skills.putAll(snapshot.skillLevels);
		knownSpells.clear();
		knownSpells.addAll(snapshot.knownSpells);
		unlockedAchievements.clear();
		unlockedAchievements.addAll(snapshot.achievements);
		companions.clear();
		for (String companionName : snapshot.companionNames) {
			companions.add(new Companion(companionName, 1.0) { });
		}
		if (snapshot.horseName != null) {
			horse = new Horse(snapshot.horseName, snapshot.horseSpeed);
		} else {
			horse = null;
		}
		if (snapshot.equippedWeaponName != null) {
			for (Item item : inventory) {
				if (item instanceof Weapon weapon && weapon.getName().equals(snapshot.equippedWeaponName)) {
					equipWeapon(weapon, false);
					break;
				}
			}
		} else {
			equippedWeapon = null;
		}
		if (snapshot.equippedArmorName != null) {
			for (Item item : inventory) {
				if (item instanceof Armor armor && armor.getName().equals(snapshot.equippedArmorName)) {
					equipArmor(armor, false);
					break;
				}
			}
		} else {
			equippedArmor = null;
		}
	}

	public static class Snapshot {
		public final String playerName;
		public final int level;
		public final int exp;
		public final int hp;
		public final int maxHp;
		public final int mana;
		public final int maxMana;
		public final int stamina;
		public final int maxStamina;
		public final int prestige;
		public final int gold;
		public final int totalGoldEarned;
		public final int reputation;
		public final int skillPoints;
		public final int enemiesDefeated;
		public final int questsCompleted;
		public final String location;
		public final String currentDomain;
		public final boolean mythicMode;
		public final List<String> inventoryNames;
		public final Map<String, Integer> skillLevels;
		public final List<String> knownSpells;
		public final List<String> achievements;
		public final List<String> companionNames;
		public final String equippedWeaponName;
		public final String equippedArmorName;
		public final String horseName;
		public final double horseSpeed;

		public Snapshot(String playerName, int level, int exp, int hp, int maxHp, int mana, int maxMana,
		               int stamina, int maxStamina, int prestige, int gold, int totalGoldEarned,
		               int reputation, int skillPoints, int enemiesDefeated, int questsCompleted,
		               String location, String currentDomain, boolean mythicMode,
		               List<String> inventoryNames, Map<String, Integer> skillLevels,
		               List<String> knownSpells, List<String> achievements, List<String> companionNames,
		               String equippedWeaponName, String equippedArmorName, String horseName, double horseSpeed) {
			this.playerName = playerName;
			this.level = level;
			this.exp = exp;
			this.hp = hp;
			this.maxHp = maxHp;
			this.mana = mana;
			this.maxMana = maxMana;
			this.stamina = stamina;
			this.maxStamina = maxStamina;
			this.prestige = prestige;
			this.gold = gold;
			this.totalGoldEarned = totalGoldEarned;
			this.reputation = reputation;
			this.skillPoints = skillPoints;
			this.enemiesDefeated = enemiesDefeated;
			this.questsCompleted = questsCompleted;
			this.location = location;
			this.currentDomain = currentDomain;
			this.mythicMode = mythicMode;
			this.inventoryNames = List.copyOf(inventoryNames);
			this.skillLevels = new HashMap<>(skillLevels);
			this.knownSpells = List.copyOf(knownSpells);
			this.achievements = List.copyOf(achievements);
			this.companionNames = List.copyOf(companionNames);
			this.equippedWeaponName = equippedWeaponName;
			this.equippedArmorName = equippedArmorName;
			this.horseName = horseName;
			this.horseSpeed = horseSpeed;
		}
	}
}
