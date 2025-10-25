package Enemy;

import Items.ItemFactory;
import Items.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Enhanced Enemy.Enemy Factory with domain-specific creation
 */
public class EnemyFactory {
	private static Map<String, List<EnemyTemplate>> domainEnemies;
	private static boolean initialized = false;
	
	public static void initialize() {
		if (initialized) return;
		
		domainEnemies = new HashMap<>();
		setupDomainEnemies();
		initialized = true;
	}
	
	private static void setupDomainEnemies() {
		// Central Domain - Beginner enemies
		domainEnemies.put("Central Domain", List.of(
				new EnemyTemplate("Goblin Scout", 30, 8, 25, 15, "physical", "Aggressive"),
				new EnemyTemplate("Wild Boar", 45, 12, 35, 20, "physical", "Berserker"),
				new EnemyTemplate("Bandit", 40, 10, 30, 25, "physical", "Tactical"),
				new EnemyTemplate("Town Guard", 60, 15, 50, 35, "physical", "Defensive") // Boss
		));
		
		// Fire Domain - Fire enemies
		domainEnemies.put("Fire Domain", List.of(
				new EnemyTemplate("Fire Imp", 50, 18, 60, 40, "fire", "Aggressive"),
				new EnemyTemplate("Lava Slug", 80, 22, 80, 50, "fire", "Defensive"),
				new EnemyTemplate("Flame Spirit", 70, 25, 90, 60, "fire", "Tactical"),
				new EnemyTemplate("Fire Dragon", 300, 80, 500, 300, "fire", "Tactical") // Boss
		));
		
		// Ice Domain - Ice enemies
		domainEnemies.put("Ice Domain", List.of(
				new EnemyTemplate("Frost Wolf", 60, 20, 70, 45, "ice", "Aggressive"),
				new EnemyTemplate("Ice Golem", 120, 25, 100, 65, "ice", "Defensive"),
				new EnemyTemplate("Blizzard Wraith", 80, 28, 110, 70, "ice", "Tactical"),
				new EnemyTemplate("Frost Giant", 400, 90, 600, 350, "ice", "Berserker") // Boss
		));
		
		// Add more domains...
		// Shadow Domain
		domainEnemies.put("Shadow Domain", List.of(
				new EnemyTemplate("Shadow Lurker", 45, 22, 65, 50, "dark", "Tactical"),
				new EnemyTemplate("Void Spawn", 70, 28, 85, 60, "dark", "Aggressive"),
				new EnemyTemplate("Dark Wraith", 90, 32, 120, 80, "dark", "Tactical"),
				new EnemyTemplate("Shadow Lord", 500, 100, 750, 400, "dark", "Tactical") // Boss
		));
		
		// Divine Domain - Mythic enemies
		domainEnemies.put("Divine Domain", List.of(
				new EnemyTemplate("Angel Guardian", 200, 60, 300, 200, "light", "Defensive"),
				new EnemyTemplate("Seraph Warrior", 250, 75, 400, 250, "light", "Tactical"),
				new EnemyTemplate("Divine Avatar", 350, 90, 600, 350, "light", "Tactical"),
				new EnemyTemplate("God Emperor", 1000, 200, 2000, 1000, "divine", "Tactical") // Ultimate Boss
		));
	}
	
	/**
	 * Create random enemy for domain and level
	 */
	public static Enemy createRandomEnemy(int playerLevel, String domain) {
		if (!initialized) initialize();
		
		List<EnemyTemplate> templates = domainEnemies.getOrDefault(domain, domainEnemies.get("Central Domain"));
		EnemyTemplate template = templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
		
		return createEnemyFromTemplate(template, playerLevel);
	}
	
	/**
	 * Create domain boss
	 */
	public static Enemy createDomainBoss(String domain, int playerLevel) {
		if (!initialized) initialize();
		
		List<EnemyTemplate> templates = domainEnemies.getOrDefault(domain, domainEnemies.get("Central Domain"));
		EnemyTemplate bossTemplate = templates.get(templates.size() - 1); // Last one is always boss
		
		Enemy boss = createEnemyFromTemplate(bossTemplate, playerLevel);
		boss.setBoss(true);
		
		// Add boss-specific abilities
		boss.addSpecialAbility("Boss Roar");
		boss.addSpecialAbility(bossTemplate.element.substring(0, 1).toUpperCase() + bossTemplate.element.substring(1) + " Blast");
		boss.setIntelligence(8);
		
		return boss;
	}
	
