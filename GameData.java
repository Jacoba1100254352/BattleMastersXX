// ============== GameData.java ==============

import java.util.*;

public class GameData {
    public static final Map<String, Map<String, Object>> DOMAINS = createDomains();
    public static final Map<String, List<String>> LOCATIONS = createLocations();
    public static final Map<String, Integer> LOCATION_DISTANCES = createLocationDistances();
    public static final List<Item> WEAPONS = createWeapons();
    public static final List<Item> ADVANCED_WEAPONS = createAdvancedWeapons();
    public static final List<Item> MAGIC_WEAPONS = createMagicWeapons();
    public static final List<Item> ARMOR = createArmor();
    public static final List<Item> ITEMS = createItems();
    public static final List<Horse> HORSES = createHorses();
    public static final List<String> FACTIONS = Arrays.asList(
        "Knights of the Phoenix", "Shadow Brotherhood", "Order of the Grove"
    );
    public static final Map<String, Map<String, Object>> SPELLS = createSpells();
    public static final String[] SURGICAL_INJURIES = {
        "compound fracture of the leg", "arrow wound to the abdomen", "sword slash to the face",
        "infected battle wound", "severed finger", "crushed hand from mace impact"
    };
    
    private static Map<String, Map<String, Object>> createDomains() {
        Map<String, Map<String, Object>> domains = new HashMap<>();
        
        // Central Domain
        Map<String, Object> central = new HashMap<>();
        central.put("realm", "regular");
        central.put("theme", "Starting area with basic facilities");
        central.put("locations", Arrays.asList("Town Square", "Blacksmith", "Magic Shop", 
            "Quest Hall", "Armoury", "Prestige Hall", "Stable", "Hospital"));
        central.put("domain_boss", "Central Guardian");
        central.put("special_shops", Arrays.asList("Basic Gear Shop", "Potion Emporium"));
        domains.put("Central Domain", central);
        
        // Forest Domain
        Map<String, Object> forest = new HashMap<>();
        forest.put("realm", "regular");
        forest.put("theme", "Lush forests with nature magic");
        forest.put("locations", Arrays.asList("Forest Entrance", "Ancient Grove", 
            "Treant Village", "Hidden Glade", "Forest Temple"));
        forest.put("domain_boss", "Forest Titan");
        forest.put("special_shops", Arrays.asList("Nature's Arsenal", "Druid Supplies"));
        domains.put("Forest Domain", forest);
        
        // Add more domains as needed...
        return domains;
    }
    
    private static Map<String, List<String>> createLocations() {
        Map<String, List<String>> locations = new HashMap<>();
        // Initialize based on domains
        for (Map.Entry<String, Map<String, Object>> domainEntry : DOMAINS.entrySet()) {
            @SuppressWarnings("unchecked")
            List<String> domainLocations = (List<String>) domainEntry.getValue().get("locations");
            for (String location : domainLocations) {
                List<String> connections = new ArrayList<>(domainLocations);
                connections.remove(location);
                locations.put(location, connections);
            }
        }
        
        // Add special connections
        locations.get("Town Square").addAll(Arrays.asList("Forest Entrance", "Mountain Base", "Oasis Town", "Mythic Gate"));
        locations.put("Mythic Gate", Arrays.asList("Town Square", "Eternal Citadel"));
        locations.put("Oasis Town", Arrays.asList("Town Square", "Eternal Citadel", "Mythic Gate"));
        locations.put("Forest Enterance")
        
        return locations;
    }
    
    private static Map<String, Integer> createLocationDistances() {
        Map<String, Integer> distances = new HashMap<>();
        Random random = new Random();
        
        // Add some basic distances
        distances.put("Town Square-Forest Entrance", 15);
        distances.put("Town Square-Mountain Base", 20);
        distances.put("Town Square-Oasis Town", 25);
        distances.put("Town Square-Mythic Gate", 10);
        distances.put("Mythic Gate-Eternal Citadel", 30);
        
        return distances;
    }
    
    private static List<Item> createWeapons() {
        return Arrays.asList(
            new Item("Rusty Sword", 5),
            new Item("Steel Sword", 10),
            new Item("Flaming Axe", 20),
            new Item("Excalibur", 50),
            new Item("Dark Bow", 100),
            new Item("Poison Scythe", 250),
            new Item("Demon Trident", 500),
            new Item("Black Magic Sword", 750),
            new Item("Blood Bending Gauntlet", 1000)
        );
    }
    
