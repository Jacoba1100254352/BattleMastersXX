// ================================================================================
// ENHANCED ENEMY SYSTEM - Should be in Enemy.java
// Complete enemy system with AI behavior and advanced mechanics
// ================================================================================
import java.util.*;
/**
 * Enhanced Enemy class
 */
class Enemy {
    private String name;
    private int hp, maxHp;
    private int attackPower;
    private int defense;
    private int expReward;
    private int goldReward;
    private boolean isBoss;
    private String element;
    private List<String> resistances;
    private List<String> weaknesses;
    private Map<String, StatusEffect> statusEffects;
    private List<Item> lootTable;
    private List<String> specialAbilities;
    private String aiType; // Aggressive, Defensive, Tactical, Berserker
    private int intelligence; // Affects AI decision making
    private boolean hasUsedSpecialThisTurn;
    
    public Enemy(String name, int hp, int attackPower, int expReward, int goldReward) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = 0;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.isBoss = false;
        this.element = "neutral";
        this.resistances = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.statusEffects = new HashMap<>();
        this.lootTable = new ArrayList<>();
        this.specialAbilities = new ArrayList<>();
        this.aiType = "Aggressive";
        this.intelligence = 5;
        this.hasUsedSpecialThisTurn = false;
    }
    
    /**
     * Enhanced AI attack system
     */
    public EnemyAction decideAction(Player player) {
        hasUsedSpecialThisTurn = false;
        
        // Check if should use special ability
        if (!specialAbilities.isEmpty() && shouldUseSpecialAbility(player)) {
            return useSpecialAbility(player);
        }
        
        // AI behavior based on type
        switch (aiType) {
            case "Defensive" -> {
                if (hp < maxHp * 0.3 && hasHealingAbility()) {
                    return new EnemyAction("heal", 0, "The " + name + " heals itself!");
                }
            }
            case "Tactical" -> {
                if (player.getHp() > player.getMaxHp() * 0.7 && hasDebuffAbility()) {
                    return new EnemyAction("debuff", 0, "The " + name + " weakens you!");
                }
            }
            case "Berserker" -> {
                if (hp < maxHp * 0.5) {
                    int berserkDamage = (int)(attackPower * 1.5);
                    return new EnemyAction("berserk_attack", berserkDamage, "The " + name + " enters a rage!");
                }
            }
        }
        
        // Default attack
        return new EnemyAction("attack", calculateDamage(), "The " + name + " attacks!");
    }
    
    /**
     * Calculate attack damage with variance
     */
    private int calculateDamage() {
        int baseDamage = attackPower;
        
        // Add random variance
        int variance = ThreadLocalRandom.current().nextInt(-attackPower/4, attackPower/4 + 1);
        baseDamage += variance;
        
        // Status effect modifications
        if (statusEffects.containsKey("weakened")) {
            baseDamage = (int)(baseDamage * 0.7);
        }
        if (statusEffects.containsKey("enraged")) {
            baseDamage = (int)(baseDamage * 1.3);
        }
        
        return Math.max(1, baseDamage);
    }
    
    /**
     * Determine if enemy should use special ability
     */
    private boolean shouldUseSpecialAbility(Player player) {
        if (hasUsedSpecialThisTurn) return false;
        
        double useChance = 0.2 + (intelligence * 0.02); // Base 20% + intelligence bonus
        
        // Increase chance if low health
        if (hp < maxHp * 0.3) useChance += 0.3;
        
        // Increase chance if player has high health
        if (player.getHp() > player.getMaxHp() * 0.8) useChance += 0.2;
        
        return ThreadLocalRandom.current().nextDouble() < useChance;
    }
    
    /**
     * Use a random special ability
     */
    private EnemyAction useSpecialAbility(Player player) {
        String ability = specialAbilities.get(ThreadLocalRandom.current().nextInt(specialAbilities.size()));
        hasUsedSpecialThisTurn = true;
        
        switch (ability) {
            case "Fire Blast" -> {
                int damage = (int)(attackPower * 1.4);
                return new EnemyAction("fire_blast", damage, name + " unleashes a fire blast!", "fire");
            }
            case "Ice Storm" -> {
                int damage = attackPower;
                return new EnemyAction("ice_storm", damage, name + " summons an ice storm!", "ice", "freeze");
            }
            case "Lightning Strike" -> {
                int damage = (int)(attackPower * 1.2);
                return new EnemyAction("lightning", damage, name + " calls down lightning!", "lightning", "stun");
            }
            case "Poison Spit" -> {
                int damage = attackPower / 2;
                return new EnemyAction("poison", damage, name + " spits venom!", "poison", "poison");
            }
            case "Heal" -> {
                int healAmount = maxHp / 4;
                heal(healAmount);
                return new EnemyAction("heal", 0, name + " recovers health!");
            }
            case "Buff" -> {
                addStatusEffect("enraged", new EnrageEffect(5));
                return new EnemyAction("buff", 0, name + " becomes enraged!");
            }
            default -> {
                return new EnemyAction("attack", calculateDamage(), name + " attacks!");
            }
        }
    }
    
    /**
     * Take damage with elemental considerations
     */
    public void takeDamage(int damage, String element) {
        // Apply resistances and weaknesses
        if (resistances.contains(element)) {
            damage = (int)(damage * 0.5);
            System.out.println(name + " resists " + element + " damage!");
        } else if (weaknesses.contains(element)) {
            damage = (int)(damage * 1.5);
            System.out.println(name + " is vulnerable to " + element + " damage!");
        }
        
        // Apply defense
        int actualDamage = Math.max(1, damage - defense);
        
        // Status effect modifications
        if (statusEffects.containsKey("vulnerable")) {
            actualDamage = (int)(actualDamage * 1.3);
        }
        
        this.hp = Math.max(0, this.hp - actualDamage);
        System.out.printf("%s takes %d %s damage! HP: %d/%d\n", name, actualDamage, element, hp, maxHp);
        
        // React to taking damage
        reactToDamage(actualDamage);
    }
    
    /**
     * React to taking damage (AI behavior)
     */
    private void reactToDamage(int damage) {
        if (damage > attackPower && aiType.equals("Berserker")) {
            if (!statusEffects.containsKey("enraged")) {
                addStatusEffect("enraged", new EnrageEffect(3));
                System.out.println(name + " becomes enraged from the heavy damage!");
            }
        }
    }
    
    /**
     * Heal the enemy
     */
    public void heal(int amount) {
        int oldHp = this.hp;
        this.hp = Math.min(this.maxHp, this.hp + amount);
        int actualHealing = this.hp - oldHp;
        
        if (actualHealing > 0) {
            System.out.printf("%s heals for %d HP!\n", name, actualHealing);
        }
    }
    
    /**
     * Process status effects at start of turn
     */
    public void processStatusEffects() {
        List<String> expiredEffects = new ArrayList<>();
        
        for (Map.Entry<String, StatusEffect> entry : statusEffects.entrySet()) {
            StatusEffect effect = entry.getValue();
            effect.apply(this);
            effect.decrementDuration();
            
            if (effect.isExpired()) {
                expiredEffects.add(entry.getKey());
            }
        }
        
        for (String effectName : expiredEffects) {
            statusEffects.remove(effectName);
            System.out.println(name + " recovers from " + effectName + ".");
        }
    }
    
    /**
     * Check if enemy has specific abilities
     */
    private boolean hasHealingAbility() {
        return specialAbilities.contains("Heal");
    }
    
    private boolean hasDebuffAbility() {
        return specialAbilities.stream().anyMatch(ability -> 
            ability.contains("Poison") || ability.contains("Weaken") || ability.contains("Curse"));
    }
    
    /**
     * Get loot when defeated
     */
    public List<Item> getLoot() {
        List<Item> droppedLoot = new ArrayList<>();
        
        // Random chance for each item in loot table
        for (Item item : lootTable) {
            double dropChance = isBoss ? 0.7 : 0.3;
            if (ThreadLocalRandom.current().nextDouble() < dropChance) {
                droppedLoot.add(item);
            }
        }
        
        // Bosses guaranteed to drop something good
        if (isBoss && droppedLoot.isEmpty()) {
            droppedLoot.add(generateBossLoot());
        }
        
        // Random gold bonus
        if (ThreadLocalRandom.current().nextDouble() < 0.5) {
            int bonusGold = ThreadLocalRandom.current().nextInt(goldReward / 4, goldReward / 2);
            goldReward += bonusGold;
        }
        
        return droppedLoot;
    }
    
    /**
     * Generate special boss loot
     */
    private Item generateBossLoot() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            // Boss weapon
            Weapon weapon = ItemFactory.createWeapon("Boss " + element + " Weapon", 
                300 + ThreadLocalRandom.current().nextInt(300), element);
            weapon.setRarity("Epic");
            weapon.addSpecialEffect("Boss Slayer");
            return weapon;
        } else {
            // Boss armor
            Armor armor = ItemFactory.createArmor("Boss Guard", 
                200 + ThreadLocalRandom.current().nextInt(200), "Heavy");
            armor.setRarity("Epic");
            armor.addBonus("boss_resistance", 25);
            return armor;
        }
    }
    
    // Status effect management
    public void addStatusEffect(String name, StatusEffect effect) {
        statusEffects.put(name, effect);
    }
    
    public void removeStatusEffect(String name) {
        statusEffects.remove(name);
    }
    
    // Getters and setters
    public boolean isAlive() { return hp > 0; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public boolean isBoss() { return isBoss; }
    public String getElement() { return element; }
    public List<String> getSpecialAbilities() { return specialAbilities; }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
    
    public void setBoss(boolean isBoss) { this.isBoss = isBoss; }
    public void setElement(String element) { this.element = element; }
    public void addResistance(String element) { this.resistances.add(element); }
    public void addWeakness(String element) { this.weaknesses.add(element); }
    public void addLoot(Item item) { this.lootTable.add(item); }
    public void addSpecialAbility(String ability) { this.specialAbilities.add(ability); }
    public void setAIType(String aiType) { this.aiType = aiType; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public void setDefense(int defense) { this.defense = defense; }
}

/**
 * Enemy action class for AI decisions
 */
class EnemyAction {
    private String actionType;
    private int damage;
    private String message;
    private String element;
    private String statusEffect;
    
    public EnemyAction(String actionType, int damage, String message) {
        this(actionType, damage, message, "physical", null);
    }
    
    public EnemyAction(String actionType, int damage, String message, String element) {
        this(actionType, damage, message, element, null);
    }
    
    public EnemyAction(String actionType, int damage, String message, String element, String statusEffect) {
        this.actionType = actionType;
        this.damage = damage;
        this.message = message;
        this.element = element;
        this.statusEffect = statusEffect;
    }
    
    // Getters
    public String getActionType() { return actionType; }
    public int getDamage() { return damage; }
    public String getMessage() { return message; }
    public String getElement() { return element; }
    public String getStatusEffect() { return statusEffect; }
}

/**
 * Enhanced Enemy Factory with domain-specific creation
 */
class EnemyFactory {
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
            enemy.addLoot(ItemFactory.createArmor("Enemy Armor", 20 + level * 8, "Medium"));
        }
        
        // Materials
        if (ThreadLocalRandom.current().nextDouble() < 0.25) {
            enemy.addLoot(new Material(element + " Essence", "Magical", Math.min(10, level / 5 + 1)));
        }
    }
    
    /**
     * Enemy template for creation
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