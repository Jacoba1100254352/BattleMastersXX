// Game.java

import java.util.*;

public class Game {
    private Player player;
    private GameTime gameTime;
    private Scanner scanner;
    private Battle battleSystem;
    private Random random;
    private Map<String, List<NPC>> npcs;
    
    public Game() {
        this.scanner = new Scanner(System.in);
        this.battleSystem = new Battle(scanner);
        this.gameTime = new GameTime();
        this.random = new Random();
        this.npcs = createNPCs();
    }
    
    private Map<String, List<NPC>> createNPCs() {
        Map<String, List<NPC>> npcMap = new HashMap<>();
        
        // Town Square NPCs
        List<NPC> townSquareNpcs = new ArrayList<>();
        
        // Old Man Rowan
        Map<String, Object> rowanQuest = new HashMap<>();
        rowanQuest.put("type", "find_item");
        rowanQuest.put("target", "Silver Amulet");
        rowanQuest.put("quantity", 1);
        rowanQuest.put("description", "Find the Silver Amulet that belonged to my brother");
        Map<String, Object> rowanReward = new HashMap<>();
        rowanReward.put("gold", 200);
        rowanReward.put("exp", 300);
        rowanQuest.put("reward", rowanReward);
        
        Map<String, Object> rowanRiddle = new HashMap<>();
        rowanRiddle.put("question", "I have cities, but no houses. I have mountains, but no trees. I have water, but no fish. What am I?");
        rowanRiddle.put("answers", Arrays.asList("map", "a map"));
        Map<String, Object> rowanRiddleReward = new HashMap<>();
        rowanRiddleReward.put("gold", 100);
        rowanRiddleReward.put("exp", 150);
        rowanRiddle.put("reward", rowanRiddleReward);
        
        NPC rowan = new NPC("Old Man Rowan", Arrays.asList(
            "Ah, another adventurer... just like the ones before.",
            "Do you hear the whispers in the wind? That's the forest remembering what we've forgotten.",
            "My brother went into the Abandoned Cave and never returned. I still wait...",
            "If you ever find a silver amulet engraved with a hawk, bring it to me. It was his.",
            "The world doesn't need more swords -- it needs more listeners."
        ), null, rowanQuest, rowanRiddle);
        
        townSquareNpcs.add(rowan);
        npcMap.put("Town Square", townSquareNpcs);
        
        return npcMap;
    }
    
    public void start() {
        System.out.println("Welcome to BattleMasterXX!");
        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine();
        player = new Player(playerName);
        
        gameLoop();
    }
    
    private void gameLoop() {
        while (player.isAlive()) {
            displayGameState();
            displayMenu();
            
            String choice = scanner.nextLine();
            handleChoice(choice);
        }
    }
    
    private void displayGameState() {
        String horseInfo = (player.getHorse() != null) ? 
            " | Horse: " + player.getHorse().getName() : " | On foot";
        
        System.out.println("\n-- " + player.getLocation() + " (" + player.getCurrentDomain() + ") --");
        System.out.println("HP: " + (int)player.getHp() + "/" + (int)player.getMaxHp() + 
                          ", Mana: " + (int)player.getMana() + "/" + (int)player.getMaxMana());
        System.out.println("Gold: " + player.getGold() + ", Level: " + player.getLevel() + 
                          ", Prestige: " + player.getPrestige() + horseInfo);
        System.out.println("Time: Day " + gameTime.getDay() + " - " + gameTime.getHour() + 
                          ":00 (" + gameTime.getTimeOfDay() + ")");
        System.out.println("Weather: " + gameTime.getCurrentWeather().getType());
    }
    
    private void displayMenu() {
        System.out.println("1. Move\n2. Explore\n3. Show Inventory\n4. Equip\n5. Use Item");
        System.out.println("6. Recruit Companion\n7. Show Companions\n8. Quit\n9. Remove Companion");
        System.out.println("10. Talk to " + player.getLocation() + "'s folk\n11. Show Quests");
        System.out.print("Your choice: ");
    }
    
