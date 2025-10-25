package Scene;

// ================================================================================
// COMPLETE GAME WORLD SYSTEM - Should be in Scene.GameWorld.java
// Contains all 28 domains, locations, and world management
// ================================================================================

import Achievements.AchievementSystem;
import Combat.CombatSystem;
import Effects.StatusEffect;
import Enemy.Enemy;
import Enemy.EnemyFactory;
import Items.Item;
import Items.ItemFactory;
import Items.Material;
import Player.Horse;
import Player.Player;
import Scene.support.NPC;
import Scene.support.NPCSystem;
import Scene.support.Shop;
import Scene.support.ShopSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Complete Game World with 28 domains and advanced location system
 */
public class GameWorld {
    private Map<String, Domain> domains;
    private Map<String, List<String>> locationConnections;
    private Map<String, Integer> travelTimes;
    private static Scanner scanner = new Scanner(System.in);
    
    public GameWorld() {
        initializeDomains();
        initializeConnections();
        initializeTravelTimes();
        EnemyFactory.initialize();
    }
    
    /**
     * Initialize all 28 domains with their complete data
     */
    private void initializeDomains() {
        domains = new LinkedHashMap<>();
        
        // REGULAR REALM DOMAINS (20 domains)
        domains.put("Central Scene.Domain", new Domain("Central Scene.Domain", "Regular",
            "Starting area with basic facilities and safety",
            List.of("Town Square", "Blacksmith", "Magic Shop", "Quests.Quest Hall", "Armory", "Prestige Hall", "Stable", "Hospital", "Market", "Inn"),
            "Central Guardian",
            List.of("Basic Gear Shop", "Potion Emporium", "Starter Equipment")));
            
        domains.put("Forest Scene.Domain", new Domain("Forest Scene.Domain", "Regular",
            "Lush forests with nature magic and ancient secrets",
            List.of("Forest Entrance", "Ancient Grove", "Treant Village", "Hidden Glade", "Forest Temple", "Druid Circle", "Mystic Falls"),
            "Forest Titan",
            List.of("Nature's Arsenal", "Druid Supplies", "Herbal Medicine")));
            
        domains.put("Mountain Scene.Domain", new Domain("Mountain Scene.Domain", "Regular",
            "High peaks and rocky terrain with dwarven settlements",
            List.of("Mountain Base", "Rocky Cliffs", "Peak Summit", "Dragon's Lair", "Crystal Caves", "Dwarven Halls", "Sky Bridge"),
            "Mountain King",
            List.of("Peak Armory", "Crystal Forge", "Dwarven Smithy")));
            
        domains.put("Desert Scene.Domain", new Domain("Desert Scene.Domain", "Regular",
            "Scorching sands with ancient ruins and nomadic tribes",
            List.of("Oasis Town", "Sand Dunes", "Ancient Pyramid", "Mirage Valley", "Buried Temple", "Nomad Camp", "Sphinx Lair"),
            "Sand Pharaoh",
            List.of("Desert Trader", "Mirage Market", "Nomad Supplies")));
            
        domains.put("Swamp Scene.Domain", new Domain("Swamp Scene.Domain", "Regular",
            "Murky wetlands with toxic atmosphere and dark magic",
            List.of("Swamp Village", "Poison Marshes", "Witch's Hut", "Bog Depths", "Crocodile Lair", "Cursed Grove", "Alchemist Lab"),
            "Swamp Lord",
            List.of("Toxic Weaponry", "Marsh Medicine", "Poison Crafts")));
            
        domains.put("Ice Scene.Domain", new Domain("Ice Scene.Domain", "Regular",
            "Frozen wasteland with ice magic and eternal winter",
            List.of("Frozen Village", "Ice Caverns", "Glacier Plains", "Aurora Peak", "Ice Palace", "Yeti Caves", "Frost Temple"),
            "Frost Emperor",
            List.of("Frost Forge", "Arctic Outfitters", "Ice Crystal Shop")));
            
        domains.put("Volcano Scene.Domain", new Domain("Volcano Scene.Domain", "Regular",
            "Fiery peaks with lava flows and forge masters",
            List.of("Lava Village", "Magma Chambers", "Volcano Crater", "Fire Temple", "Molten Core", "Phoenix Nest", "Forge of Kings"),
            "Magma King",
            List.of("Inferno Weapons", "Fire Forge", "Molten Crafts")));
            
        domains.put("Ocean Scene.Domain", new Domain("Ocean Scene.Domain", "Regular",
            "Vast seas with underwater cities and sea creatures",
            List.of("Port Town", "Coral Reef", "Underwater City", "Deep Trench", "Kraken's Scene.Domain", "Mermaid Palace", "Pirate Cove"),
            "Sea Emperor",
            List.of("Nautical Gear", "Pearl Merchant", "Sea Treasures")));
            
        domains.put("Sky Scene.Domain", new Domain("Sky Scene.Domain", "Regular",
            "Floating islands in the clouds with wind magic",
            List.of("Cloud Village", "Wind Temple", "Sky Bridge", "Storm Citadel", "Heaven's Gate", "Eagle's Nest", "Star Observatory"),
            "Storm Lord",
            List.of("Wind Walker Gear", "Cloud Forge", "Sky Crystals")));
            
        domains.put("Underground Scene.Domain", new Domain("Underground Scene.Domain", "Regular",
            "Deep caverns and mining tunnels with earth magic",
            List.of("Mining Town", "Crystal Caverns", "Underground Lake", "Dwarven Halls", "Deep Core", "Gem Mines", "Earth Temple"),
            "Earth Titan",
            List.of("Dwarven Smith", "Gem Cutter", "Earth Forge")));
            
        domains.put("Shadow Scene.Domain", new Domain("Shadow Scene.Domain", "Regular",
            "Dark realm of shadows with stealth and void magic",
            List.of("Shadow Village", "Dark Forest", "Void Temple", "Shadow Maze", "Darkness Core", "Assassin Guild", "Nightmare Realm"),
            "Shadow Master",
            List.of("Shadow Weapons", "Void Arsenal", "Dark Arts Shop")));
            
        domains.put("Light Scene.Domain", new Domain("Light Scene.Domain", "Regular",
            "Radiant realm of holy magic and divine power",
            List.of("Temple City", "Sacred Grove", "Light Spire", "Angel Sanctuary", "Divine Throne", "Holy Cathedral", "Paladin Hall"),
            "Light Seraph",
            List.of("Holy Armaments", "Divine Forge", "Sacred Relics")));
            
        domains.put("Time Scene.Domain", new Domain("Time Scene.Domain", "Regular",
            "Temporal magic and time manipulation mysteries",
            List.of("Clockwork City", "Past Ruins", "Future Lab", "Time Vortex", "Temporal Core", "Chrono Tower", "Paradox Chamber"),
            "Chrono Master",
            List.of("Temporal Weapons", "Time Forge", "Paradox Shop")));
            
        domains.put("Space Scene.Domain", new Domain("Space Scene.Domain", "Regular",
            "Cosmic void with stellar magic and alien mysteries",
            List.of("Space Station", "Asteroid Field", "Nebula Garden", "Black Hole", "Cosmic Center", "Star Forge", "Alien Outpost"),
            "Void Emperor",
            List.of("Cosmic Arsenal", "Star Forge", "Alien Technology")));
            
        domains.put("Dream Scene.Domain", new Domain("Dream Scene.Domain", "Regular",
            "Surreal dreamscape with illusion magic and mind games",
            List.of("Dream Village", "Nightmare Forest", "Lucid Lake", "Surreal City", "Dream Core", "Memory Palace", "Inception Chamber"),
            "Dream Weaver",
            List.of("Illusion Gear", "Dream Forge", "Mind Crystals")));
            
        domains.put("Ancient Scene.Domain", new Domain("Ancient Scene.Domain", "Regular",
            "Ruins of lost civilizations with archaeological treasures",
            List.of("Ruined City", "Ancient Library", "Lost Temple", "Forgotten Tomb", "Primordial Core", "Archaeology Site", "Relic Vault"),
            "Ancient Guardian",
            List.of("Relic Weapons", "Ancient Forge", "Archaeological Tools")));
            
        domains.put("Beast Scene.Domain", new Domain("Beast Scene.Domain", "Regular",
            "Wild lands ruled by magical creatures and nature spirits",
            List.of("Beast Village", "Wild Plains", "Creature Den", "Alpha Territory", "Primal Core", "Tamer's Lodge", "Spirit Grove"),
            "Beast King",
            List.of("Beast Gear", "Fang & Claw", "Tamer Supplies")));
            
        domains.put("Elemental Scene.Domain", new Domain("Elemental Scene.Domain", "Regular",
            "Pure elemental forces in perfect harmony",
            List.of("Elemental Nexus", "Fire Quarter", "Water Quarter", "Earth Quarter", "Air Quarter", "Spirit Quarter", "Balance Chamber"),
            "Elemental Avatar",
            List.of("Elemental Forge", "Essence Shop", "Pure Elements")));
            
        domains.put("Chaos Scene.Domain", new Domain("Chaos Scene.Domain", "Regular",
            "Unstable realm of pure chaos and random events",
            List.of("Chaos Village", "Random Plains", "Unstable Void", "Madness Peak", "Chaos Core", "Probability Lab", "Entropy Gate"),
            "Chaos Lord",
            List.of("Chaos Weapons", "Random Forge", "Uncertainty Shop")));
            
        domains.put("Order Scene.Domain", new Domain("Order Scene.Domain", "Regular",
            "Perfect realm of law, structure, and absolute order",
            List.of("Order City", "Law Temple", "Perfect Gardens", "Justice Hall", "Order Core", "Logic Center", "Harmony Plaza"),
            "Order Master",
            List.of("Law Weapons", "Order Forge", "Perfect Equipment")));
            
        // MYTHIC REALM DOMAINS (8 domains)
        domains.put("Eternal Scene.Domain", new Domain("Eternal Scene.Domain", "Mythic",
            "Timeless realm of immortal beings beyond mortal comprehension",
            List.of("Eternal Citadel", "Immortal Gardens", "Ageless Library", "Forever Falls", "Eternity Core", "Timeless Spire", "Infinity Pool"),
            "Eternal Emperor",
            List.of("Immortal Forge", "Eternal Arsenal", "Timeless Crafts")));
            
        domains.put("Cosmic Scene.Domain", new Domain("Cosmic Scene.Domain", "Mythic",
            "Universe-spanning powers and galactic civilizations",
            List.of("Galaxy Center", "Star Nursery", "Cosmic Web", "Universal Core", "Reality Nexus", "Dimension Gate", "Multiverse Hub"),
            "Cosmic Entity",
            List.of("Universe Forge", "Cosmic Workshop", "Reality Crafts")));
            
        domains.put("Divine Scene.Domain", new Domain("Divine Scene.Domain", "Mythic",
            "Realm of gods with ultimate divine power",
            List.of("Divine Palace", "God's Throne", "Heaven's Armory", "Celestial Court", "Divine Core", "Sacred Mountain", "Holy Sanctum"),
            "God Emperor",
            List.of("Divine Workshop", "God Forge", "Celestial Arsenal")));
            
        domains.put("Infernal Scene.Domain", new Domain("Infernal Scene.Domain", "Mythic",
            "Hellish realm of demons, devils, and eternal torment",
            List.of("Hell Gate", "Demon City", "Torture Chambers", "Devil's Throne", "Infernal Core", "Brimstone Forges", "Damnation Pit"),
            "Devil Emperor",
            List.of("Hellish Forge", "Demon Arsenal", "Infernal Crafts")));
            
        domains.put("Void Scene.Domain", new Domain("Void Scene.Domain", "Mythic",
            "Emptiness between realities where nothing exists",
            List.of("Void Center", "Nothing Plains", "Reality Tears", "Existence Edge", "Void Core", "Null Chamber", "Absence Shrine"),
            "Void Lord",
            List.of("Void Forge", "Nothingness Shop", "Null Crafts")));
            
        domains.put("Creation Scene.Domain", new Domain("Creation Scene.Domain", "Mythic",
            "Origin point of all existence and creative force",
            List.of("Genesis Point", "First Light", "Original Word", "Creator's Workshop", "Creation Core", "Birth Chamber", "Origin Fountain"),
            "Creator God",
            List.of("Genesis Forge", "Origin Workshop", "Creation Tools")));
            
        domains.put("Destruction Scene.Domain", new Domain("Destruction Scene.Domain", "Mythic",
            "End of all things and ultimate entropy",
            List.of("Apocalypse Gate", "Final Hour", "Last Breath", "Ending Point", "Destruction Core", "Entropy Engine", "Annihilation Chamber"),
            "Destroyer",
            List.of("Apocalypse Forge", "End Shop", "Destruction Tools")));
            
        domains.put("Origin Scene.Domain", new Domain("Origin Scene.Domain", "Mythic",
            "Source of the mythic gate system and reality itself",
            List.of("Gate of Origins", "Portal Nexus", "Mythic Source", "Reality Anchor", "Origin Core", "Gate Matrix", "Source Chamber"),
            "Gate Master",
            List.of("Origin Forge", "Gate Workshop", "Reality Tools")));
    }
    
