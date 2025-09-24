// ================================================================================
// MAIN GAME CLASS - Should be in Main.java
// Contains the main game loop and initialization
// ================================================================================
import java.util.*;
public class BattleMasterXX {
    private static Scanner scanner = new Scanner(System.in);
    private static Player player;
    private static GameWorld gameWorld;
    private static TimeWeatherSystem timeWeather;
    private static CombatSystem combatSystem;
    
    public static void main(String[] args) {
        System.out.println("=== WELCOME TO BATTLEMASTERXX JAVA EDITION ===");
        System.out.println("An epic RPG adventure with 28 domains, advanced systems, and endless content!");
        
        initializeGame();
        gameLoop();
    }
    
    /**
     * Initialize all game systems and create the player
     */
    private static void initializeGame() {
        System.out.print("Enter your character name: ");
        String playerName = scanner.nextLine();
        
        player = new Player(playerName);
        gameWorld = new GameWorld();
        timeWeather = new TimeWeatherSystem();
        combatSystem = new CombatSystem();
        
        // Initialize all systems
        ItemFactory.initialize();
        SpellSystem.initialize();
        FactionSystem.initialize();
        AchievementSystem.initialize();
        PrestigeSystem.initialize();
        
        System.out.println("\n" + playerName + ", your adventure begins in the Central Domain!");
        System.out.println("Type 'help' at any time to see available commands.");
        
        // Give starting items
        player.addToInventory(ItemFactory.createHealthPotion("Minor Health Potion", 30));
        player.addToInventory(ItemFactory.createManaPotion("Minor Mana Potion", 20));
        player.addToInventory(ItemFactory.createWeapon("Rusty Sword", 15, "physical"));
    }
    
    /**
     * Main game loop - handles all player input and game flow
     */
    private static void gameLoop() {
        while (player.isAlive()) {
            displayGameStatus();
            displayMainMenu();
            
            String choice = scanner.nextLine().toLowerCase().trim();
            processPlayerChoice(choice);
            
            // Random events and time progression
            timeWeather.advanceTime(1);
            processRandomEvents();
            
            // Auto-save every 10 actions
            if (timeWeather.getTotalHours() % 10 == 0) {
                SaveSystem.autoSave(player, gameWorld, timeWeather);
            }
        }
        
        System.out.println("\n=== GAME OVER ===");
        displayFinalStats();
        System.out.println("Thanks for playing BattleMasterXX Java Edition!");
    }
    
    /**
     * Display current game status information
     */
    private static void displayGameStatus() {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("║ %s - %s (%s)%n", 
            player.getName(), player.getLocation(), player.getCurrentDomain());
        System.out.printf("║ HP: %d/%d | Mana: %d/%d | Stamina: %d/%d | Gold: %d%n", 
            player.getHp(), player.getMaxHp(), player.getMana(), player.getMaxMana(),
            player.getStamina(), player.getMaxStamina(), player.getGold());
        System.out.printf("║ Level: %d | Prestige: %d | XP: %d/%d%n", 
            player.getLevel(), player.getPrestige(), player.getExp(), player.getLevel() * 100);
        System.out.printf("║ Time: %s | Weather: %s%n", 
            timeWeather.getCurrentTimeString(), timeWeather.getCurrentWeather());
        
        if (player.getHorse() != null) {
            System.out.printf("║ Mount: %s (Speed: %.1fx)%n", 
                player.getHorse().getName(), player.getHorse().getSpeed());
        }
        
        if (player.getFaction() != null) {
            System.out.printf("║ Faction: %s (Rep: %d)%n", 
                player.getFaction().getName(), player.getReputation());
        }
        
        // Show active status effects
        if (!player.getStatusEffects().isEmpty()) {
            System.out.printf("║ Status: %s%n", 
                String.join(", ", player.getStatusEffects().keySet()));
        }
        
        System.out.println("=".repeat(80));
    }
    