    private void handleChoice(String choice) {
        switch (choice) {
            case "1":
                move();
                break;
            case "2":
                explore();
                break;
            case "3":
                showInventory();
                break;
            case "4":
                equip();
                break;
            case "5":
                useItem();
                break;
            case "6":
                recruitCompanion();
                break;
            case "7":
                showCompanions();
                break;
            case "8":
                System.out.println("Thanks for playing BattleMasterXX!");
                System.exit(0);
                break;
            case "9":
                removeCompanion();
                break;
            case "10":
                talkToNPCs();
                break;
            case "11":
                showQuests();
                break;
            default:
                System.out.println("Invalid input.");
        }
    }
    
    private void move() {
        System.out.println("You are in " + player.getLocation() + " (" + player.getCurrentDomain() + ").");
        List<String> availableLocations = GameData.LOCATIONS.get(player.getLocation());
        
        if (availableLocations == null || availableLocations.isEmpty()) {
            System.out.println("No locations available to travel to.");
            return;
        }
        
        for (int i = 0; i < availableLocations.size(); i++) {
            String loc = availableLocations.get(i);
            double horseSpeed = (player.getHorse() != null) ? player.getHorse().getSpeed() : 1.0;
            int travelTime = getTravelTime(player.getLocation(), loc, horseSpeed);
            String destDomain = getCurrentDomain(loc);
            System.out.println((i + 1) + ". " + loc + " (" + destDomain + ") - Travel time: " + travelTime + " hr");
        }
        
        try {
            System.out.print("Where do you want to go?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < availableLocations.size()) {
                String destination = availableLocations.get(idx);
                travelWithTime(destination);
                handleLocationEvents();
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void travelWithTime(String destination) {
        double horseSpeed = (player.getHorse() != null) ? player.getHorse().getSpeed() : 1.0;
        int travelTime = getTravelTime(player.getLocation(), destination, horseSpeed);
        
        System.out.println("Current time: Day " + gameTime.getDay() + " - " + gameTime.getHour() + 
                          ":00 (" + gameTime.getTimeOfDay() + ")");
        System.out.println("Weather: " + gameTime.getCurrentWeather().getType());
        
        if (player.getHorse() != null) {
            System.out.println("Traveling to " + destination + " on your " + player.getHorse().getName() + "...");
        } else {
            System.out.println("Traveling to " + destination + " on foot...");
        }
        
        System.out.println("Travel time: " + travelTime + " hour(s)");
        
        for (int i = 0; i < travelTime; i++) {
            gameTime.advanceTime(1);
            System.out.println("Traveling... " + (i + 1) + "/" + travelTime + " hr (Time: " + 
                             gameTime.getHour() + ":00)");
            try {
                Thread.sleep(500); // Brief pause for effect
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        String oldDomain = player.getCurrentDomain();
        player.setLocation(destination);
        player.setCurrentDomain(getCurrentDomain(destination));
        
        System.out.println("You arrived at " + destination + "! It's " + 
                          gameTime.getTimeOfDay().toLowerCase() + " and the weather is " + 
                          gameTime.getCurrentWeather().getType().toLowerCase() + ".");
        
        if (!oldDomain.equals(player.getCurrentDomain())) {
            System.out.println("*** You have entered the " + player.getCurrentDomain() + "! ***");
            @SuppressWarnings("unchecked")
            Map<String, Object> domainInfo = (Map<String, Object>) GameData.DOMAINS.get(player.getCurrentDomain());
            if (domainInfo != null) {
                System.out.println("Theme: " + domainInfo.get("theme"));
            }
        }
    }
    
    private int getTravelTime(String fromLoc, String toLoc, double horseSpeed) {
        String key1 = fromLoc + "-" + toLoc;
        String key2 = toLoc + "-" + fromLoc;
        Integer distance = GameData.LOCATION_DISTANCES.get(key1);
        if (distance == null) {
            distance = GameData.LOCATION_DISTANCES.get(key2);
        }
        if (distance == null) {
            distance = 5; // Default distance
        }
        
        return Math.max(1, (int)(distance / horseSpeed));
    }
    
    private String getCurrentDomain(String location) {
        for (Map.Entry<String, Map<String, Object>> entry : GameData.DOMAINS.entrySet()) {
            @SuppressWarnings("unchecked")
            List<String> locations = (List<String>) entry.getValue().get("locations");
            if (locations != null && locations.contains(location)) {
                return entry.getKey();
            }
        }
        return "Central Domain"; // Default
    }
    
    private void handleLocationEvents() {
        player.checkNpcQuestItems();
        
        String location = player.getLocation();
        
        // Handle special locations
        switch (location) {
            case "Quest Hall":
                handleQuestHall();
                break;
            case "Hospital":
                handleHospital();
                break;
            case "Stable":
                handleStable();
                break;
            case "Prestige Hall":
                handlePrestigeHall();
                break;
            case "Blacksmith":
                handleBlacksmith();
                break;
            case "Magic Shop":
                handleMagicShop();
                break;
            case "Armoury":
                handleArmoury();
                break;
            case "Mythic Gate":
                handleMythicGate();
                break;
        }
    }
    
    private void handleQuestHall() {
        System.out.println("\nWelcome to the Quest Hall!");
        if (player.getCurrentQuest() != null) {
            System.out.println("Current quest: Defeat a " + player.getCurrentQuest().getTarget());
        } else {
            System.out.print("Would you like a quest? (yes/no): ");
            String start = scanner.nextLine().toLowerCase();
            if ("yes".equals(start)) {
                EnemyData.EnemyTemplate template = EnemyData.getWeightedRandomEnemy();
                if (!template.isBoss()) {
                    Enemy questEnemy = template.createEnemy();
                    player.startQuest(questEnemy);
                }
            }
        }
    }
    
    private void handleHospital() {
        if (player.getHp() < player.getMaxHp() / 2) {
            System.out.print("Oh dear! Would you like to see the doctor? (yes/no): ");
            String doctorDecision = scanner.nextLine().toLowerCase();
            if ("yes".equals(doctorDecision)) {
                String injury = GameData.SURGICAL_INJURIES[random.nextInt(GameData.SURGICAL_INJURIES.length)];
                System.out.println("\nYou are undergoing surgery for " + injury + "...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                player.setHp(player.getHp() + player.getMaxHp() / 2);
                System.out.println("You're as good as new!");
            } else {
                System.out.println("\nAlright tough guy!");
            }
        } else {
            System.out.println("\nYou have enough health to sustain yourself!");
        }
    }
    
    private void handleStable() {
        System.out.println("\nWelcome to the Stable!");
        showStable();
    }
    
    private void showStable() {
        System.out.println("\n=== STABLE === Gold: " + player.getGold());
        if (player.getHorse() != null) {
            System.out.println("Current horse: " + player.getHorse().getName() + 
                             " (Speed: " + player.getHorse().getSpeed() + "x)");
            System.out.print("Sell your current horse for half price? (yes/no): ");
            String sellOption = scanner.nextLine().toLowerCase();
            if ("yes".equals(sellOption)) {
                int sellPrice = player.getHorse().getPrice() / 2;
                player.setGold(player.getGold() + sellPrice);
                System.out.println("You sold your " + player.getHorse().getName() + 
                                 " for " + sellPrice + " gold!");
                player.setHorse(null);
            }
        }
        
        System.out.println("\nHorses for sale:");
        List<Horse> horses = GameData.HORSES;
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            String speedDesc = horse.getSpeed() + "x speed";
            if (horse.getSpeed() < 1.0) {
                speedDesc += " (slower)";
            } else if (horse.getSpeed() > 1.0) {
                speedDesc += " (faster)";
            }
            System.out.println((i + 1) + ". " + horse.getName() + " - " + horse.getPrice() + 
                             " gold (" + speedDesc + ")");
        }
        
        try {
            System.out.print("\nChoose a horse to buy (0 to exit): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            if (choice >= 1 && choice <= horses.size()) {
                Horse horse = horses.get(choice - 1);
                if (player.getGold() >= horse.getPrice()) {
                    if (player.getHorse() != null) {
                        System.out.println("You already have a horse! Sell it first.");
                        return;
                    }
                    player.setGold(player.getGold() - horse.getPrice());
                    player.setHorse(new Horse(horse));
                    System.out.println("You bought " + horse.getName() + 
                                     "! Travel time reduced by " + horse.getSpeed() + "x!");
                } else {
                    System.out.println("Not enough gold!");
                }
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        }
    }
    
    private void handlePrestigeHall() {
        System.out.println("\nWelcome to the Prestige Hall!");
        if (player.canPrestige()) {
            System.out.println("*** PRESTIGE AVAILABLE! ***");
            System.out.print("Would you like to prestige? (yes/no): ");
            String prestigeChoice = scanner.nextLine().toLowerCase();
            if ("yes".equals(prestigeChoice)) {
                player.doPrestige();
            }
        } else {
            System.out.println("You need to reach level 50 to prestige. Current level: " + player.getLevel());
        }
        
        if (player.getPrestige() > 0) {
            System.out.print("Visit prestige shop? (yes/no): ");
            String shopChoice = scanner.nextLine().toLowerCase();
            if ("yes".equals(shopChoice)) {
                showPrestigeShop();
            }
        }
    }
    
    private void showPrestigeShop() {
        if (player.getPrestige() == 0) {
            System.out.println("You need at least 1 prestige to access the prestige shop!");
            return;
        }
        
        System.out.println("\n=== PRESTIGE SHOP === (Your Prestiges: " + player.getPrestige() + ")");
        System.out.println("Prestige Weapons:");
        
        // Create some prestige weapons
        List<Item> prestigeWeapons = Arrays.asList(
            new Item("Prestige Blade Mk1", 10000),
            new Item("Prestige Blade Mk2", 12500),
            new Item("Prestige Blade Mk3", 15000),
            new Item("Ultimate Prestige Sword", 30000),
            new Item("Godly Prestige Weapon", 100000)
        );
        
        int[] costs = {1, 2, 3, 10, 50};
        
        for (int i = 0; i < prestigeWeapons.size(); i++) {
            Item weapon = prestigeWeapons.get(i);
            int cost = costs[i];
            String affordable = (player.getPrestige() >= cost) ? "✓" : "✗";
            System.out.println((i + 1) + ". " + weapon.getName() + " (Power: " + weapon.getPower() + 
                             ") - Cost: " + cost + " prestiges " + affordable);
        }
        
        try {
            System.out.print("\nChoose a weapon to buy (0 to exit): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            if (choice >= 1 && choice <= prestigeWeapons.size()) {
                Item weapon = prestigeWeapons.get(choice - 1);
                int cost = costs[choice - 1];
                if (player.getPrestige() >= cost) {
                    player.setPrestige(player.getPrestige() - cost);
                    player.getInventory().add(new Item(weapon));
                    System.out.println("You bought " + weapon.getName() + 
                                     "! Prestiges remaining: " + player.getPrestige());
                } else {
                    System.out.println("Not enough prestiges!");
                }
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        }
    }
    
    private void handleBlacksmith() {
        System.out.println("\nWelcome to the Blacksmith! Gold: " + player.getGold());
        System.out.println("1. Browse Basic Weapons\n2. Browse Advanced Weapons\n3. Sell Regular Weapons");
        System.out.print("Choose option: ");
        String weaponChoice = scanner.nextLine();
        
        switch (weaponChoice) {
            case "1":
                browseWeapons(GameData.WEAPONS, "Basic Weapons");
                break;
            case "2":
                browseWeapons(GameData.ADVANCED_WEAPONS, "Advanced Weapons");
                break;
            case "3":
                sellWeapons(false);
                break;
        }
    }
    
    private void handleMagicShop() {
        System.out.println("\nWelcome to the Magic Shop! Gold: " + player.getGold());
        System.out.println("1. Browse Magic Weapons\n2. Sell Magic Weapons");
        System.out.print("Choose option: ");
        String magicChoice = scanner.nextLine();
        
        switch (magicChoice) {
            case "1":
                browseWeapons(GameData.MAGIC_WEAPONS, "Magic Weapons");
                break;
            case "2":
                sellWeapons(true);
                break;
        }
    }
    
    private void handleArmoury() {
        System.out.println("\nWelcome to the Armoury! Gold: " + player.getGold());
        System.out.print("Browse armor? (yes/no): ");
        String buy = scanner.nextLine().toLowerCase();
        if ("yes".equals(buy)) {
            browseArmor();
        }
    }
    
    private void handleMythicGate() {
        if (!player.isMythicMode()) {
            System.out.println("A shimmering barrier sends you back to Town Square. You feel you're not yet ready for what lies beyond.");
            player.setLocation("Town Square");
            return;
        }
        System.out.println("You pass through the Mythic Gate. A surge of energy surrounds you...");
        travelWithTime("Eternal Citadel");
    }
    
    private void browseWeapons(List<Item> weaponList, String pageName) {
        System.out.println("\n" + pageName + " - Gold: " + player.getGold());
        for (int i = 0; i < weaponList.size(); i++) {
            Item weapon = weaponList.get(i);
            int cost = weapon.getPower() * 9;
            System.out.println((i + 1) + ". " + weapon.getName() + " (Power: " + weapon.getPower() + 
                             ") - " + cost + " gold");
        }
        
        try {
            System.out.print("Choose weapon (0 to go back): ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx == -1) return;
            
            if (idx >= 0 && idx < weaponList.size()) {
                Item weapon = weaponList.get(idx);
                int cost = weapon.getPower() * 9;
                if (player.getGold() >= cost) {
                    player.setGold(player.getGold() - cost);
                    player.getInventory().add(new Item(weapon));
                    System.out.println("You bought " + weapon.getName());
                } else {
                    System.out.println("Not enough gold.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void browseArmor() {
        System.out.println("\nArmor - Gold: " + player.getGold());
        for (int i = 0; i < GameData.ARMOR.size(); i++) {
            Item armor = GameData.ARMOR.get(i);
            int cost = armor.getDefense() * 7;
            System.out.println((i + 1) + ". " + armor.getName() + " (Defense: " + armor.getDefense() + 
                             ") - " + cost + " gold");
        }
        
        try {
            System.out.print("Choose armor (0 to exit): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            if (choice >= 1 && choice <= GameData.ARMOR.size()) {
                Item armor = GameData.ARMOR.get(choice - 1);
                int cost = armor.getDefense() * 7;
                if (player.getGold() >= cost) {
                    player.setGold(player.getGold() - cost);
                    player.getInventory().add(new Item(armor));
                    System.out.println("You bought " + armor.getName() + "!");
                } else {
                    System.out.println("Not enough gold.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void sellWeapons(boolean magicOnly) {
        List<Item> sellableWeapons = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item.isWeapon()) {
                boolean isMagic = GameData.MAGIC_WEAPONS.stream()
                    .anyMatch(magic -> magic.getName().equals(item.getName()));
                if (magicOnly == isMagic) {
                    sellableWeapons.add(item);
                }
            }
        }
        
        if (sellableWeapons.isEmpty()) {
            String weaponType = magicOnly ? "magic" : "regular";
            System.out.println("You have no " + weaponType + " weapons to sell.");
            return;
        }
        
        String weaponType = magicOnly ? "MAGIC" : "REGULAR";
        System.out.println("\n=== SELL " + weaponType + " WEAPONS ===");
        System.out.println(weaponType.toLowerCase() + " weapons you can sell:");
        
        for (int i = 0; i < sellableWeapons.size(); i++) {
            Item weapon = sellableWeapons.get(i);
            int sellPrice = (weapon.getPower() * (magicOnly ? 5 : 9)) / 2;
            System.out.println((i + 1) + ". " + weapon.getName() + " (Power: " + weapon.getPower() + 
                             ") - Sell for " + sellPrice + " gold");
        }
        
        try {
            System.out.print("\nChoose weapon to sell (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            if (choice >= 1 && choice <= sellableWeapons.size()) {
                Item weapon = sellableWeapons.get(choice - 1);
                int sellPrice = (weapon.getPower() * (magicOnly ? 5 : 9)) / 2;
                
                // Check if it's currently equipped
                if (player.getWeapon() != null && player.getWeapon().getName().equals(weapon.getName())) {
                    System.out.println("You cannot sell your currently equipped weapon! Unequip it first.");
                    return;
                }
                
                player.setGold(player.getGold() + sellPrice);
                player.getInventory().remove(weapon);
                System.out.println("You sold " + weapon.getName() + " for " + sellPrice + " gold!");
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void explore() {
        String currentDomain = getCurrentDomain(player.getLocation());
        System.out.println("Exploring " + currentDomain + " during " + 
                          gameTime.getTimeOfDay().toLowerCase() + " in " + 
                          gameTime.getCurrentWeather().getType().toLowerCase() + " weather...");
        
        // Weather effects on exploration
        String weatherType = gameTime.getCurrentWeather().getType();
        if ("Fog".equals(weatherType) && random.nextDouble() < 0.4) {
            System.out.println("It's too foggy to find anything.");
            return;
        } else if ("Sandstorm".equals(weatherType) && random.nextDouble() < 0.3) {
            System.out.println("The sandstorm forces you to take shelter.");
            return;
        } else if ("Toxic Rain".equals(weatherType) && random.nextDouble() < 0.2) {
            System.out.println("The toxic rain burns you!");
            player.setHp(Math.max(1, player.getHp() - 20));
            return;
        }
        
        EnemyData.EnemyTemplate template = EnemyData.getWeightedRandomEnemy();
        double levelMultiplier = 1 + (player.getLevel() - 1) * 0.1 + player.getPrestige() * 0.5;
        
        Enemy enemy = new Enemy(
            template.getName(),
            (int)(template.getHp() * levelMultiplier),
            (int)(template.getAttack() * levelMultiplier),
            (int)(template.getExp() * (1 + player.getPrestige() * 0.2)),
            (int)(template.getGold() * (1 + player.getPrestige() * 0.3)),
            template.isBoss(),
            template.getResist(),
            template.getWeak()
        );
        
        String result = battleSystem.battle(player, enemy);
        
        if ("victory".equals(result)) {
            player.updateNpcQuestProgress("defeat_enemy", enemy.getName());
        }
    }
    
    private void showInventory() {
        if (player.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }
        
        System.out.println("Inventory:");
        for (int i = 0; i < player.getInventory().size(); i++) {
            Item item = player.getInventory().get(i);
            System.out.println((i + 1) + ". " + item.getName());
        }
    }
    
    private void equip() {
        List<Item> equipable = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item.isWeapon() || item.isArmor()) {
                equipable.add(item);
            }
        }
        
        if (equipable.isEmpty()) {
            System.out.println("No equipable items.");
            return;
        }
        
        for (int i = 0; i < equipable.size(); i++) {
            System.out.println((i + 1) + ". " + equipable.get(i).getName());
        }
        
        try {
            System.out.print("Equip which item?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < equipable.size()) {
                Item item = equipable.get(idx);
                if (item.isWeapon()) {
                    player.setWeapon(item);
                    System.out.println("Equipped " + item.getName() + " as weapon.");
                } else if (item.isArmor()) {
                    player.setArmor(item);
                    System.out.println("Equipped " + item.getName() + " as armor.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void useItem() {
        List<Item> usable = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item.isUsable()) {
                usable.add(item);
            }
        }
        
        if (usable.isEmpty()) {
            System.out.println("No usable items in your inventory.");
            return;
        }
        
        System.out.println("Usable items:");
        for (int i = 0; i < usable.size(); i++) {
            System.out.println((i + 1) + ". " + usable.get(i).getName());
        }
        
        try {
            System.out.print("Use which item?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < usable.size()) {
                Item item = usable.get(idx);
                
                // Check for bleeding first
                if (player.getStatusEffects().containsKey("bleed") && "heal".equals(item.getEffect())) {
                    System.out.println("You are bleeding and cannot use healing items!");
                    return;
                }
                
                if ("heal".equals(item.getEffect())) {
                    int amount = item.getValue();
                    if (player.getStatusEffects().containsKey("poison")) {
                        amount = amount / 2;
                        System.out.println("Poison reduces your healing!");
                    }
                    player.setHp(player.getHp() + amount);
                    System.out.println("You healed " + amount + " HP.");
                } else if ("mana".equals(item.getEffect())) {
                    player.setMana(player.getMana() + item.getValue());
                    System.out.println("You restored " + item.getValue() + " mana.");
                } else if ("full_heal".equals(item.getEffect())) {
                    player.setHp(player.getMaxHp());
                    player.setMana(player.getMaxMana());
                    System.out.println("You fully restored your HP and mana!");
                }
                
                player.getInventory().remove(item);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void recruitCompanion() {
        if (player.getCompanions().size() >= player.getMaxCompanions()) {
            System.out.println("You can only have " + player.getMaxCompanions() + " companions maximum!");
            return;
        }
        
        System.out.print("Companion's name: ");
        String name = scanner.nextLine();
        System.out.print("Role (warrior/healer): ");
        String role = scanner.nextLine().toLowerCase();
        
        if ("warrior".equals(role) || "healer".equals(role)) {
            Companion companion = new Companion(name, role);
            if (player.addCompanion(companion)) {
                System.out.println(name + " the " + role + " has joined!");
            }
        } else {
            System.out.println("Invalid role.");
        }
    }
    
    private void showCompanions() {
        if (player.getCompanions().isEmpty()) {
            System.out.println("You have no companions.");
        } else {
            System.out.println("Companions (" + player.getCompanions().size() + "/" + 
                             player.getMaxCompanions() + "):");
            for (Companion companion : player.getCompanions()) {
                System.out.println("- " + companion.getName() + " (" + companion.getRole() + ")");
            }
        }
    }
    
    private void removeCompanion() {
        if (player.getCompanions().isEmpty()) {
            System.out.println("No companions.");
            return;
        }
        
        for (int i = 0; i < player.getCompanions().size(); i++) {
            Companion companion = player.getCompanions().get(i);
            System.out.println((i + 1) + ". " + companion.getName() + " (" + companion.getRole() + ")");
        }
        
        try {
            System.out.print("Remove who?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < player.getCompanions().size()) {
                Companion left = player.getCompanions().remove(idx);
                System.out.println(left.getName() + " has left the party.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void talkToNPCs() {
        List<NPC> locationNpcs = npcs.get(player.getLocation());
        if (locationNpcs == null || locationNpcs.isEmpty()) {
            System.out.println("No one to talk to here.");
            return;
        }
        
        for (int i = 0; i < locationNpcs.size(); i++) {
            System.out.println((i + 1) + ". Talk to " + locationNpcs.get(i).getName());
        }
        
        try {
            System.out.print("Talk to who?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < locationNpcs.size()) {
                locationNpcs.get(idx).talk(player, scanner);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
        }
    }
    
    private void showQuests() {
        System.out.println("\n=== ACTIVE QUESTS ===");
        if (player.getCurrentQuest() != null) {
            System.out.println("Main Quest: Defeat a " + player.getCurrentQuest().getTarget());
        }
        
        if (!player.getNpcQuests().isEmpty()) {
            System.out.println("NPC Quests:");
            for (Quest quest : player.getNpcQuests()) {
                System.out.println("- " + quest.getNpc() + ": " + quest.getDescription() + 
                                 " (" + quest.getProgress() + "/" + quest.getQuantity() + ")");
            }
        }
        
        if (player.getCurrentQuest() == null && player.getNpcQuests().isEmpty()) {
            System.out.println("No active quests.");
        }
    }
}