    /**
     * Initialize location connections between domains
     */
    private void initializeConnections() {
        locationConnections = new HashMap<>();
        
        // Central Scene.Domain connections to other realms
        addConnection("Town Square", List.of("Forest Entrance", "Mountain Base", "Oasis Town", "Mythic Gate"));
        
        // Regular domain connections (each connects to nearby domains)
        addConnection("Forest Entrance", List.of("Town Square", "Swamp Village", "Beast Village"));
        addConnection("Mountain Base", List.of("Town Square", "Underground Mining Town", "Sky Cloud Village"));
        addConnection("Oasis Town", List.of("Town Square", "Volcano Lava Village"));
        
        // Mythic Gate - requires Prestige 10+
        addConnection("Mythic Gate", List.of("Town Square", "Eternal Citadel"));
        addConnection("Eternal Citadel", List.of("Mythic Gate", "Galaxy Center", "Divine Palace"));
        
        // Add all internal domain connections
        for (Domain domain : domains.values()) {
            for (String location : domain.getLocations()) {
                if (!locationConnections.containsKey(location)) {
                    List<String> connections = new ArrayList<>(domain.getLocations());
                    connections.remove(location); // Remove self
                    addConnection(location, connections);
                }
            }
        }
    }
    
    private void addConnection(String from, List<String> destinations) {
        locationConnections.put(from, destinations);
    }
    
