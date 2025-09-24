// ================================================================================
// COMPLETE ITEM SYSTEM - Should be split into multiple files
// Enhanced with rarity, weight, and special properties
// ================================================================================
import java.util.*;
/**
 * Enhanced base Item class with comprehensive properties
 */
abstract class Item {
    protected String name;
    protected String description;
    protected String type;
    protected int value;
    protected int weight;
    protected boolean isQuestItem;
    protected String rarity; // Common, Uncommon, Rare, Epic, Legendary, Mythic
    protected Map<String, String> properties;
    
    public Item(String name, String description, String type, int value, int weight) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.weight = weight;
        this.isQuestItem = false;
        this.rarity = "Common";
        this.properties = new HashMap<>();
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getValue() { return value; }
    public int getWeight() { return weight; }
    public boolean isQuestItem() { return isQuestItem; }
    public String getRarity() { return rarity; }
    public Map<String, String> getProperties() { return properties; }
    
    // Setters
    public void setQuestItem(boolean isQuestItem) { this.isQuestItem = isQuestItem; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public void addProperty(String key, String value) { this.properties.put(key, value); }
}

/**
 * Enhanced Weapon class with elemental damage and special effects
 */
class Weapon extends Item {
    private int power;
    private String element;
    private Map<String, Integer> bonuses;
    private List<String> specialEffects;
    private int durability;
    private int maxDurability;
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
     * Use weapon and reduce durability
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
     * Repair weapon
     */
    public void repair(int amount) {
        durability = Math.min(maxDurability, durability + amount);
        System.out.println(name + " has been repaired!");
    }
    
    /**
     * Get effective power based on durability
     */
    public int getEffectivePower() {
        if (durability == 0) return 0;
        return (int)(power * (durability / (double)maxDurability));
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

/**
 * Enhanced Armor class with set bonuses and durability
 */
class Armor extends Item {
    private int defense;
    private Map<String, Integer> resistances;
    private Map<String, Integer> bonuses;
    private String armorType; // Light, Medium, Heavy
    private int durability;
    private int maxDurability;
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
     * Take damage and reduce durability
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
     * Get effective defense based on durability
     */
    public int getEffectiveDefense() {
        if (durability == 0) return 0;
        return (int)(defense * (durability / (double)maxDurability));
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

/**
 * Enhanced Consumable class with cooldowns and stacking effects
 */
abstract class Consumable extends Item {
    protected int cooldown; // Seconds before can use again
    protected boolean canStack; // Can multiple effects stack
    protected String effectType; // Type of effect for stacking rules
    
    public Consumable(String name, String description, int value, int weight) {
        super(name, description, "Consumable", value, weight);
        this.cooldown = 0;
        this.canStack = false;
        this.effectType = "basic";
    }
    
    public abstract void use(Player player);
    
    public int getCooldown() { return cooldown; }
    public boolean canStack() { return canStack; }
    public String getEffectType() { return effectType; }
    
    protected void setCooldown(int cooldown) { this.cooldown = cooldown; }
    protected void setCanStack(boolean canStack) { this.canStack = canStack; }
    protected void setEffectType(String effectType) { this.effectType = effectType; }
}

/**
 * Enhanced Health Potion with different tiers
 */
class HealthPotion extends Consumable {
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

/**
 * Enhanced Mana Potion with different effects
 */
class ManaPotion extends Consumable {
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

/**
 * Enhanced Accessory class with set bonuses and enchantments
 */
class Accessory extends Item {
    private Map<String, Integer> bonuses;
    private List<String> effects;
    private String accessoryType; // Ring, Amulet, Charm, etc.
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

/**
 * Enhanced Material class for crafting
 */
class Material extends Item {
    private String materialType; // Metal, Gem, Organic, Magical
    private int tier; // 1-10, higher tiers for better items
    private boolean isRare;
    
    public Material(String name, String materialType, int tier) {
        super(name, String.format("A %s material of tier %d", materialType, tier), "Material", tier * 25, 1);
        this.materialType = materialType;
        this.tier = tier;
        this.isRare = tier > 7;
    }
    
    public String getMaterialType() { return materialType; }
    public int getTier() { return tier; }
    public boolean isRare() { return isRare; }
}