package Items;

// ================================================================================
// COMPLETE ITEM SYSTEM - Should be split into multiple files
// Enhanced with rarity, weight, and special properties
// ================================================================================

import java.util.*;

/**
 * Enhanced base Items.Item class with comprehensive properties
 */
public abstract class Item {
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