    private static List<Item> createAdvancedWeapons() {
        return Arrays.asList(
            new Item("Soul Reaper", 1200),
            new Item("Void Blade", 1500),
            new Item("Titan Hammer", 1800),
            new Item("Lightning Spear", 2100),
            new Item("Frost Claymore", 2400),
            new Item("Shadow Katana", 2700),
            new Item("Phoenix Blade", 3000),
            new Item("Celestial Bow", 3300),
            new Item("Doomsday Axe", 3600)
        );
    }
    
    private static List<Item> createMagicWeapons() {
        return Arrays.asList(
            new Item("Wand of Light", 300),
            new Item("Black Magic Powder", 500),
            new Item("Staff of Wisdom", 600),
            new Item("Spirit of Shadows", 1000),
            new Item("Orb of Chaos", 1200),
            new Item("Staff of Ziard", 2000),
            new Item("Fire Wand", 2400),
            new Item("Snake Scepter", 3000),
            new Item("Rune of Time", 3500),
            new Item("Spear of Destiny", 4000),
            new Item("Staff of the Ancients", 4800),
            new Item("The Bramasthra", 5000)
        );
    }
    
    private static List<Item> createArmor() {
        return Arrays.asList(
            new Item("Cloth Armor", 2, true),
            new Item("Leather Armor", 5, true),
            new Item("Chainmail", 10, true),
            new Item("Golden Silver", 25, true),
            new Item("Lava Armor", 70, true),
            new Item("Phoenix Moon Scale", 250, true),
            new Item("Dragon Plate", 400, true),
            new Item("Titanium Armor", 600, true),
            new Item("Mystic Robes", 800, true),
            new Item("Void Armor", 1000, true),
            new Item("Celestial Plate", 1300, true),
            new Item("Divine Guardian Armor", 1600, true),
            new Item("Godly Protection Suit", 2000, true)
        );
    }
    
    private static List<Item> createItems() {
        return Arrays.asList(
            new Item("Health Potion", "heal", 30),
            new Item("Mana Potion", "mana", 20),
            new Item("Greater Health Potion", "heal", 60),
            new Item("Greater Mana Potion", "mana", 40),
            new Item("Super Health Potion", "heal", 100),
            new Item("Super Mana Potion", "mana", 80),
            new Item("Ultimate Health Elixir", "heal", 200),
            new Item("Ultimate Mana Elixir", "mana", 150),
            new Item("Full Restore Potion", "full_heal", 999),
            new Item("Damage Potion", "damage", 50),
            new Item("Greater Damage Potion", "damage", 100),
            new Item("Gemstone", "none", 0),
            new Item("Silver Amulet", "none", 0),
            new Item("Blue Glowing Gemstone", "none", 0)
        );
    }
    
    private static List<Horse> createHorses() {
        return Arrays.asList(
            new Horse("Old Nag", 0.5, 100),
            new Horse("Farm Horse", 1.0, 500),
            new Horse("Riding Horse", 1.5, 1200),
            new Horse("War Horse", 2.0, 2500),
            new Horse("Arabian Stallion", 3.0, 5000),
            new Horse("Pegasus", 5.0, 15000),
            new Horse("Shadow Steed", 10.0, 50000)
        );
    }
    
    private static Map<String, Map<String, Object>> createSpells() {
        Map<String, Map<String, Object>> spells = new HashMap<>();
        
        Map<String, Object> fireball = new HashMap<>();
        fireball.put("cost", 15);
        fireball.put("damage", 25);
        fireball.put("element", "fire");
        spells.put("fireball", fireball);
        
        Map<String, Object> iceShard = new HashMap<>();
        iceShard.put("cost", 10);
        iceShard.put("damage", 15);
        iceShard.put("element", "ice");
        spells.put("ice shard", iceShard);
        
        Map<String, Object> heal = new HashMap<>();
        heal.put("cost", 20);
        heal.put("heal", 30);
        spells.put("heal", heal);
        
        Map<String, Object> megaHeal = new HashMap<>();
        megaHeal.put("cost", 50);
        megaHeal.put("heal", 80);
        spells.put("mega heal", megaHeal);
        
        return spells;
    }
}