    /**
     * Initialize travel times between locations
     */
    private void initializeTravelTimes() {
        travelTimes = new HashMap<>();
        
        // Base travel times (modified by horse speed)
        // Within domain: 2-8 hours
        // Between regular domains: 15-25 hours
        // To mythic domains: 30-50 hours
        
        // This would be expanded with specific times for each route
        // For brevity, using computed times in getTravelTime method
    }
    
    /**
     * Show available locations from current position
     */
    public void showAvailableLocations(Player player) {
        String currentLocation = player.getLocation();
        List<String> available = locationConnections.getOrDefault(currentLocation, new ArrayList<>());
        
        System.out.println("\n=== TRAVEL OPTIONS ===");
        System.out.printf("Current Location: %s (%s)\n", currentLocation, player.getCurrentDomain());
        
        if (available.isEmpty()) {
            System.out.println("No travel options available from this location.");
            return;
        }
        
        System.out.println("Available destinations:");
        for (int i = 0; i < available.size(); i++) {
            String destination = available.get(i);
            String destDomain = getDomainForLocation(destination);
            int travelTime = getTravelTime(currentLocation, destination, player.getHorse());
            String requirements = getTravelRequirements(destination, player);
            
            System.out.printf("%d. %s (%s) - %d hours%s\n", 
                i + 1, destination, destDomain, travelTime, requirements);
        }
    }
    
