package Items;// ================================================================================
// ITEM FACTORY - Should be in Items.ItemFactory.java
// Creates all items with proper configuration
// ================================================================================

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
public class ItemFactory {
    private static Map<String, String[]> weaponsByElement;
    private static Map<String, String[]> armorByType;
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) return;
        
        setupWeaponData();
        setupArmorData();
        initialized = true;
    }
    
    private static void setupWeaponData() {
        weaponsByElement = new HashMap<>();
        weaponsByElement.put("fire", new String[]{"Flame Sword", "Inferno Blade", "Phoenix Talon", "Molten Hammer", "Solar Spear"});
        weaponsByElement.put("ice", new String[]{"Frost Blade", "Glacier Edge", "Winter's Bite", "Arctic Hammer", "Frozen Spear"});
        weaponsByElement.put("lightning", new String[]{"Storm Sword", "Thunder Blade", "Lightning Rod", "Shock Hammer", "Bolt Spear"});
        weaponsByElement.put("earth", new String[]{"Stone Crusher", "Boulder Blade", "Mountain Edge", "Earth Shaker", "Terra Spear"});
        weaponsByElement.put("dark", new String[]{"Shadow Blade", "Void Cutter", "Darkness Edge", "Nightmare Hammer", "Soul Spear"});
        weaponsByElement.put("light", new String[]{"Holy Sword", "Divine Blade", "Radiant Edge", "Sacred Hammer", "Light Spear"});
        weaponsByElement.put("physical", new String[]{"Steel Sword", "Iron Blade", "Sharp Edge", "War Hammer", "Combat Spear"});
    }
    
    private static void setupArmorData() {
        armorByType = new HashMap<>();
        armorByType.put("Light", new String[]{"Leather Vest", "Scout Items.Armor", "Assassin Garb", "Ranger Cloak", "Thief Suit"});
        armorByType.put("Medium", new String[]{"Chain Mail", "Scale Items.Armor", "Reinforced Vest", "Battle Garb", "War Cloak"});
        armorByType.put("Heavy", new String[]{"Plate Items.Armor", "Full Plate", "Knight Items.Armor", "Guardian Plate", "Fortress Mail"});
    }
    
    /**
     * Create a weapon with specified parameters
     */
    public static Weapon createWeapon(String name, int power, String element) {
        Weapon weapon = new Weapon(name, power, element, getWeaponWeight(power));
        
        // Add rarity based on power
        if (power > 1000) weapon.setRarity("Legendary");
        else if (power > 500) weapon.setRarity("Epic");
        else if (power > 200) weapon.setRarity("Rare");
        else if (power > 50) weapon.setRarity("Uncommon");
        
        // Add special effects for high-power weapons
        if (power > 300) {
            weapon.addSpecialEffect("Enhanced " + element + " damage");
        }
        if (power > 600) {
            weapon.setCriticalChance(0.15); // 15% crit chance
        }
        
        return weapon;
    }
    
    /**
     * Create a random weapon for a given level and element
     */
    public static Weapon createRandomWeapon(int level, String element) {
        String[] names = weaponsByElement.getOrDefault(element, weaponsByElement.get("physical"));
        String name = names[ThreadLocalRandom.current().nextInt(names.length)];
        
        int basePower = 20 + (level * 8) + ThreadLocalRandom.current().nextInt(10, 30);
        int weight = getWeaponWeight(basePower);
        
        Weapon weapon = new Weapon(name, basePower, element, weight);
        
        // Random bonuses
        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            weapon.addBonus("critical_chance", ThreadLocalRandom.current().nextInt(5, 15));
        }
        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            weapon.addSpecialEffect("Vampiric");
        }
        
        return weapon;
    }
    
    /**
     * Create armor with specified parameters
     */
    public static Armor createArmor(String name, int defense, String type) {
        int weight = getArmorWeight(defense, type);
        Armor armor = new Armor(name, defense, type, weight);
        
        // Add rarity based on defense
        if (defense > 800) armor.setRarity("Legendary");
        else if (defense > 400) armor.setRarity("Epic");
        else if (defense > 150) armor.setRarity("Rare");
        else if (defense > 50) armor.setRarity("Uncommon");
        
        // Add resistances based on type
        switch (type) {
            case "Light" -> {
                armor.addResistance("physical", 5);
                armor.addBonus("agility", 10);
            }
            case "Medium" -> {
                armor.addResistance("physical", 15);
                armor.addResistance("fire", 10);
            }
            case "Heavy" -> {
                armor.addResistance("physical", 25);
                armor.addResistance("ice", 15);
                armor.addBonus("strength", 5);
            }
        }
        
        return armor;
    }
    
    /**
     * Create a health potion
     */
    public static HealthPotion createHealthPotion(String name, int healAmount) {
        HealthPotion potion = new HealthPotion(name, healAmount);
        
        // Enhanced potions for higher heal amounts
        if (healAmount > 100) {
            potion.setOverTimeHealing(5);
            potion.setRarity("Rare");
        }
        
        return potion;
    }
    
    /**
     * Create a mana potion
     */
    public static ManaPotion createManaPotion(String name, int manaAmount) {
        ManaPotion potion = new ManaPotion(name, manaAmount);
        
        // Enhanced potions for higher mana amounts
        if (manaAmount > 80) {
            potion.setSpellPowerBoost(10);
            potion.setRarity("Rare");
        }
        
        return potion;
    }
    
    /**
     * Create a random treasure item
     */
    public static Item createRandomTreasure(int level) {
        String[] treasureNames = {
            "Ancient Coin", "Mystic Crystal", "Dragon Scale", "Phoenix Feather",
            "Void Essence", "Starlight Fragment", "Demon Horn", "Angel Wing",
            "Time Shard", "Reality Gem", "Soul Stone", "Life Crystal"
        };
        
        String name = treasureNames[ThreadLocalRandom.current().nextInt(treasureNames.length)];
        int value = 100 + (level * 25) + ThreadLocalRandom.current().nextInt(50, 200);
        
        Material treasure = new Material(name, "Magical", Math.min(10, level / 5 + 1));
        treasure.setRarity("Rare");
        
        return treasure;
    }
    
    // Helper methods
    private static int getWeaponWeight(int power) {
        return Math.max(1, power / 50);
    }
    
    private static int getArmorWeight(int defense, String type) {
        int baseWeight = defense / 10;
        return switch (type) {
            case "Light" -> Math.max(2, baseWeight / 2);
            case "Medium" -> Math.max(4, baseWeight);
            case "Heavy" -> Math.max(8, baseWeight * 2);
            default -> baseWeight;
        };
    }
}