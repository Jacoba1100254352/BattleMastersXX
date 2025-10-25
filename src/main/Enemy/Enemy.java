package Enemy;

// ================================================================================
// ENHANCED ENEMY SYSTEM - Should be in Enemy.Enemy.java
// Complete enemy system with AI behavior and advanced mechanics
// ================================================================================

import Effects.EnrageEffect;
import Effects.StatusEffect;
import Items.Armor;
import Items.Item;
import Items.ItemFactory;
import Items.Weapon;
import Player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Enhanced Enemy class.
 */
public class Enemy {
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
            Weapon weapon = ItemFactory.createWeapon("Boss " + element + " Items.Weapon",
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