    /**
     * Get domain name for a location
     */
    private String getDomainForLocation(String location) {
        for (Map.Entry<String, Domain> entry : domains.entrySet()) {
            if (entry.getValue().getLocations().contains(location)) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }
    
    /**
     * Calculate travel time between locations
     */
    private int getTravelTime(String from, String to, Horse horse) {
        String fromDomain = getDomainForLocation(from);
        String toDomain = getDomainForLocation(to);
        
        int baseTime;
        if (fromDomain.equals(toDomain)) {
            baseTime = ThreadLocalRandom.current().nextInt(2, 9); // Within domain
        } else if (domains.get(toDomain).getRealm().equals("Mythic")) {
            baseTime = ThreadLocalRandom.current().nextInt(30, 51); // To mythic
        } else {
            baseTime = ThreadLocalRandom.current().nextInt(15, 26); // Between regular
        }
        
        double horseSpeed = horse != null ? horse.getSpeed() : 1.0;
        return Math.max(1, (int)(baseTime / horseSpeed));
    }
    
    /**
     * Get travel requirements for destination
     */
    private String getTravelRequirements(String destination, Player player) {
        String destDomain = getDomainForLocation(destination);
        Domain domain = domains.get(destDomain);
        
        if (domain == null) return "";
        
        if (domain.getRealm().equals("Mythic") && !player.isMythicMode()) {
            return " [LOCKED - Requires Prestige 10+]";
        }
        
        if (destination.equals("Mythic Gate") && player.getPrestige() < 10) {
            return " [LOCKED - Requires Prestige 10+]";
        }
        
        return "";
    }
    
    /**
     * Move player to new location
     */
    public void movePlayer(Player player, int choice) {
        String currentLocation = player.getLocation();
        List<String> available = locationConnections.getOrDefault(currentLocation, new ArrayList<>());
        
        if (choice < 1 || choice > available.size()) {
            System.out.println("Invalid destination choice.");
            return;
        }
        
        String destination = available.get(choice - 1);
        String requirements = getTravelRequirements(destination, player);
        
        if (requirements.contains("LOCKED")) {
            System.out.println("You cannot travel there yet! " + requirements);
            return;
        }
        
        // Check stamina
        int staminaCost = getTravelTime(currentLocation, destination, player.getHorse()) * 2;
        if (player.getStamina() < staminaCost) {
            System.out.println("You're too tired to make this journey! Rest first.");
            return;
        }
        
        // Initiate travel
        travelToLocation(player, destination);
    }
    
    /**
     * Handle travel mechanics with time progression
     */
    private void travelToLocation(Player player, String destination) {
        String origin = player.getLocation();
        int travelTime = getTravelTime(origin, destination, player.getHorse());
        int staminaCost = travelTime * 2;
        
        System.out.printf("\nTraveling from %s to %s...\n", origin, destination);
        if (player.getHorse() != null) {
            System.out.printf("Riding %s (%.1fx speed)\n", player.getHorse().getName(), player.getHorse().getSpeed());
        }
        
        System.out.printf("Journey will take %d hours and cost %d stamina.\n", travelTime, staminaCost);
        
        // Consume stamina
        player.consumeStamina(staminaCost);
        
        // Update location
        player.setLocation(destination);
        String newDomain = getDomainForLocation(destination);
        String oldDomain = player.getCurrentDomain();
        player.setCurrentDomain(newDomain);
        
        // Advance time (handled by main game loop)
        
        System.out.printf("\nArrived at %s!\n", destination);
        
        // Scene.Domain change announcement
        if (!oldDomain.equals(newDomain)) {
            System.out.printf("*** ENTERED %s ***\n", newDomain.toUpperCase());
            Domain domain = domains.get(newDomain);
            System.out.println(domain.getDescription());
            
            // Achievement for entering new domain
            AchievementSystem.checkDomainAchievement(player, newDomain);
        }
        
        // Trigger location events
        handleLocationEvents(player);
    }
    
    /**
     * Handle events when entering a location
     */
    private void handleLocationEvents(Player player) {
        String location = player.getLocation();
        String domain = player.getCurrentDomain();
        
        // Special location events
        switch (location) {
            case "Dragon's Lair" -> handleDragonLair(player);
            case "Mythic Gate" -> handleMythicGate(player);
            case "Gate of Origins" -> handleGateOfOrigins(player);
            case "Divine Palace" -> handleDivinePalace(player);
            case "Hell Gate" -> handleHellGate(player);
        }
        
        // Scene.Domain boss encounters (5% chance)
        if (ThreadLocalRandom.current().nextDouble() < 0.05) {
            Domain currentDomain = domains.get(domain);
            if (currentDomain != null && currentDomain.getDomainBoss() != null) {
                encounterDomainBoss(player, currentDomain);
            }
        }
        
        // Random exploration opportunities
        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            offerExplorationOpportunity(player);
        }
    }
    