    /**
     * Display the main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n┌─── MAIN MENU ───────────────────────────────────────────────┐");
        System.out.println("│ 1.Move/Travel    2.Explore Area     3.Inventory    4.Equipment │");
        System.out.println("│ 5.Use Item       6.Magic/Spells     7.Companions   8.Quests     │");
        System.out.println("│ 9.Talk to NPCs   10.Shop           11.Faction     12.Prestige   │");
        System.out.println("│ 13.Skills        14.Achievements   15.Crafting    16.Map        │");
        System.out.println("│ 17.Statistics    18.Settings       19.Save/Load   20.Help       │");
        System.out.println("│ 21.Quit                                                         │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");
    }
    
    /**
     * Process player menu choice with enhanced error handling
     */
    private static void processPlayerChoice(String choice) {
        try {
            switch (choice) {
                case "1", "move", "travel" -> handleMovement();
                case "2", "explore" -> handleExploration();
                case "3", "inventory", "inv" -> player.showInventory();
                case "4", "equipment", "equip" -> handleEquipment();
                case "5", "use", "item" -> player.useItem();
                case "6", "magic", "spells" -> handleMagic();
                case "7", "companions", "comp" -> handleCompanions();
                case "8", "quests" -> player.showQuests();
                case "9", "talk", "npc" -> handleNPCInteraction();
                case "10", "shop" -> handleShopping();
                case "11", "faction" -> handleFaction();
                case "12", "prestige" -> handlePrestige();
                case "13", "skills" -> handleSkills();
                case "14", "achievements" -> handleAchievements();
                case "15", "crafting", "craft" -> handleCrafting();
                case "16", "map" -> gameWorld.showMap(player);
                case "17", "stats" -> player.showDetailedStats();
                case "18", "settings" -> handleSettings();
                case "19", "save", "load" -> handleSaveLoad();
                case "20", "help" -> showHelpMenu();
                case "21", "quit", "exit" -> handleQuit();
                default -> System.out.println("Invalid choice. Type 'help' for available commands.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please try again or type 'help' for assistance.");
        }
    }
    
    /**
     * Handle random events with enhanced variety
     */
    private static void processRandomEvents() {
        double eventChance = ThreadLocalRandom.current().nextDouble();
        
        // Weather events (10% chance)
        if (eventChance < 0.1) {
            timeWeather.triggerRandomWeatherEvent(player);
        }
        // Random encounters in dangerous areas (5% chance)
        else if (eventChance < 0.15 && isInDangerousArea()) {
            System.out.println("\n*** RANDOM ENCOUNTER ***");
            handleRandomEncounter();
        }
        // Faction events (3% chance)
        else if (eventChance < 0.18 && player.getFaction() != null) {
            player.getFaction().triggerRandomEvent(player);
        }
        // Treasure events (2% chance)
        else if (eventChance < 0.20) {
            handleTreasureEvent();
        }
        // Merchant events (1% chance)
        else if (eventChance < 0.21) {
            handleWanderingMerchant();
        }
    }
    
    // Handler methods for all menu options
    private static void handleMovement() {
        gameWorld.showAvailableLocations(player);
        System.out.print("Where would you like to go? (number or 'back'): ");
        String input = scanner.nextLine();
        
        if (!input.equals("back")) {
            try {
                int choice = Integer.parseInt(input);
                gameWorld.movePlayer(player, choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number.");
            }
        }
    }
    
    private static void handleExploration() {
        if (player.getStamina() < 10) {
            System.out.println("You're too tired to explore! Rest or use a stamina potion.");
            return;
        }
        
        System.out.println("Exploring " + player.getCurrentDomain() + "...");
        player.consumeStamina(10);
        gameWorld.exploreCurrentArea(player);
        
        // Skill experience for exploration
        player.gainSkillExperience("Exploration", 5);
    }
    
    private static void handleEquipment() {
        EquipmentSystem.showEquipmentMenu(player);
    }
    
    private static void handleMagic() {
        SpellSystem.showSpellMenu(player);
    }
    
    private static void handleCompanions() {
        CompanionSystem.showCompanionMenu(player);
    }
    
    private static void handleNPCInteraction() {
        NPCSystem.interactWithNPCs(player, gameWorld.getNPCsInLocation(player.getLocation()));
    }
    
    private static void handleShopping() {
        ShopSystem.showShopsInArea(player, gameWorld.getShopsInLocation(player.getLocation()));
    }
    
    private static void handleFaction() {
        FactionSystem.showFactionMenu(player);
    }
    
    private static void handlePrestige() {
        PrestigeSystem.showPrestigeMenu(player);
    }
    
    private static void handleSkills() {
        SkillSystem.showSkillMenu(player);
    }
    
    private static void handleAchievements() {
        AchievementSystem.showAchievements(player);
    }
    
    private static void handleCrafting() {
        CraftingSystem.showCraftingMenu(player);
    }
    
    private static void handleSettings() {
        SettingsSystem.showSettingsMenu();
    }
    
    private static void handleSaveLoad() {
        SaveLoadSystem.showSaveLoadMenu(player, gameWorld, timeWeather);
    }
    
    private static void showHelpMenu() {
        System.out.println("\n=== COMPREHENSIVE HELP MENU ===");
        System.out.println("MOVEMENT & EXPLORATION:");
        System.out.println("• move/travel - Travel between locations and domains");
        System.out.println("• explore - Search current area for enemies, treasures, and events");
        System.out.println("• map - View the world map and your location");
        System.out.println();
        System.out.println("INVENTORY & EQUIPMENT:");
        System.out.println("• inventory/inv - View and manage your items");
        System.out.println("• equipment/equip - Manage weapons, armor, and accessories");
        System.out.println("• use/item - Consume potions and other usable items");
        System.out.println("• crafting/craft - Create new items from materials");
        System.out.println();
        System.out.println("COMBAT & MAGIC:");
        System.out.println("• magic/spells - Cast spells and manage your magical abilities");
        System.out.println("• Combat is automatic when exploring or encountering enemies");
        System.out.println();
        System.out.println("SOCIAL & PROGRESSION:");
        System.out.println("• companions - Recruit and manage party members");
        System.out.println("• talk/npc - Interact with NPCs for quests and information");
        System.out.println("• quests - View active quests and their progress");
        System.out.println("• faction - Join factions and gain special benefits");
        System.out.println("• prestige - Advanced character progression system");
        System.out.println("• skills - Develop various skills that enhance gameplay");
        System.out.println("• achievements - Track your accomplishments");
        System.out.println();
        System.out.println("GAME MANAGEMENT:");
        System.out.println("• shop - Buy and sell items with merchants");
        System.out.println("• stats - View detailed character statistics");
        System.out.println("• save/load - Save your progress or load a previous game");
        System.out.println("• settings - Adjust game preferences");
        System.out.println("• quit - Exit the game");
    }
    
    private static void handleQuit() {
        System.out.println("Would you like to save before quitting? (y/n)");
        if (scanner.nextLine().toLowerCase().startsWith("y")) {
            SaveSystem.quickSave(player, gameWorld, timeWeather);
        }
        System.out.println("Thanks for playing BattleMasterXX! Your adventure awaits your return.");
        System.exit(0);
    }
    
    private static boolean isInDangerousArea() {
        return !player.getCurrentDomain().equals("Central Domain") && 
               !player.getLocation().contains("Village") && 
               !player.getLocation().contains("Town") &&
               !player.getLocation().contains("Safe");
    }
    
    private static void handleRandomEncounter() {
        Enemy randomEnemy = EnemyFactory.createRandomEnemy(player.getLevel(), player.getCurrentDomain());
        combatSystem.initiateBattle(player, randomEnemy);
    }
    
    private static void handleTreasureEvent() {
        System.out.println("\n*** You discover a hidden treasure! ***");
        List<Item> treasures = TreasureSystem.generateRandomTreasure(player.getLevel());
        for (Item treasure : treasures) {
            player.addToInventory(treasure);
            System.out.println("Found: " + treasure.getName());
        }
        player.gainSkillExperience("Exploration", 10);
    }
    
    private static void handleWanderingMerchant() {
        System.out.println("\n*** A wandering merchant approaches! ***");
        WanderingMerchant merchant = new WanderingMerchant();
        merchant.interact(player);
    }
    
    private static void displayFinalStats() {
        System.out.println("\n=== FINAL STATISTICS ===");
        System.out.printf("Final Level: %d\n", player.getLevel());
        System.out.printf("Prestige Level: %d\n", player.getPrestige());
        System.out.printf("Total Gold Earned: %d\n", player.getTotalGoldEarned());
        System.out.printf("Enemies Defeated: %d\n", player.getEnemiesDefeated());
        System.out.printf("Quests Completed: %d\n", player.getQuestsCompleted());
        System.out.printf("Achievements Unlocked: %d\n", player.getUnlockedAchievements().size());
        System.out.printf("Time Played: %s\n", timeWeather.getTotalTimeString());
    }
}