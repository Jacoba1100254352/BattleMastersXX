// ============== Item.java ==============

public class Item {
    private String name;
    private String effect;
    private int value;
    private int power;
    private int defense;
    private String element;
    
    public Item(String name, String effect, int value) {
        this.name = name;
        this.effect = effect;
        this.value = value;
    }
    
    public Item(String name, int power) {
        this.name = name;
        this.power = power;
        this.effect = "weapon";
    }
    
    public Item(String name, int defense, boolean isArmor) {
        this.name = name;
        this.defense = defense;
        this.effect = "armor";
    }
    
    public Item(String name, int power, String element) {
        this.name = name;
        this.power = power;
        this.element = element;
        this.effect = "weapon";
    }
    
    public Item(String name, int defense, String element, boolean isArmor) {
        this.name = name;
        this.defense = defense;
        this.element = element;
        this.effect = "armor";
    }
    
    // Copy constructor
    public Item(Item other) {
        this.name = other.name;
        this.effect = other.effect;
        this.value = other.value;
        this.power = other.power;
        this.defense = other.defense;
        this.element = other.element;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public String getEffect() { return effect; }
    public int getValue() { return value; }
    public int getPower() { return power; }
    public int getDefense() { return defense; }
    public String getElement() { return element; }
    
    public boolean isWeapon() { return power > 0; }
    public boolean isArmor() { return defense > 0; }
    public boolean isUsable() { 
        return "heal".equals(effect) || "mana".equals(effect) || "full_heal".equals(effect); 
    }
}