    private void handleDragonLair(Player player) {
        System.out.println("\nThe ancient dragon's lair stretches before you...");
        System.out.println("Treasures glint in the darkness, but danger lurks nearby.");
        
        if (ThreadLocalRandom.current().nextDouble() < 0.6) {
            System.out.println("A mighty dragon emerges from the shadows!");
            Enemy dragon = EnemyFactory.createDomainBoss("Mountain Scene.Domain", player.getLevel());
            dragon.setElement("fire");
            dragon.addSpecialAbility("Dragon Breath");
            dragon.addSpecialAbility("Wing Buffet");
            
            CombatSystem combatSystem = new CombatSystem();
            combatSystem.initiateBattle(player, dragon);
        }
    }
    
    private void handleMythicGate(Player player) {
        if (!player.isMythicMode()) {
            System.out.println("\nA shimmering barrier blocks your path.");
            System.out.println("You sense great power beyond, but you are not yet ready.");
            System.out.println("Achieve Prestige 10 to unlock the Mythic Realm!");
            return;
        }
        
        System.out.println("\nThe Mythic Gate recognizes your power and opens before you.");
        System.out.println("Beyond lies realms of unimaginable power and danger...");
    }
    
    private void handleGateOfOrigins(Player player) {
        System.out.println("\nYou stand before the Gate of Origins, source of all realities.");
        System.out.println("Visions of the game's beginning flash before your eyes.");
        
        if (player.getPrestige() >= 25) {
            System.out.println("A voice whispers: 'The True Ending awaits the most dedicated.'");
            // Unlock secret ending quest
        }
    }
    