	/**
	 * Create enemy from template with level scaling
	 */
	private static Enemy createEnemyFromTemplate(EnemyTemplate template, int playerLevel) {
		// Scale stats based on player level
		double levelMultiplier = 1.0 + (playerLevel - 1) * 0.15;
		
		int scaledHp = (int)(template.hp * levelMultiplier);
		int scaledAttack = (int)(template.attack * levelMultiplier);
		int scaledExp = (int)(template.exp * levelMultiplier);
		int scaledGold = (int)(template.gold * levelMultiplier);
		
		Enemy enemy = new Enemy(template.name, scaledHp, scaledAttack, scaledExp, scaledGold);
		enemy.setElement(template.element);
		enemy.setAIType(template.aiType);
		enemy.setDefense(playerLevel / 3);
		
		// Add element-specific properties
		setupElementalProperties(enemy, template.element);
		
		// Add level-appropriate abilities
		addAbilitiesBasedOnLevel(enemy, playerLevel, template.element);
		
		// Random loot
		addRandomLoot(enemy, playerLevel, template.element);
		
		return enemy;
	}
	
	/**
	 * Setup elemental resistances and weaknesses
	 */
	private static void setupElementalProperties(Enemy enemy, String element) {
		switch (element) {
			case "fire" -> {
				enemy.addResistance("fire");
				enemy.addWeakness("ice");
				enemy.addWeakness("water");
			}
			case "ice" -> {
				enemy.addResistance("ice");
				enemy.addWeakness("fire");
			}
			case "dark" -> {
				enemy.addResistance("dark");
				enemy.addWeakness("light");
			}
			case "light" -> {
				enemy.addResistance("light");
				enemy.addWeakness("dark");
			}
			case "divine" -> {
				enemy.addResistance("light");
				enemy.addResistance("holy");
				enemy.addWeakness("void");
			}
		}
	}
	
	/**
	 * Add abilities based on level and element
	 */
	private static void addAbilitiesBasedOnLevel(Enemy enemy, int level, String element) {
		// Basic elemental ability
		enemy.addSpecialAbility(element.substring(0, 1).toUpperCase() + element.substring(1) + " Blast");
		
		if (level > 10) {
			enemy.addSpecialAbility("Heal");
		}
		if (level > 20) {
			enemy.addSpecialAbility("Buff");
		}
		if (level > 30) {
			enemy.addSpecialAbility(element + " Storm");
		}
	}
	
	/**
	 * Add random loot based on level and element
	 */
	private static void addRandomLoot(Enemy enemy, int level, String element) {
		// Health/Mana potions
		if (ThreadLocalRandom.current().nextDouble() < 0.4) {
			enemy.addLoot(ItemFactory.createHealthPotion("Health Potion", 30 + level * 5));
		}
		if (ThreadLocalRandom.current().nextDouble() < 0.3) {
			enemy.addLoot(ItemFactory.createManaPotion("Mana Potion", 20 + level * 3));
		}
		
		// Equipment (lower chance)
		if (ThreadLocalRandom.current().nextDouble() < 0.15) {
			enemy.addLoot(ItemFactory.createRandomWeapon(level, element));
		}
		if (ThreadLocalRandom.current().nextDouble() < 0.1) {
			enemy.addLoot(ItemFactory.createArmor("Enemy.Enemy Items.Armor", 20 + level * 8, "Medium"));
		}
		
		// Materials
		if (ThreadLocalRandom.current().nextDouble() < 0.25) {
			enemy.addLoot(new Material(element + " Essence", "Magical", Math.min(10, level / 5 + 1)));
		}
	}
	
	/**
	 * Enemy.Enemy template for creation
	 */
	private static class EnemyTemplate {
		String name;
		int hp, attack, exp, gold;
		String element, aiType;
		
		EnemyTemplate(String name, int hp, int attack, int exp, int gold, String element, String aiType) {
			this.name = name;
			this.hp = hp;
			this.attack = attack;
			this.exp = exp;
			this.gold = gold;
			this.element = element;
			this.aiType = aiType;
		}
	}
}