    private void handleDivinePalace(Player player) {
        System.out.println("\nCelestial music fills the air as you approach the Divine Palace.");
        System.out.println("Angels guard the entrance, their presence both beautiful and terrifying.");
        
        if (player.getFaction() != null && player.getFaction().getName().contains("Light")) {
            System.out.println("The angels recognize your allegiance and bow respectfully.");
            player.heal(player.getMaxHp() / 4);
            player.restoreMana(player.getMaxMana() / 4);
        }
    }
    
    private void handleHellGate(Player player) {
        System.out.println("\nThe screams of the damned echo from beyond the Hell Gate.");
        System.out.println("Sulfur burns your nostrils and heat sears your skin.");
        
        if (player.getFaction() != null && player.getFaction().getName().contains("Shadow")) {
            System.out.println("Dark forces recognize you as an ally.");
            player.addStatusEffect("demonic_power", new StatusEffect("Demonic Power", 10, 25, true) {
                @Override
                public void apply(Object target) {
                    // Passive attack bonus
                }
            });
        } else {
            player.takeDamage(20, "fire"); // Environmental damage
        }
    }
    
    private void encounterDomainBoss(Player player, Domain domain) {
        System.out.printf("\n*** %s APPEARS! ***\n", domain.getDomainBoss().toUpperCase());
        System.out.println("The ruler of " + domain.getName() + " challenges you!");
        
        Enemy boss = EnemyFactory.createDomainBoss(domain.getName(), player.getLevel());
        CombatSystem combatSystem = new CombatSystem();
        
        if (combatSystem.initiateBattle(player, boss)) {
            System.out.printf("*** VICTORY! You have conquered %s! ***\n", domain.getName());
            player.addGold(boss.getGoldReward() * 2); // Bonus gold for domain boss
            
            // Unlock achievement
            AchievementSystem.unlockAchievement(player, "Domain_Conqueror_" + domain.getName().replace(" ", "_"));
        }
    }
    
    private void offerExplorationOpportunity(Player player) {
        String[] opportunities = {
            "You notice a hidden cave entrance behind some rocks.",
            "Strange lights emanate from a nearby grove.",
            "An ancient shrine sits undisturbed in the distance.",
            "You hear the sound of rushing water from underground.",
            "Mysterious symbols are carved into a nearby tree.",
            "A faint magical aura draws your attention to a specific area."
        };
        
        String opportunity = opportunities[ThreadLocalRandom.current().nextInt(opportunities.length)];
        System.out.println("\n" + opportunity);
        System.out.print("Investigate? (yes/no): ");
        
        if (scanner.nextLine().toLowerCase().startsWith("y")) {
            exploreCurrentArea(player);
        }
    }
    
    /**
     * Explore current area for enemies and treasures
     */
    public void exploreCurrentArea(Player player) {
        String domain = player.getCurrentDomain();
        System.out.println("Exploring " + domain + "...");
        
        double encounterChance = ThreadLocalRandom.current().nextDouble();
        
        if (encounterChance < 0.6) {
            // Combat encounter
            Enemy enemy = EnemyFactory.createRandomEnemy(player.getLevel(), domain);
            System.out.printf("A wild %s appears!\n", enemy.getName());
            
            CombatSystem combatSystem = new CombatSystem();
            if (combatSystem.initiateBattle(player, enemy)) {
                player.incrementEnemiesDefeated();
                player.gainSkillExperience("Combat", 5);
            }
        } else if (encounterChance < 0.8) {
            // Treasure encounter
            handleTreasureFind(player);
        } else {
            // Safe exploration
            System.out.println("You explore the area but find nothing of interest.");
            player.gainSkillExperience("Exploration", 3);
        }
    }
    
    private void handleTreasureFind(Player player) {
        System.out.println("You discover a hidden treasure!");
        
        double treasureType = ThreadLocalRandom.current().nextDouble();
        
        if (treasureType < 0.4) {
            // Gold
            int goldFound = 50 + (player.getLevel() * 10) + ThreadLocalRandom.current().nextInt(1, 100);
            player.addGold(goldFound);
            System.out.println("Found " + goldFound + " gold!");
        } else if (treasureType < 0.7) {
            // Items
            Item treasure = ItemFactory.createRandomTreasure(player.getLevel());
            player.addToInventory(treasure);
            System.out.println("Found: " + treasure.getName());
        } else {
            // Special materials
            String domain = player.getCurrentDomain();
            String elementType = getElementForDomain(domain);
            Material material = new Material(elementType + " Crystal", "Magical", player.getLevel() / 5 + 1);
            player.addToInventory(material);
            System.out.println("Found rare material: " + material.getName());
        }
        
        player.gainSkillExperience("Exploration", 8);
    }
    
    private String getElementForDomain(String domain) {
        return switch (domain) {
            case "Fire Scene.Domain", "Volcano Scene.Domain" -> "Fire";
            case "Ice Scene.Domain" -> "Ice";
            case "Shadow Scene.Domain" -> "Shadow";
            case "Light Scene.Domain" -> "Light";
            case "Forest Scene.Domain" -> "Nature";
            case "Mountain Scene.Domain" -> "Earth";
            case "Ocean Scene.Domain" -> "Water";
            case "Sky Scene.Domain" -> "Air";
            default -> "Neutral";
        };
    }
    
    /**
     * Show world map
     */
    public void showMap(Player player) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                           WORLD MAP");
        System.out.println("=".repeat(80));
        
        System.out.println("\nREGULAR REALM:");
        showRealmMap("Regular", player);
        
        System.out.println("\nMYTHIC REALM:");
        if (player.isMythicMode()) {
            showRealmMap("Mythic", player);
        } else {
            System.out.println("  [LOCKED - Requires Prestige 10+]");
        }
        
        System.out.println("\nCurrent Location: " + player.getLocation() + " (" + player.getCurrentDomain() + ")");
    }
    
    private void showRealmMap(String realm, Player player) {
        domains.values().stream()
            .filter(domain -> domain.getRealm().equals(realm))
            .forEach(domain -> {
                String marker = domain.getName().equals(player.getCurrentDomain()) ? " <- YOU ARE HERE" : "";
                System.out.printf("  %s: %s%s\n", domain.getName(), domain.getDescription(), marker);
            });
    }
    
    /**
     * Get NPCs in current location
     */
    public List<NPC> getNPCsInLocation(String location) {
        // This would contain the full NPC system
        return NPCSystem.getNPCsForLocation(location);
    }
    
    /**
     * Get shops in current location
     */
    public List<Shop> getShopsInLocation(String location) {
        String domain = getDomainForLocation(location);
        Domain domainObj = domains.get(domain);
        
        if (domainObj == null) return new ArrayList<>();
        
        return domainObj.getSpecialShops().stream()
            .map(shopName -> new Shop(shopName, location))
            .toList();
    }
    
    // Getters
    public Map<String, Domain> getDomains() { return domains; }
    public Domain getDomain(String name) { return domains.get(name); }
}
