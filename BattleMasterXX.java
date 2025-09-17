} catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void talkToNPCs() {
        List<NPC> npcsHere = locationNPCs.getOrDefault(player.getLocation(), new ArrayList<>());
        
        if (npcsHere.isEmpty()) {
            System.out.println("There's no one to talk to here.");
            return;
        }
        
        System.out.println("\nPeople you can talk to:");
        for (int i = 0; i < npcsHere.size(); i++) {
            NPC npc = npcsHere.get(i);
            System.out.println((i + 1) + ". " + npc.getName() + " (" + npc.getPersonality() + ")");
        }
        
        System.out.print("Talk to whom? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < npcsHere.size()) {
                NPC npc = npcsHere.get(choice);
                npc.talk(player, scanner);
                player.gainSkillExp("Diplomacy", 10);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void showQuests() {
        System.out.println("\n=== ACTIVE QUESTS ===");
        
        if (player.getNpcQuests().isEmpty()) {
            System.out.println("No active quests.");
        } else {
            for (NPCQuest quest : player.getNpcQuests()) {
                System.out.println("- " + quest.getName());
                System.out.println("  From: " + quest.getNpcName());
                System.out.println("  Description: " + quest.getDescription());
                System.out.println("  Progress: " + quest.getProgress() + "/" + quest.getQuantity());
                System.out.println();
            }
        }
    }
    
    private void showDetailedStats() {
        System.out.println("\n=== DETAILED CHARACTER STATS ===");
        System.out.println("Name: " + player.getName());
        System.out.println("Level: " + player.getLevel() + " (EXP: " + player.getExp() + ")");
        System.out.println("Prestige: " + player.getPrestige());
        System.out.println("HP: " + (int)player.getHp() + "/" + (int)player.getMaxHp());
        System.out.println("Mana: " + (int)player.getMana() + "/" + (int)player.getMaxMana());
        System.out.println("Gold: " + player.getGold());
        System.out.println("Attack Power: " + player.attackValue());
        System.out.println("Reputation: " + player.getReputation());
        
        if (player.getFaction() != null) {
            System.out.println("Faction: " + player.getFaction());
        }
        
        if (player.getWeapon() != null) {
            System.out.println("Weapon: " + player.getWeapon().getName() + 
                             " (Power: " + player.getWeapon().getPower() + ")");
        }
        
        if (player.getArmor() != null) {
            System.out.println("Armor: " + player.getArmor().getName() + 
                             " (Defense: " + player.getArmor().getDefense() + ")");
        }
        
        if (player.getHorse() != null) {
            System.out.println("Mount: " + player.getHorse().getName() + 
                             " (Speed: " + player.getHorse().getSpeed() + "x)");
        }
        
        System.out.println("\n=== SKILLS ===");
        player.getSkills().forEach((skill, level) -> 
            System.out.println(skill + ": Level " + level));
        
        System.out.println("\n=== ACHIEVEMENTS ===");
        if (player.getAchievements().isEmpty()) {
            System.out.println("No achievements yet.");
        } else {
            player.getAchievements().forEach(System.out::println);
        }
        
        System.out.println("\n=== DISCOVERED LOCATIONS ===");
        System.out.println("Discovered " + player.getDiscoveredLocations().size() + " locations:");
        player.getDiscoveredLocations().forEach(location -> System.out.println("- " + location));
    }
    
    private void handleShops() {
        String location = player.getLocation();
        
        switch (location) {
            case "Blacksmith":
                handleBlacksmithShop();
                break;
            case "Magic Shop":
                handleMagicShop();
                break;
            case "Armoury":
                handleArmouryShop();
                break;
            default:
                // Check for domain-specific shops
                String domain = player.getCurrentDomain();
                Domain domainData = domains.get(domain);
                if (domainData != null && !domainData.getSpecialShops().isEmpty()) {
                    handleDomainShops(domainData);
                } else {
                    System.out.println("No shops available at this location.");
                }
        }
    }
    
    private void handleBlacksmithShop() {
        System.out.println("\n=== BLACKSMITH SHOP ===");
        System.out.println("Welcome to the forge! Gold: " + player.getGold());
        System.out.println("1. Buy Weapons");
        System.out.println("2. Sell Weapons");
        System.out.println("3. Upgrade Weapon");
        
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                buyWeapons();
                break;
            case "2":
                sellWeapons();
                break;
            case "3":
                upgradeWeapon();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void buyWeapons() {
        List<Weapon> weapons = GameData.createWeapons();
        
        System.out.println("\n=== WEAPONS FOR SALE ===");
        for (int i = 0; i < Math.min(10, weapons.size()); i++) {
            Weapon weapon = weapons.get(i);
            int price = weapon.getPower() * 5;
            System.out.println((i + 1) + ". " + weapon.getName() + 
                             " (Power: " + weapon.getPower() + ") - " + price + " gold");
        }
        
        System.out.print("Buy which weapon? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < Math.min(10, weapons.size())) {
                Weapon weapon = weapons.get(choice);
                int price = weapon.getPower() * 5;
                
                if (player.getGold() >= price) {
                    player.setGold(player.getGold() - price);
                    player.getInventory().add(weapon);
                    System.out.println("You bought " + weapon.getName() + "!");
                    player.gainSkillExp("Trading", 10);
                } else {
                    System.out.println("Not enough gold!");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void sellWeapons() {
        List<Weapon> weaponsToSell = new ArrayList<>();
        
        for (Item item : player.getInventory()) {
            if (item instanceof Weapon) {
                weaponsToSell.add((Weapon) item);
            }
        }
        
        if (weaponsToSell.isEmpty()) {
            System.out.println("You have no weapons to sell.");
            return;
        }
        
        System.out.println("\n=== SELL WEAPONS ===");
        for (int i = 0; i < weaponsToSell.size(); i++) {
            Weapon weapon = weaponsToSell.get(i);
            int sellPrice = weapon.getPower() * 3;
            System.out.println((i + 1) + ". " + weapon.getName() + 
                             " (Power: " + weapon.getPower() + ") - Sell for " + sellPrice + " gold");
        }
        
        System.out.print("Sell which weapon? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < weaponsToSell.size()) {
                Weapon weapon = weaponsToSell.get(choice);
                
                if (player.getWeapon() != null && player.getWeapon().getName().equals(weapon.getName())) {
                    System.out.println("You cannot sell your currently equipped weapon!");
                    return;
                }
                
                int sellPrice = weapon.getPower() * 3;
                player.setGold(player.getGold() + sellPrice);
                player.getInventory().remove(weapon);
                System.out.println("You sold " + weapon.getName() + " for " + sellPrice + " gold!");
                player.gainSkillExp("Trading", 15);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void upgradeWeapon() {
        if (player.getWeapon() == null) {
            System.out.println("You need to have a weapon equipped to upgrade it!");
            return;
        }
        
        Weapon weapon = player.getWeapon();
        int upgradeCost = weapon.getPower() * 10;
        
        System.out.println("\n=== WEAPON UPGRADE ===");
        System.out.println("Current weapon: " + weapon.getName() + " (Power: " + weapon.getPower() + ")");
        System.out.println("Upgrade cost: " + upgradeCost + " gold");
        System.out.println("After upgrade: Power " + (weapon.getPower() + 50));
        
        System.out.print("Upgrade weapon? (yes/no): ");
        String choice = scanner.nextLine().toLowerCase();
        
        if ("yes".equals(choice)) {
            if (player.getGold() >= upgradeCost) {
                player.setGold(player.getGold() - upgradeCost);
                
                // Create upgraded weapon
                Weapon upgradedWeapon = new Weapon(weapon.getName() + " +1", 
                    weapon.getPower() + 50, weapon.getElement(), weapon.getRarity());
                player.setWeapon(upgradedWeapon);
                
                // Replace in inventory
                for (int i = 0; i < player.getInventory().size(); i++) {
                    if (player.getInventory().get(i) instanceof Weapon && 
                        ((Weapon) player.getInventory().get(i)).getName().equals(weapon.getName())) {
                        player.getInventory().set(i, upgradedWeapon);
                        break;
                    }
                }
                
                System.out.println("Weapon upgraded successfully!");
                player.gainSkillExp("Crafting", 30);
            } else {
                System.out.println("Not enough gold for upgrade!");
            }
        }
    }
    
    private void handleMagicShop() {
        System.out.println("\n=== MAGIC SHOP ===");
        System.out.println("Mystical items and potions! Gold: " + player.getGold());
        System.out.println("1. Buy Consumables");
        System.out.println("2. Buy Spell Scrolls");
        System.out.println("3. Enchant Equipment");
        
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                buyConsumables();
                break;
            case "2":
                buySpellScrolls();
                break;
            case "3":
                enchantEquipment();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void buyConsumables() {
        List<Consumable> consumables = GameData.createConsumables();
        
        System.out.println("\n=== CONSUMABLES FOR SALE ===");
        for (int i = 0; i < Math.min(8, consumables.size()); i++) {
            Consumable consumable = consumables.get(i);
            int price = consumable.getValue() * 2;
            System.out.println((i + 1) + ". " + consumable.getName() + 
                             " (" + consumable.getEffect() + " +" + consumable.getValue() + ") - " + price + " gold");
        }
        
        System.out.print("Buy which item? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < Math.min(8, consumables.size())) {
                Consumable consumable = consumables.get(choice);
                int price = consumable.getValue() * 2;
                
                if (player.getGold() >= price) {
                    player.setGold(player.getGold() - price);
                    player.getInventory().add(consumable);
                    System.out.println("You bought " + consumable.getName() + "!");
                    player.gainSkillExp("Trading", 5);
                } else {
                    System.out.println("Not enough gold!");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void buySpellScrolls() {
        System.out.println("\n=== SPELL SCROLLS ===");
        System.out.println("1. Fireball Scroll - 100 gold");
        System.out.println("2. Healing Scroll - 80 gold");
        System.out.println("3. Lightning Scroll - 150 gold");
        System.out.println("4. Teleport Scroll - 200 gold");
        
        System.out.print("Buy which scroll? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            String scrollName = "";
            int price = 0;
            
            switch (choice) {
                case 1: scrollName = "Fireball Scroll"; price = 100; break;
                case 2: scrollName = "Healing Scroll"; price = 80; break;
                case 3: scrollName = "Lightning Scroll"; price = 150; break;
                case 4: scrollName = "Teleport Scroll"; price = 200; break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            
            if (player.getGold() >= price) {
                player.setGold(player.getGold() - price);
                player.getInventory().add(new Consumable(scrollName, "spell", price / 10, "Magic"));
                System.out.println("You bought a " + scrollName + "!");
                player.gainSkillExp("Magic", 15);
            } else {
                System.out.println("Not enough gold!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void enchantEquipment() {
        System.out.println("\n=== ENCHANTMENT SERVICE ===");
        
        if (player.getWeapon() == null && player.getArmor() == null) {
            System.out.println("You need to have equipment equipped to enchant!");
            return;
        }
        
        System.out.println("Available enchantments:");
        System.out.println("1. Fire Enchantment (+25% fire damage) - 500 gold");
        System.out.println("2. Ice Enchantment (+25% ice damage) - 500 gold");
        System.out.println("3. Lightning Enchantment (+25% lightning damage) - 500 gold");
        System.out.println("4. Protection Enchantment (+20% defense) - 400 gold");
        
        System.out.print("Choose enchantment (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            
            String enchantment = "";
            int price = 0;
            
            switch (choice) {
                case 1: enchantment = "Fire"; price = 500; break;
                case 2: enchantment = "Ice"; price = 500; break;
                case 3: enchantment = "Lightning"; price = 500; break;
                case 4: enchantment = "Protection"; price = 400; break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            
            if (player.getGold() >= price) {
                player.setGold(player.getGold() - price);
                
                if (player.getWeapon() != null && choice <= 3) {
                    player.getWeapon().addEnchantment(enchantment, 25);
                    System.out.println("Your weapon has been enchanted with " + enchantment + "!");
                } else if (player.getArmor() != null && choice == 4) {
                    player.getArmor().addEnchantment(enchantment, 20);
                    System.out.println("Your armor has been enchanted with " + enchantment + "!");
                } else {
                    System.out.println("Invalid equipment for this enchantment!");
                    player.setGold(player.getGold() + price); // Refund
                    return;
                }
                
                player.gainSkillExp("Magic", 25);
                player.gainSkillExp("Crafting", 20);
            } else {
                System.out.println("Not enough gold!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void handleArmouryShop() {
        System.out.println("\n=== ARMOURY SHOP ===");
        System.out.println("Fine armor and protection! Gold: " + player.getGold());
        
        List<Armor> armor = GameData.createArmor();
        
        System.out.println("\n=== ARMOR FOR SALE ===");
        for (int i = 0; i < Math.min(8, armor.size()); i++) {
            Armor armorPiece = armor.get(i);
            int price = armorPiece.getDefense() * 8;
            System.out.println((i + 1) + ". " + armorPiece.getName() + 
                             " (Defense: " + armorPiece.getDefense() + ") - " + price + " gold");
        }
        
        System.out.print("Buy which armor? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < Math.min(8, armor.size())) {
                Armor armorPiece = armor.get(choice);
                int price = armorPiece.getDefense() * 8;
                
                if (player.getGold() >= price) {
                    player.setGold(player.getGold() - price);
                    player.getInventory().add(armorPiece);
                    System.out.println("You bought " + armorPiece.getName() + "!");
                    player.gainSkillExp("Trading", 10);
                } else {
                    System.out.println("Not enough gold!");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void handleDomainShops(Domain domain) {
        System.out.println("\n=== " + domain.getName().toUpperCase() + " SHOPS ===");
        List<String> shops = domain.getSpecialShops();
        
        for (int i = 0; i < shops.size(); i++) {
            System.out.println((i + 1) + ". " + shops.get(i));
        }
        
        System.out.print("Visit which shop? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < shops.size()) {
                String shopName = shops.get(choice);
                System.out.println("Welcome to " + shopName + "!");
                System.out.println("This shop offers specialized gear for the " + domain.getName() + "!");
                System.out.println("(Advanced domain shop functionality would be implemented here)");
                player.gainSkillExp("Exploration", 10);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void handlePrestigeHall() {
        if (player.canPrestige()) {
            System.out.println("*** PRESTIGE AVAILABLE! ***");
            System.out.print("Would you like to prestige? (yes/no): ");
            String choice = scanner.nextLine().toLowerCase();
            
            if ("yes".equals(choice)) {
                player.doPrestige();
            }
        } else {
            System.out.println("You need to reach level 50 to prestige.        // Forest Domain
        connections.put("Forest Entrance", List.of("Town Square", "Ancient Grove", "Treant Village"));
        connections.put("Ancient Grove", List.of("Forest Entrance", "Hidden Glade", "Forest Temple"));
        connections.put("Treant Village", List.of("Forest Entrance", "Hidden Glade"));
        connections.put("Hidden Glade", List.of("Ancient Grove", "Treant Village", "Forest Temple"));
        connections.put("Forest Temple", List.of("Ancient Grove", "Hidden Glade"));
        
        // Mountain Domain
        connections.put("Mountain Base", List.of("Town Square", "Rocky Cliffs", "Crystal Caves"));
        connections.put("Rocky Cliffs", List.of("Mountain Base", "Peak Summit", "Dragon's Lair"));
        connections.put("Peak Summit", List.of("Rocky Cliffs", "Dragon's Lair"));
        connections.put("Dragon's Lair", List.of("Rocky Cliffs", "Peak Summit", "Crystal Caves"));
        connections.put("Crystal Caves", List.of("Mountain Base", "Dragon's Lair"));
        
        // Desert Domain
        connections.put("Oasis Town", List.of("Town Square", "Sand Dunes", "Ancient Pyramid"));
        connections.put("Sand Dunes", List.of("Oasis Town", "Mirage Valley", "Buried Temple"));
        connections.put("Ancient Pyramid", List.of("Oasis Town", "Mirage Valley"));
        connections.put("Mirage Valley", List.of("Sand Dunes", "Ancient Pyramid", "Buried Temple"));
        connections.put("Buried Temple", List.of("Sand Dunes", "Mirage Valley"));
        
        // Mythic connections
        connections.put("Mythic Gate", List.of("Town Square", "Eternal Citadel"));
        connections.put("Eternal Citadel", List.of("Mythic Gate", "Immortal Gardens", "Ageless Library"));
        connections.put("Immortal Gardens", List.of("Eternal Citadel", "Forever Falls"));
        connections.put("Ageless Library", List.of("Eternal Citadel", "Eternity Core"));
        connections.put("Forever Falls", List.of("Immortal Gardens", "Eternity Core"));
        connections.put("Eternity Core", List.of("Ageless Library", "Forever Falls"));
        
        return connections;
    }
    
    private Map<String, List<NPC>> createNPCs() {
        Map<String, List<NPC>> npcs = new HashMap<>();
        
        // Town Square NPCs
        List<NPC> townSquareNPCs = new ArrayList<>();
        
        NPC oldManRowan = new NPC("Old Man Rowan", List.of(
            "Ah, another adventurer... just like the ones before.",
            "Do you hear the whispers in the wind? That's the forest remembering what we've forgotten.",
            "My brother went into the Abandoned Cave and never returned. I still wait...",
            "If you ever find a silver amulet engraved with a hawk, bring it to me. It was his.",
            "The world doesn't need more swords -- it needs more listeners."
        ), "Melancholic");
        
        NPCQuest rowanQuest = new NPCQuest("Brother's Memory", "Find the Silver Amulet that belonged to my brother", 
            "Old Man Rowan", "find_item", "Silver Amulet", 1);
        rowanQuest.rewards.put("gold", 200);
        rowanQuest.rewards.put("exp", 300);
        oldManRowan.setQuest(rowanQuest);
        
        Riddle rowanRiddle = new Riddle("I have cities, but no houses. I have mountains, but no trees. I have water, but no fish. What am I?", 
            List.of("map", "a map"));
        rowanRiddle.addReward("gold", 100);
        rowanRiddle.addReward("exp", 150);
        oldManRowan.setRiddle(rowanRiddle);
        
        townSquareNPCs.add(oldManRowan);
        
        NPC ladyMirielle = new NPC("Lady Mirielle", List.of(
            "Once, this town was ruled by seven warrior-queens. Their thrones lie in ruin.",
            "Legends say the Spirit Towers align when the moon is red -- a gate opens.",
            "Not everything that glitters is treasure. Some of it is bait.",
            "If you find a gemstone glowing blue in the Hidden Grove, bring it here. I may have something for you."
        ), "Mysterious");
        
        NPCQuest mirielleQuest = new NPCQuest("The Blue Gemstone", "Retrieve the blue glowing gemstone from the Hidden Grove",
            "Lady Mirielle", "find_item", "Blue Glowing Gemstone", 1);
        mirielleQuest.rewards.put("gold", 150);
        mirielleQuest.rewards.put("item", new Weapon("Mystic Blade", 500, "magic", "Rare"));
        ladyMirielle.setQuest(mirielleQuest);
        
        Riddle mirielleRiddle = new Riddle("The more you take, the more you leave behind. What am I?", 
            List.of("footsteps", "steps"));
        mirielleRiddle.addReward("gold", 80);
        mirielleRiddle.addReward("exp", 120);
        ladyMirielle.setRiddle(mirielleRiddle);
        
        townSquareNPCs.add(ladyMirielle);
        
        NPC townCrier = new NPC("Town Crier", List.of(
            "Hear ye, hear ye! Great adventures await brave souls!",
            "The prestige system has been established for the truly elite!",
            "Beware the new bosses that have emerged from the depths!",
            "Stock up on potions - you'll need them for what's coming!",
            "The Mythic Gates have opened! Only the worthy may pass!",
            "New domains have been discovered beyond the known realms!"
        ), "Enthusiastic");
        
        Riddle crierRiddle = new Riddle("What gets wetter the more it dries?", List.of("towel", "a towel"));
        crierRiddle.addReward("item", new Consumable("Lucky Charm", "luck", 1, "Common"));
        townCrier.setRiddle(crierRiddle);
        
        townSquareNPCs.add(townCrier);
        
        npcs.put("Town Square", townSquareNPCs);
        
        // Add more NPCs for other locations
        npcs.put("Forest Entrance", List.of(
            new NPC("Elven Ranger", List.of(
                "Welcome to the Forest Domain, traveler.",
                "The ancient trees hold many secrets.",
                "Beware the Forest Titan - it guards the heart of these woods."
            ), "Noble")
        ));
        
        npcs.put("Blacksmith", List.of(
            new NPC("Master Blacksmith Thorgrim", List.of(
                "Welcome to my forge! I craft the finest weapons and armor.",
                "Bring me rare materials and I'll forge you something special.",
                "The flames never die here - they've burned for a thousand years."
            ), "Gruff")
        ));
        
        return npcs;
    }
    
    public void start() {
        System.out.println("=".repeat(60));
        System.out.println("        WELCOME TO BATTLEMASTERXX - JAVA EDITION");
        System.out.println("=".repeat(60));
        System.out.println("An enhanced RPG adventure awaits you!");
        System.out.println();
        
        System.out.print("Enter your player name: ");
        String name = scanner.nextLine();
        player = new Player(name);
        
        System.out.println("\nWelcome, " + name + "! Your adventure begins in the Town Square.");
        System.out.println("Type 'help' at any time to see available commands.");
        
        gameLoop();
    }
    
    private void gameLoop() {
        while (player.isAlive()) {
            displayStatus();
            displayMenu();
            
            System.out.print("Your choice: ");
            String choice = scanner.nextLine().toLowerCase().trim();
            
            switch (choice) {
                case "1":
                case "move":
                case "travel":
                    handleMovement();
                    break;
                case "2":
                case "explore":
                    handleExploration();
                    break;
                case "3":
                case "inventory":
                case "inv":
                    showInventory();
                    break;
                case "4":
                case "equip":
                    handleEquipment();
                    break;
                case "5":
                case "item":
                case "use":
                    useItem();
                    break;
                case "6":
                case "companion":
                    handleCompanions();
                    break;
                case "7":
                case "talk":
                case "npc":
                    talkToNPCs();
                    break;
                case "8":
                case "quest":
                case "quests":
                    showQuests();
                    break;
                case "9":
                case "stats":
                case "status":
                    showDetailedStats();
                    break;
                case "10":
                case "shop":
                    handleShops();
                    break;
                case "help":
                    showHelp();
                    break;
                case "quit":
                case "exit":
                    System.out.println("Thanks for playing BattleMasterXX Java Edition!");
                    return;
                default:
                    System.out.println("Invalid command. Type 'help' for available commands.");
            }
            
            // Advance time and apply effects
            gameTime.advanceTime(ThreadLocalRandom.current().nextInt(10, 30));
            player.applyStatusEffects();
        }
    }
    
    private void displayStatus() {
        String horseInfo = player.getHorse() != null ? " | Horse: " + player.getHorse().getName() : " | On foot";
        System.out.println("\n" + "=".repeat(60));
        System.out.println("-- " + player.getLocation() + " (" + player.getCurrentDomain() + ") --");
        System.out.println("HP: " + (int)player.getHp() + "/" + (int)player.getMaxHp() + 
                         " | Mana: " + (int)player.getMana() + "/" + (int)player.getMaxMana());
        System.out.println("Gold: " + player.getGold() + " | Level: " + player.getLevel() + 
                         " | Prestige: " + player.getPrestige() + horseInfo);
        System.out.println("Time: Day " + gameTime.getDay() + " - " + gameTime.getHour() + ":00 (" + 
                         gameTime.getTimeOfDay() + ")");
        System.out.println("Weather: " + gameTime.getCurrentWeather().getType());
        
        if (player.getFaction() != null) {
            System.out.println("Faction: " + player.getFaction());
        }
        
        if (!player.getStatusEffects().isEmpty()) {
            System.out.print("Status Effects: ");
            player.getStatusEffects().forEach((name, effect) -> 
                System.out.print(name + "(" + effect.getTurnsRemaining() + ") "));
            System.out.println();
        }
    }
    
    private void displayMenu() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("1. Move/Travel    2. Explore       3. Inventory");
        System.out.println("4. Equip         5. Use Item      6. Companions");
        System.out.println("7. Talk to NPCs  8. Quests        9. Detailed Stats");
        System.out.println("10. Shops        help. Help       quit. Quit Game");
        System.out.println("-".repeat(40));
    }
    
    private void handleMovement() {
        System.out.println("\nYou are in " + player.getLocation() + " (" + player.getCurrentDomain() + ").");
        List<String> availableLocations = locationConnections.getOrDefault(player.getLocation(), new ArrayList<>());
        
        if (availableLocations.isEmpty()) {
            System.out.println("No exits available from this location.");
            return;
        }
        
        System.out.println("Available destinations:");
        for (int i = 0; i < availableLocations.size(); i++) {
            String destination = availableLocations.get(i);
            String destDomain = getDomainForLocation(destination);
            double travelTime = calculateTravelTime(player.getLocation(), destination);
            System.out.println((i + 1) + ". " + destination + " (" + destDomain + ") - " + 
                             String.format("%.1f", travelTime) + " hours");
        }
        
        System.out.print("Where do you want to go? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < availableLocations.size()) {
                String destination = availableLocations.get(choice);
                travelToLocation(destination);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void travelToLocation(String destination) {
        double travelTime = calculateTravelTime(player.getLocation(), destination);
        String oldDomain = player.getCurrentDomain();
        
        if (player.getHorse() != null) {
            System.out.println("Traveling to " + destination + " on your " + player.getHorse().getName() + "...");
        } else {
            System.out.println("Traveling to " + destination + " on foot...");
        }
        
        System.out.println("Travel time: " + String.format("%.1f", travelTime) + " hours");
        
        // Simulate travel time
        gameTime.advanceTime((int)(travelTime * 60));
        
        // Update player location
        player.setLocation(destination);
        String newDomain = getDomainForLocation(destination);
        player.setCurrentDomain(newDomain);
        
        System.out.println("You arrived at " + destination + "! It's " + 
                         gameTime.getTimeOfDay().toLowerCase() + " and the weather is " + 
                         gameTime.getCurrentWeather().getType().toLowerCase() + ".");
        
        if (!oldDomain.equals(newDomain)) {
            System.out.println("*** You have entered the " + newDomain + "! ***");
            Domain domain = domains.get(newDomain);
            if (domain != null) {
                System.out.println("Theme: " + domain.getTheme());
            }
        }
        
        // Add to discovered locations
        if (!player.getDiscoveredLocations().contains(destination)) {
            player.getDiscoveredLocations().add(destination);
            player.gainSkillExp("Exploration", 20);
        }
        
        // Handle special location events
        handleLocationEvents();
    }
    
    private void handleLocationEvents() {
        String location = player.getLocation();
        String domain = player.getCurrentDomain();
        
        // Special location handling
        switch (location) {
            case "Mythic Gate":
                if (!player.isMythicMode()) {
                    System.out.println("A shimmering barrier blocks your path. You feel you're not yet ready for what lies beyond.");
                    System.out.println("(Mythic Mode unlocked at Prestige 10)");
                    player.setLocation("Town Square");
                    player.setCurrentDomain("Central Domain");
                    return;
                }
                System.out.println("You pass through the Mythic Gate. Cosmic energy surrounds you...");
                break;
                
            case "Prestige Hall":
                System.out.println("\nWelcome to the Prestige Hall!");
                handlePrestigeHall();
                break;
                
            case "Stable":
                System.out.println("\nWelcome to the Stable!");
                handleStable();
                break;
                
            case "Hospital":
                handleHospital();
                break;
                
            case "Faction Hall":
                handleFactionHall();
                break;
        }
        
        // Random encounters based on weather and time
        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            handleRandomEvent();
        }
    }
    
    private void handleRandomEvent() {
        String weather = gameTime.getCurrentWeather().getType();
        String timeOfDay = gameTime.getTimeOfDay();
        
        switch (weather) {
            case "Meteor Shower":
                if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                    System.out.println("\n*** A meteor crashes nearby! ***");
                    Item meteorite = new Weapon("Star Fragment Blade", 1500, "cosmic", "Epic");
                    player.getInventory().add(meteorite);
                    System.out.println("You found a " + meteorite.getName() + "!");
                }
                break;
                
            case "Aurora":
                if ("Night".equals(timeOfDay)) {
                    System.out.println("\nThe beautiful aurora energizes you!");
                    player.heal(50);
                    player.setMana(Math.min(player.getMaxMana(), player.getMana() + 30));
                    System.out.println("You feel refreshed! (+50 HP, +30 Mana)");
                }
                break;
                
            case "Toxic Rain":
                System.out.println("\nThe toxic rain burns your skin!");
                player.setHp(Math.max(1, player.getHp() - 15));
                System.out.println("You take 15 damage from the toxic rain!");
                break;
        }
    }
    
    private void handleExploration() {
        String domain = player.getCurrentDomain();
        System.out.println("Exploring " + domain + " during " + gameTime.getTimeOfDay().toLowerCase() + 
                         " in " + gameTime.getCurrentWeather().getType().toLowerCase() + " weather...");
        
        // Weather effects on exploration
        String weather = gameTime.getCurrentWeather().getType();
        if ("Fog".equals(weather) && ThreadLocalRandom.current().nextDouble() < 0.4) {
            System.out.println("It's too foggy to explore safely.");
            return;
        } else if ("Sandstorm".equals(weather) && ThreadLocalRandom.current().nextDouble() < 0.3) {
            System.out.println("The sandstorm forces you to take shelter.");
            return;
        }
        
        // Generate enemy encounter
        Enemy enemy = generateEnemy();
        
        System.out.println("\nPreparing for battle...");
        String result = BattleSystem.battle(player, enemy, scanner);
        
        if ("victory".equals(result)) {
            player.gainSkillExp("Combat", 20);
            
            // Check for quest completion
            for (NPCQuest quest : new ArrayList<>(player.getNpcQuests())) {
                if ("defeat_enemy".equals(quest.getType()) && enemy.getName().equals(quest.getTarget())) {
                    quest.updateProgress(enemy.getName());
                }
            }
        }
    }
    
    private Enemy generateEnemy() {
        List<Enemy> possibleEnemies = new ArrayList<>();
        int playerLevel = player.getLevel() + (player.getPrestige() * 20);
        
        for (Enemy template : enemyTemplates) {
            // Filter enemies based on player level and domain
            if (!template.isBoss() || (template.isBoss() && ThreadLocalRandom.current().nextDouble() < 0.1)) {
                possibleEnemies.add(template);
            }
        }
        
        Enemy template = possibleEnemies.get(ThreadLocalRandom.current().nextInt(possibleEnemies.size()));
        
        // Scale enemy to player level
        double levelMultiplier = 1 + (playerLevel - 1) * 0.1 + player.getPrestige() * 0.5;
        
        Enemy enemy = new Enemy(
            template.getName(),
            (int)(template.getMaxHp() * levelMultiplier),
            (int)(template.getAttackPower() * levelMultiplier),
            (int)(template.getExpReward() * (1 + player.getPrestige() * 0.2)),
            (int)(template.getGoldReward() * (1 + player.getPrestige() * 0.3)),
            template.isBoss()
        );
        
        // Add domain-specific resistances and weaknesses
        String domain = player.getCurrentDomain();
        if (domain.contains("Fire") || domain.contains("Volcano")) {
            enemy.getResistances().add("fire");
            enemy.getWeaknesses().add("ice");
        } else if (domain.contains("Ice")) {
            enemy.getResistances().add("ice");
            enemy.getWeaknesses().add("fire");
        }
        
        return enemy;
    }
    
    private void showInventory() {
        if (player.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }
        
        System.out.println("\n=== INVENTORY ===");
        for (int i = 0; i < player.getInventory().size(); i++) {
            Item item = player.getInventory().get(i);
            String details = "";
            
            if (item instanceof Weapon) {
                Weapon weapon = (Weapon) item;
                details = " (Power: " + weapon.getPower() + ", Element: " + weapon.getElement() + ")";
            } else if (item instanceof Armor) {
                Armor armor = (Armor) item;
                details = " (Defense: " + armor.getDefense() + ", Element: " + armor.getElement() + ")";
            } else if (item instanceof Consumable) {
                Consumable consumable = (Consumable) item;
                details = " (Effect: " + consumable.getEffect() + " +" + consumable.getValue() + ")";
            }
            
            System.out.println((i + 1) + ". " + item.getName() + details + " [" + item.getRarity() + "]");
        }
    }
    
    private void handleEquipment() {
        List<Item> equipableItems = new ArrayList<>();
        
        for (Item item : player.getInventory()) {
            if (item instanceof Weapon || item instanceof Armor) {
                equipableItems.add(item);
            }
        }
        
        if (equipableItems.isEmpty()) {
            System.out.println("No equipable items in your inventory.");
            return;
        }
        
        System.out.println("\n=== EQUIPABLE ITEMS ===");
        for (int i = 0; i < equipableItems.size(); i++) {
            Item item = equipableItems.get(i);
            String details = "";
            
            if (item instanceof Weapon) {
                Weapon weapon = (Weapon) item;
                details = " (Power: " + weapon.getPower() + ")";
                if (player.getWeapon() != null && player.getWeapon().getName().equals(weapon.getName())) {
                    details += " [EQUIPPED]";
                }
            } else if (item instanceof Armor) {
                Armor armor = (Armor) item;
                details = " (Defense: " + armor.getDefense() + ")";
                if (player.getArmor() != null && player.getArmor().getName().equals(armor.getName())) {
                    details += " [EQUIPPED]";
                }
            }
            
            System.out.println((i + 1) + ". " + item.getName() + details);
        }
        
        System.out.print("Equip which item? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < equipableItems.size()) {
                Item item = equipableItems.get(choice);
                
                if (item instanceof Weapon) {
                    player.setWeapon((Weapon) item);
                    System.out.println("Equipped " + item.getName() + " as weapon.");
                } else if (item instanceof Armor) {
                    player.setArmor((Armor) item);
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
        List<Consumable> usableItems = new ArrayList<>();
        
        for (Item item : player.getInventory()) {
            if (item instanceof Consumable) {
                Consumable consumable = (Consumable) item;
                if (List.of("heal", "mana", "full_heal", "strength").contains(consumable.getEffect())) {
                    usableItems.add(consumable);
                }
            }
        }
        
        if (usableItems.isEmpty()) {
            System.out.println("No usable items in your inventory.");
            return;
        }
        
        System.out.println("\n=== USABLE ITEMS ===");
        for (int i = 0; i < usableItems.size(); i++) {
            Consumable item = usableItems.get(i);
            System.out.println((i + 1) + ". " + item.getName() + " (Effect: " + 
                             item.getEffect() + " +" + item.getValue() + ")");
        }
        
        System.out.print("Use which item? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < usableItems.size()) {
                Consumable item = usableItems.get(choice);
                
                // Check for bleeding
                if (player.getStatusEffects().containsKey("bleed") && "heal".equals(item.getEffect())) {
                    System.out.println("You are bleeding and cannot use healing items!");
                    return;
                }
                
                switch (item.getEffect()) {
                    case "heal":
                        player.heal(item.getValue());
                        System.out.println("You healed " + item.getValue() + " HP.");
                        break;
                    case "mana":
                        player.setMana(Math.min(player.getMaxMana(), player.getMana() + item.getValue()));
                        System.out.println("You restored " + item.getValue() + " mana.");
                        break;
                    case "full_heal":
                        player.setHp(player.getMaxHp());
                        player.setMana(player.getMaxMana());
                        System.out.println("You fully restored your HP and mana!");
                        break;
                    case "strength":
                        Map<String, Object> strengthProps = new HashMap<>();
                        strengthProps.put("bonus", item.getValue());
                        player.addStatusEffect("strength", 10, strengthProps);
                        System.out.println("You feel stronger! (+50% damage for 10 turns)");
                        break;
                }
                
                player.getInventory().remove(item);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void handleCompanions() {
        System.out.println("\n=== COMPANION MANAGEMENT ===");
        System.out.println("1. View Companions");
        System.out.println("2. Recruit Companion");
        System.out.println("3. Dismiss Companion");
        
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                viewCompanions();
                break;
            case "2":
                recruitCompanion();
                break;
            case "3":
                dismissCompanion();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void viewCompanions() {
        if (player.getCompanions().isEmpty()) {
            System.out.println("You have no companions.");
            return;
        }
        
        System.out.println("\nYour Companions (" + player.getCompanions().size() + "/3):");
        for (int i = 0; i < player.getCompanions().size(); i++) {
            Companion companion = player.getCompanions().get(i);
            System.out.println((i + 1) + ". " + companion.getName() + " (" + companion.getRole() + 
                             ") - Level " + companion.getLevel() + " - HP: " + companion.getHp() + 
                             "/" + companion.getMaxHp());
        }
    }
    
    private void recruitCompanion() {
        if (player.getCompanions().size() >= 3) {
            System.out.println("You can only have 3 companions maximum!");
            return;
        }
        
        System.out.print("Companion's name: ");
        String name = scanner.nextLine();
        
        System.out.println("Available roles:");
        System.out.println("1. Warrior (High HP, good attack)");
        System.out.println("2. Healer (Healing abilities)");
        System.out.println("3. Mage (Magic damage)");
        System.out.println("4. Archer (Ranged damage)");
        
        System.out.print("Choose role (1-4): ");
        String roleChoice = scanner.nextLine();
        
        String role;
        switch (roleChoice) {
            case "1": role = "warrior"; break;
            case "2": role = "healer"; break;
            case "3": role = "mage"; break;
            case "4": role = "archer"; break;
            default:
                System.out.println("Invalid role choice.");
                return;
        }
        
        Companion companion = new Companion(name, role);
        player.addCompanion(companion);
        player.gainSkillExp("Diplomacy", 25);
    }
    
    private void dismissCompanion() {
        if (player.getCompanions().isEmpty()) {
            System.out.println("You have no companions to dismiss.");
            return;
        }
        
        viewCompanions();
        System.out.print("Dismiss which companion? (0 to cancel): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < player.getCompanions().size()) {
                Companion dismissed = player.getCompanions().remove(choice);
                System.out.println(dismissed.getName() + " has left your party.");
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }// Main Game Class with Enhanced Features
public class BattleMasterXX {
    private Player player;
    private GameTime gameTime;
    private Map<String, Domain> domains;
    private Map<String, List<String>> locationConnections;
    private Map<String, List<NPC>> locationNPCs;
    private List<Enemy> enemyTemplates;
    private Scanner scanner;
    
    public BattleMasterXX() {
        this.gameTime = new GameTime();
        this.domains = GameData.createDomains();
        this.locationConnections = createLocationConnections();
        this.locationNPCs = createNPCs();
        this.enemyTemplates = GameData.createEnemyTemplates();
        this.scanner = new Scanner(System.in);
    }
    
    private Map<String, List<String>> createLocationConnections() {
        Map<String, List<String>> connections = new HashMap<>();
        
        // Central Domain connections
        connections.put("Town Square", List.of("Blacksmith", "Magic Shop", "Quest Hall", 
            "Armoury", "Prestige Hall", "Stable", "Hospital", "Faction Hall", 
            "Forest Entrance", "Mountain Base", "Oasis Town", "Mythic Gate"));
            
        connections.put("Blacksmith", List.of("Town Square", "Armoury"));
        connections.put("Magic Shop", List.of("Town Square", "Quest Hall"));
        connections.put("Quest Hall", List.of("Town Square", "Magic Shop"));
        connections.put("Armoury", List.of("Town Square", "Blacksmith"));
        connections.put("Prestige Hall", List.of("Town Square"));
        connections.put("Stable", List.of("Town Square"));
        connections.put("Hospital", List.of("Town Square"));
        connections.put("Faction Hall", List.of("Town Square"));
        
        // Forest Domain
        connections.put("Forest Entrance", List.of("Town Square", "Ancient Grove", "Treant Village"));
        connections.put("Ancient Grove", List.of("Forest// Enhanced Enemy System
class Enemy {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private int expReward;
    private int goldReward;
    private boolean isBoss;
    private Map<String, StatusEffect> statusEffects;
    private List<String> resistances;
    private List<String> weaknesses;
    private List<String> abilities;
    
    public Enemy(String name, int hp, int attackPower, int expReward, int goldReward, boolean isBoss) {
        this.name = name;
        this.hp = this.maxHp = hp;
        this.attackPower = attackPower;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.isBoss = isBoss;
        this.statusEffects = new HashMap<>();
        this.resistances = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.abilities = new ArrayList<>();
    }
    
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }
    
    public int attack() {
        // Boss enemies have special attacks
        if (isBoss && ThreadLocalRandom.current().nextDouble() < 0.3) {
            System.out.println(name + " uses a devastating special attack!");
            return attackPower * 2;
        }
        return attackPower;
    }
    
    public void applyStatusEffects() {
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, StatusEffect> entry : statusEffects.entrySet()) {
            String effectName = entry.getKey();
            StatusEffect effect = entry.getValue();
            
            switch (effectName) {
                case "burn":
                    int burnDamage = (Integer) effect.getProperties().getOrDefault("damage", 10);
                    takeDamage(burnDamage);
                    System.out.println(name + " takes " + burnDamage + " burn damage!");
                    break;
                case "poison":
                    int poisonDamage = (Integer) effect.getProperties().getOrDefault("damage", 5);
                    takeDamage(poisonDamage);
                    System.out.println(name + " takes " + poisonDamage + " poison damage!");
                    break;
            }
            
            effect.tick();
            if (effect.isExpired()) {
                toRemove.add(effectName);
            }
        }
        
        toRemove.forEach(statusEffects::remove);
    }
    
    public void addStatusEffect(String name, int turns, Map<String, Object> properties) {
        statusEffects.put(name, new StatusEffect(name, turns, properties));
    }
    
    public boolean isAlive() { return hp > 0; }
    
    // Getters and setters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public boolean isBoss() { return isBoss; }
    public List<String> getResistances() { return resistances; }
    public List<String> getWeaknesses() { return weaknesses; }
    public List<String> getAbilities() { return abilities; }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
}

// ******************************************************************************
// **                        DOMAIN AND WORLD SYSTEM                         **
// ******************************************************************************

// Enhanced Domain System
class Domain {
    private String name;
    private String realm;
    private String theme;
    private List<String> locations;
    private String domainBoss;
    private List<String> specialShops;
    private Map<String, Object> properties;
    
    public Domain(String name, String realm, String theme, List<String> locations, 
                 String domainBoss, List<String> specialShops) {
        this.name = name;
        this.realm = realm;
        this.theme = theme;
        this.locations = new ArrayList<>(locations);
        this.domainBoss = domainBoss;
        this.specialShops = new ArrayList<>(specialShops);
        this.properties = new HashMap<>();
    }
    
    // Getters
    public String getName() { return name; }
    public String getRealm() { return realm; }
    public String getTheme() { return theme; }
    public List<String> getLocations() { return locations; }
    public String getDomainBoss() { return domainBoss; }
    public List<String> getSpecialShops() { return specialShops; }
    public Map<String, Object> getProperties() { return properties; }
}

// ******************************************************************************
// **                           QUEST SYSTEM                                  **
// ******************************************************************************

// Enhanced Quest System
abstract class Quest {
    protected String name;
    protected String description;
    protected boolean completed;
    protected Map<String, Object> rewards;
    
    public Quest(String name, String description) {
        this.name = name;
        this.description = description;
        this.completed = false;
        this.rewards = new HashMap<>();
    }
    
    public abstract boolean checkCompletion(Player player);
    public abstract void giveRewards(Player player);
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    protected void setCompleted(boolean completed) { this.completed = completed; }
}

class NPCQuest extends Quest {
    private String npcName;
    private String type;
    private String target;
    private int quantity;
    private int progress;
    
    public NPCQuest(String name, String description, String npcName, String type, 
                   String target, int quantity) {
        super(name, description);
        this.npcName = npcName;
        this.type = type;
        this.target = target;
        this.quantity = quantity;
        this.progress = 0;
    }
    
    @Override
    public boolean checkCompletion(Player player) {
        switch (type) {
            case "find_item":
                int count = 0;
                for (Item item : player.getInventory()) {
                    if (item.getName().equals(target)) {
                        count++;
                    }
                }
                progress = Math.min(count, quantity);
                break;
            case "defeat_enemy":
                progress = player.getEnemyKillCount().getOrDefault(target, 0);
                break;
        }
        
        boolean wasCompleted = completed;
        completed = progress >= quantity;
        
        return completed && !wasCompleted;
    }
    
    @Override
    public void giveRewards(Player player) {
        if (completed) {
            System.out.println("*** Quest Completed: " + name + " ***");
            
            // Give rewards
            if (rewards.containsKey("gold")) {
                int gold = (Integer) rewards.get("gold");
                player.setGold(player.getGold() + gold);
                System.out.println("You received " + gold + " gold!");
            }
            
            if (rewards.containsKey("exp")) {
                int exp = (Integer) rewards.get("exp");
                player.gainExp(exp);
                System.out.println("You received " + exp + " experience!");
            }
            
            if (rewards.containsKey("item")) {
                Item item = (Item) rewards.get("item");
                player.getInventory().add(item);
                System.out.println("You received " + item.getName() + "!");
            }
        }
    }
    
    public void updateProgress(String targetName) {
        if (type.equals("defeat_enemy") && targetName.equals(target)) {
            progress++;
        }
    }
    
    // Getters
    public String getNpcName() { return npcName; }
    public String getType() { return type; }
    public String getTarget() { return target; }
    public int getQuantity() { return quantity; }
    public int getProgress() { return progress; }
}

// ******************************************************************************
// **                            NPC SYSTEM                                   **
// ******************************************************************************

// Enhanced NPC System
class NPC {
    private String name;
    private List<String> dialogues;
    private int dialogueIndex;
    private Map<String, Object> rewards;
    private NPCQuest quest;
    private Riddle riddle;
    private boolean questGiven;
    private boolean riddleSolved;
    private String personality;
    private List<String> traits;
    
    public NPC(String name, List<String> dialogues, String personality) {
        this.name = name;
        this.dialogues = new ArrayList<>(dialogues);
        this.dialogueIndex = 0;
        this.rewards = new HashMap<>();
        this.questGiven = false;
        this.riddleSolved = false;
        this.personality = personality;
        this.traits = new ArrayList<>();
    }
    
    public void talk(Player player, Scanner scanner) {
        System.out.println("\n" + name + " says:");
        System.out.println(dialogues.get(dialogueIndex));
        dialogueIndex = (dialogueIndex + 1) % dialogues.size();
        
        // Handle riddle
        if (riddle != null && !riddleSolved) {
            System.out.print("\n" + name + " has a riddle for you. Would you like to hear it? (yes/no): ");
            String tryRiddle = scanner.nextLine().toLowerCase();
            if ("yes".equals(tryRiddle)) {
                System.out.println("\n" + name + " asks: " + riddle.getQuestion());
                System.out.print("Your answer: ");
                String answer = scanner.nextLine().toLowerCase().trim();
                
                if (riddle.checkAnswer(answer)) {
                    System.out.println("\n" + name + ": Correct! Well done, adventurer!");
                    riddleSolved = true;
                    riddle.giveRewards(player);
                } else {
                    System.out.println("\n" + name + ": Hmm, that's not quite right. Think more carefully next time.");
                }
            }
        }
        
        // Handle quest offering
        if (quest != null && !questGiven) {
            System.out.print("\n" + name + " seems to have a request. Accept their quest? (yes/no): ");
            String offerQuest = scanner.nextLine().toLowerCase();
            if ("yes".equals(offerQuest)) {
                player.getNpcQuests().add(quest);
                questGiven = true;
                System.out.println("Quest accepted: " + quest.getName());
            }
        }
        
        // Check quest completion
        if (quest != null && questGiven && quest.checkCompletion(player)) {
            quest.giveRewards(player);
            player.getNpcQuests().remove(quest);
            questGiven = false;
        }
    }
    
    public void setQuest(NPCQuest quest) { this.quest = quest; }
    public void setRiddle(Riddle riddle) { this.riddle = riddle; }
    
    // Getters
    public String getName() { return name; }
    public String getPersonality() { return personality; }
    public List<String> getTraits() { return traits; }
}

// Riddle System
class Riddle {
    private String question;
    private List<String> answers;
    private Map<String, Object> rewards;
    
    public Riddle(String question, List<String> answers) {
        this.question = question;
        this.answers = new ArrayList<>();
        for (String answer : answers) {
            this.answers.add(answer.toLowerCase());
        }
        this.rewards = new HashMap<>();
    }
    
    public boolean checkAnswer(String answer) {
        return answers.contains(answer.toLowerCase());
    }
    
    public void giveRewards(Player player) {
        if (rewards.containsKey("gold")) {
            int gold = (Integer) rewards.get("gold");
            player.setGold(player.getGold() + gold);
            System.out.println("You received " + gold + " gold for solving the riddle!");
        }
        
        if (rewards.containsKey("exp")) {
            int exp = (Integer) rewards.get("exp");
            player.gainExp(exp);
            System.out.println("You received " + exp + " experience!");
        }
        
        if (rewards.containsKey("item")) {
            Item item = (Item) rewards.get("item");
            player.getInventory().add(item);
            System.out.println("You received " + item.getName() + "!");
        }
    }
    
    public void addReward(String type, Object value) {
        rewards.put(type, value);
    }
    
    public String getQuestion() { return question; }
}

// ******************************************************************************
// **                        GAME DATA AND FACTORIES                         **
// ******************************************************************************

// Game Data Factory
class GameData {
    public static List<Weapon> createWeapons() {
        List<Weapon> weapons = new ArrayList<>();
        
        // Basic weapons
        weapons.add(new Weapon("Rusty Sword", 5));
        weapons.add(new Weapon("Steel Sword", 10));
        weapons.add(new Weapon("Flaming Axe", 20, "fire", "Uncommon"));
        weapons.add(new Weapon("Excalibur", 50, "holy", "Rare"));
        weapons.add(new Weapon("Dark Bow", 100, "dark", "Epic"));
        weapons.add(new Weapon("Poison Scythe", 250, "poison", "Epic"));
        weapons.add(new Weapon("Demon Trident", 500, "infernal", "Legendary"));
        weapons.add(new Weapon("Black Magic Sword", 750, "dark", "Legendary"));
        weapons.add(new Weapon("Blood Bending Gauntlet", 1000, "blood", "Mythic"));
        
        // Advanced weapons
        weapons.add(new Weapon("Soul Reaper", 1200, "soul", "Mythic"));
        weapons.add(new Weapon("Void Blade", 1500, "void", "Mythic"));
        weapons.add(new Weapon("Titan Hammer", 1800, "earth", "Mythic"));
        weapons.add(new Weapon("Lightning Spear", 2100, "lightning", "Mythic"));
        weapons.add(new Weapon("Frost Claymore", 2400, "ice", "Mythic"));
        weapons.add(new Weapon("Shadow Katana", 2700, "shadow", "Mythic"));
        weapons.add(new Weapon("Phoenix Blade", 3000, "fire", "Mythic"));
        weapons.add(new Weapon("Celestial Bow", 3300, "holy", "Mythic"));
        weapons.add(new Weapon("Doomsday Axe", 3600, "chaos", "Mythic"));
        
        // Ultra rare weapons
        weapons.add(new Weapon("Reality Breaker", 5000, "reality", "Ascended"));
        weapons.add(new Weapon("Time Ripper", 6000, "temporal", "Ascended"));
        weapons.add(new Weapon("Infinity Edge", 10000, "cosmic", "Transcendent"));
        
        return weapons;
    }
    
    public static List<Armor> createArmor() {
        List<Armor> armor = new ArrayList<>();
        
        // Basic armor
        armor.add(new Armor("Cloth Armor", 2));
        armor.add(new Armor("Leather Armor", 5));
        armor.add(new Armor("Chainmail", 10));
        armor.add(new Armor("Golden Silver", 25, "holy", "Uncommon"));
        armor.add(new Armor("Lava Armor", 70, "fire", "Rare"));
        armor.add(new Armor("Phoenix Moon Scale", 250, "fire", "Epic"));
        armor.add(new Armor("Dragon Plate", 400, "physical", "Epic"));
        armor.add(new Armor("Titanium Armor", 600, "physical", "Legendary"));
        armor.add(new Armor("Mystic Robes", 800, "magic", "Legendary"));
        armor.add(new Armor("Void Armor", 1000, "void", "Mythic"));
        armor.add(new Armor("Celestial Plate", 1300, "holy", "Mythic"));
        armor.add(new Armor("Divine Guardian Armor", 1600, "divine", "Mythic"));
        armor.add(new Armor("Godly Protection Suit", 2000, "divine", "Transcendent"));
        
        return armor;
    }
    
    public static List<Consumable> createConsumables() {
        List<Consumable> consumables = new ArrayList<>();
        
        consumables.add(new Consumable("Health Potion", "heal", 30));
        consumables.add(new Consumable("Mana Potion", "mana", 20));
        consumables.add(new Consumable("Greater Health Potion", "heal", 60, "Uncommon"));
        consumables.add(new Consumable("Greater Mana Potion", "mana", 40, "Uncommon"));
        consumables.add(new Consumable("Super Health Potion", "heal", 100, "Rare"));
        consumables.add(new Consumable("Super Mana Potion", "mana", 80, "Rare"));
        consumables.add(new Consumable("Ultimate Health Elixir", "heal", 200, "Epic"));
        consumables.add(new Consumable("Ultimate Mana Elixir", "mana", 150, "Epic"));
        consumables.add(new Consumable("Full Restore Potion", "full_heal", 999, "Legendary"));
        consumables.add(new Consumable("Damage Boost Potion", "damage", 50, "Uncommon"));
        consumables.add(new Consumable("Greater Damage Potion", "damage", 100, "Rare"));
        consumables.add(new Consumable("Elixir of Strength", "strength", 200, "Epic"));
        consumables.add(new Consumable("Phoenix Tear", "resurrection", 1, "Mythic"));
        
        return consumables;
    }
    
    public static List<Horse> createHorses() {
        List<Horse> horses = new ArrayList<>();
        
        horses.add(new Horse("Old Nag", 0.5, 100));
        horses.add(new Horse("Farm Horse", 1.0, 500));
        horses.add(new Horse("Riding Horse", 1.5, 1200, "Common"));
        horses.add(new Horse("War Horse", 2.0, 2500, "Uncommon"));
        horses.add(new Horse("Arabian Stallion", 3.0, 5000, "Rare"));
        horses.add(new Horse("Pegasus", 5.0, 15000, "Epic"));
        horses.add(new Horse("Shadow Steed", 10.0, 50000, "Legendary"));
        horses.add(new Horse("Unicorn", 15.0, 100000, "Mythic"));
        horses.add(new Horse("Dragon Mount", 25.0, 500000, "Transcendent"));
        
        return horses;
    }
    
    public static Map<String, Domain> createDomains() {
        Map<String, Domain> domains = new HashMap<>();
        
        // Central Domain
        domains.put("Central Domain", new Domain(
            "Central Domain", "regular", "Starting area with basic facilities",
            List.of("Town Square", "Blacksmith", "Magic Shop", "Quest Hall", "Armoury", 
                   "Prestige Hall", "Stable", "Hospital", "Faction Hall"),
            "Central Guardian", List.of("Basic Gear Shop", "Potion Emporium")
        ));
        
        // Forest Domain
        domains.put("Forest Domain", new Domain(
            "Forest Domain", "regular", "Lush forests with nature magic",
            List.of("Forest Entrance", "Ancient Grove", "Treant Village", "Hidden Glade", "Forest Temple"),
            "Forest Titan", List.of("Nature's Arsenal", "Druid Supplies")
        ));
        
        // Mountain Domain
        domains.put("Mountain Domain", new Domain(
            "Mountain Domain", "regular", "High peaks and rocky terrain",
            List.of("Mountain Base", "Rocky Cliffs", "Peak Summit", "Dragon's Lair", "Crystal Caves"),
            "Mountain King", List.of("Peak Armory", "Crystal Forge")
        ));
        
        // Desert Domain
        domains.put("Desert Domain", new Domain(
            "Desert Domain", "regular", "Scorching sands and ancient ruins",
            List.of("Oasis Town", "Sand Dunes", "Ancient Pyramid", "Mirage Valley", "Buried Temple"),
            "Sand Pharaoh", List.of("Desert Trader", "Mirage Market")
        ));
        
        // Ice Domain
        domains.put("Ice Domain", new Domain(
            "Ice Domain", "regular", "Frozen wasteland with ice magic",
            List.of("Frozen Village", "Ice Caverns", "Glacier Plains", "Aurora Peak", "Ice Palace"),
            "Frost Emperor", List.of("Frost Forge", "Arctic Outfitters")
        ));
        
        // Mythic Domains
        domains.put("Eternal Domain", new Domain(
            "Eternal Domain", "mythic", "Timeless realm of immortal beings",
            List.of("Eternal Citadel", "Immortal Gardens", "Ageless Library", "Forever Falls", "Eternity Core"),
            "Eternal Emperor", List.of("Immortal Forge", "Eternal Arsenal")
        ));
        
        domains.put("Cosmic Domain", new Domain(
            "Cosmic Domain", "mythic", "Universe-spanning cosmic powers",
            List.of("Galaxy Center", "Star Nursery", "Cosmic Web", "Universal Core", "Reality Nexus"),
            "Cosmic Entity", List.of("Universe Forge", "Cosmic Workshop")
        ));
        
        domains.put("Divine Domain", new Domain(
            "Divine Domain", "mythic", "Realm of gods and divine power",
            List.of("Divine Palace", "God's Throne", "Heaven's Armory", "Celestial Court", "Divine Core"),
            "God Emperor", List.of("Divine Workshop", "God Forge")
        ));
        
        return domains;
    }
    
    public static List<Enemy> createEnemyTemplates() {
        List<Enemy> enemies = new ArrayList<>();
        
        // Regular enemies
        enemies.add(new Enemy("Goblin", 30, 10, 50, 20, false));
        enemies.add(new Enemy("Orc", 50, 15, 100, 40, false));
        enemies.add(new Enemy("Troll", 80, 20, 150, 70, false));
        enemies.add(new Enemy("Skeleton", 40, 12, 60, 30, false));
        enemies.add(new Enemy("Bandit", 35, 14, 70, 25, false));
        enemies.add(new Enemy("Dark Elf", 70, 25, 200, 80, false));
        
        // Elite enemies
        enemies.add(new Enemy("Fire Elemental", 150, 45, 400, 150, false));
        enemies.add(new Enemy("Ice Giant", 180, 50, 500, 180, false));
        enemies.add(new Enemy("Shadow Beast", 120, 40, 350, 120, false));
        enemies.add(new Enemy("Void Wraith", 200, 60, 600, 200, false));
        
        // Bosses
        enemies.add(new Enemy("Dragon", 200, 40, 1000, 500, true));
        enemies.add(new Enemy("Dark Knight", 150, 35, 800, 400, true));
        enemies.add(new Enemy("Goblin Lord", 350, 50, 1100, 650, true));
        enemies.add(new Enemy("Ancient Dragon", 8000, 600, 20000, 15000, true));
        enemies.add(new Enemy("Void Lord", 10000, 700, 25000, 20000, true));
        enemies.add(new Enemy("Death Incarnate", 15000, 2000, 50000, 50000, true));
        
        // Mythic bosses
        enemies.add(new Enemy("Eternal Emperor", 25000, 1000, 50000, 25000, true));
        enemies.add(new Enemy("Cosmic Entity", 40000, 1500, 80000, 40000, true));
        enemies.add(new Enemy("God Emperor", 60000, 2000, 120000, 60000, true));
        enemies.add(new Enemy("Creator God", 100000, 3000, 200000, 100000, true));
        
        return enemies;
    }
}

// ******************************************************************************
// **                        BATTLE SYSTEM                                   **
// ******************************************************************************

// Enhanced Battle System
class BattleSystem {
    private static final Map<String, Spell> SPELLS = createSpells();
    
    private static Map<String, Spell> createSpells() {
        Map<String, Spell> spells = new HashMap<>();
        
        spells.put("fireball", new Spell("Fireball", 15, 25, "fire"));
        spells.put("ice shard", new Spell("Ice Shard", 10, 15, "ice"));
        spells.put("earthly boulder", new Spell("Earthly Boulder", 70, 100, "earth"));
        spells.put("dark shadow spirit", new Spell("Dark Shadow Spirit", 200, 300, "dark"));
        spells.put("heal", new Spell("Heal", 20, 0, "holy", 30));
        spells.put("mega heal", new Spell("Mega Heal", 50, 0, "holy", 80));
        spells.put("burning hands", new Spell("Burning Hands", 20, 15, "fire"));
        spells.put("frost bite", new Spell("Frost Bite", 25, 10, "ice"));
        spells.put("poison cloud", new Spell("Poison Cloud", 30, 5, "poison"));
        spells.put("lightning bolt", new Spell("Lightning Bolt", 40, 80, "lightning"));
        spells.put("meteor", new Spell("Meteor", 100, 200, "cosmic"));
        spells.put("time stop", new Spell("Time Stop", 150, 0, "temporal"));
        
        return spells;
    }
    
    public static String battle(Player player, Enemy enemy, Scanner scanner) {
        System.out.println("A wild " + enemy.getName() + " appears!");
        
        while (player.isAlive() && enemy.isAlive()) {
            System.out.println("\n" + player.getName() + " HP: " + (int)player.getHp() + "/" + (int)player.getMaxHp() + 
                             " | Mana: " + (int)player.getMana() + "/" + (int)player.getMaxMana());
            System.out.println(enemy.getName() + " HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
            
            // Check for freeze status
            if (player.getStatusEffects().containsKey("freeze")) {
                System.out.println("You are frozen and skip this turn!");
                player.applyStatusEffects();
                enemyTurn(player, enemy);
                continue;
            }
            
            System.out.println("\n1. Attack");
            System.out.println("2. Use Magic");
            System.out.println("3. Use Item");
            System.out.println("4. Special Attack");
            System.out.println("5. Companion Actions");
            System.out.println("6. Run");
            System.out.print("Your action: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    playerAttack(player, enemy);
                    break;
                case "2":
                    castSpell(player, enemy, scanner);
                    break;
                case "3":
                    useItem(player, scanner);
                    break;
                case "4":
                    specialAttack(player, enemy, scanner);
                    break;
                case "5":
                    companionActions(player, enemy);
                    break;
                case "6":
                    System.out.println("You fled from battle!");
                    return "fled";
                default:
                    System.out.println("Invalid action!");
                    continue;
            }
            
            player.applyStatusEffects();
            enemy.applyStatusEffects();
            
            if (!enemy.isAlive()) {
                System.out.println("\nYou defeated " + enemy.getName() + "!");
                giveVictoryRewards(player, enemy);
                return "victory";
            }
            
            if (player.isAlive()) {
                enemyTurn(player, enemy);
            }
        }
        
        if (!player.isAlive()) {
            System.out.println("\n*** You have been defeated. Game Over. ***");
            return "defeat";
        }
        
        return "unknown";
    }
    
    private static void playerAttack(Player player, Enemy enemy) {
        int damage = player.attackValue();
        
        // Critical hit chance
        if (ThreadLocalRandom.current().nextDouble() < 0.15) {
            damage *= 2;
            System.out.println("CRITICAL HIT!");
        }
        
        // Apply weaknesses and resistances
        if (player.getWeapon() != null) {
            String element = player.getWeapon().getElement();
            if (enemy.getWeaknesses().contains(element)) {
                damage = (int)(damage * 1.5);
                System.out.println(enemy.getName() + " is weak to " + element + " damage!");
            } else if (enemy.getResistances().contains(element)) {
                damage = (int)(damage * 0.5);
                System.out.println(enemy.getName() + " resists " + element + " damage!");
            }
        }
        
        enemy.takeDamage(damage);
        System.out.println("You hit " + enemy.getName() + " for " + damage + " damage!");
        
        player.gainSkillExp("Combat", 10);
    }
    
    private static void castSpell(Player player, Enemy enemy, Scanner scanner) {
        System.out.println("Available spells:");
        SPELLS.forEach((name, spell) -> {
            if (player.getMana() >= spell.getCost()) {
                System.out.println("- " + name + " (Cost: " + spell.getCost() + " mana)");
            }
        });
        
        System.out.print("Cast spell: ");
        String spellName = scanner.nextLine().toLowerCase();
        
        Spell spell = SPELLS.get(spellName);
        if (spell == null) {
            System.out.println("Unknown spell!");
            return;
        }
        
        if (player.getMana() < spell.getCost()) {
            System.out.println("Not enough mana!");
            return;
        }
        
        player.setMana(player.getMana() - spell.getCost());
        
        if (spell.getDamage() > 0) {
            int damage = spell.getDamage();
            
            // Apply elemental effects
            String element = spell.getElement();
            if (enemy.getWeaknesses().contains(element)) {
                damage = (int)(damage * 1.5);
                System.out.println(enemy.getName() + " is weak to " + element + " magic!");
            } else if (enemy.getResistances().contains(element)) {
                damage = (int)(damage * 0.5);
                System.out.println(enemy.getName() + " resists " + element + " magic!");
            }
            
            enemy.takeDamage(damage);
            System.out.println(spell.getName() + " hits " + enemy.getName() + " for " + damage + " damage!");
            
            // Apply status effects
            applySpellEffects(spell, enemy);
            
        } else if (spell.getHeal() > 0) {
            player.heal(spell.getHeal());
            System.out.println("Healed " + spell.getHeal() + " HP.");
        }
        
        player.gainSkillExp("Magic", 15);
    }
    
    private static void applySpellEffects(Spell spell, Enemy enemy) {
        switch (spell.getElement()) {
            case "fire":
                Map<String, Object> burnProps = new HashMap<>();
                burnProps.put("damage", 10);
                enemy.addStatusEffect("burn", 3, burnProps);
                System.out.println(enemy.getName() + " is burning!");
                break;
            case "ice":
                enemy.addStatusEffect("freeze", 1, new HashMap<>());
                System.out.println(enemy.getName() + " is frozen!");
                break;
            case "poison":
                Map<String, Object> poisonProps = new HashMap<>();
                poisonProps.put("damage", 5);
                enemy.addStatusEffect("poison", 3, poisonProps);
                System.out.println(enemy.getName() + " is poisoned!");
                break;
        }
    }
    
    private static void useItem(Player player, Scanner scanner) {
        List<Consumable> usableItems = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item instanceof Consumable) {
                Consumable consumable = (Consumable) item;
                if (List.of("heal", "mana", "full_heal", "damage").contains(consumable.getEffect())) {
                    usableItems.add(consumable);
                }
            }
        }
        
        if (usableItems.isEmpty()) {
            System.out.println("No usable items in your inventory.");
            return;
        }
        
        System.out.println("Usable items:");
        for (int i = 0; i < usableItems.size(); i++) {
            System.out.println((i + 1) + ". " + usableItems.get(i).getName());
        }
        
        System.out.print("Use which item? (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice == -1) return;
            
            if (choice >= 0 && choice < usableItems.size()) {
                Consumable item = usableItems.get(choice);
                
                // Check for bleeding
                if (player.getStatusEffects().containsKey("bleed") && "heal".equals(item.getEffect())) {
                    System.out.println("You are bleeding and cannot use healing items!");
                    return;
                }
                
                switch (item.getEffect()) {
                    case "heal":
                        player.heal(item.getValue());
                        System.out.println("You healed " + item.getValue() + " HP.");
                        break;
                    case "mana":
                        player.setMana(Math.min(player.getMaxMana(), player.getMana() + item.getValue()));
                        System.out.println("You restored " + item.getValue() + " mana.");
                        break;
                    case "full_heal":
                        player.setHp(player.getMaxHp());
                        player.setMana(player.getMaxMana());
                        System.out.println("You fully restored your HP and mana!");
                        break;
                }
                
                player.getInventory().remove(item);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private static void specialAttack(Player player, Enemy enemy, Scanner scanner) {
        System.out.println("Special Attacks:");
        System.out.println("1. Power Strike (Double damage, costs 20 mana)");
        System.out.println("2. Elemental Burst (Weapon element damage, costs 30 mana)");
        System.out.println("3. Berserker Rage (Triple damage, take damage, costs 40 mana)");
        
        System.out.print("Choose special attack: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                if (player.getMana() >= 20) {
                    player.setMana(player.getMana() - 20);
                    int damage = player.attackValue() * 2;
                    enemy.takeDamage(damage);
                    System.out.println("Power Strike hits for " + damage + " damage!");
                } else {
                    System.out.println("Not enough mana!");
                }
                break;
            case "2":
                if (player.getMana() >= 30) {
                    player.setMana(player.getMana() - 30);
                    int damage = player.attackValue();
                    if (player.getWeapon() != null) {
                        String element = player.getWeapon().getElement();
                        System.out.println("Elemental burst of " + element + " energy!");
                        
                        if (enemy.getWeaknesses().contains(element)) {
                            damage = (int)(damage * 2);
                            System.out.println("Super effective!");
                        }
                    }
                    enemy.takeDamage(damage);
                    System.out.println("Elemental Burst hits for " + damage + " damage!");
                } else {
                    System.out.println("Not enough mana!");
                }
                break;
            case "3":
                if (player.getMana() >= 40) {
                    player.setMana(player.getMana() - 40);
                    int damage = player.attackValue() * 3;
                    enemy.takeDamage(damage);
                    player.setHp(player.getHp() - 20); // Self damage
                    System.out.println("Berserker Rage! You deal " + damage + " damage but take 20 damage!");
                } else {
                    System.out.println("Not enough mana!");
                }
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
    
    private static void companionActions(Player player, Enemy enemy) {
        if (player.getCompanions().isEmpty()) {
            System.out.println("You have no companions to help you!");
            return;
        }
        
        for (Companion companion : player.getCompanions()) {
            companion.act(player, enemy);
            companion.gainExp(10);
        }
    }
    
    private static void enemyTurn(Player player, Enemy enemy) {
        if (enemy.getStatusEffects().containsKey("freeze")) {
            System.out.println(enemy.getName() + " is frozen and cannot move!");
            return;
        }
        
        int damage = enemy.attack();
        player.defend(damage);
        
        // Random status effect chance for certain enemies
        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            applyEnemyStatusEffects(player, enemy);
        }
    }
    
    private static void applyEnemyStatusEffects(Player player, Enemy enemy) {
        String enemyName = enemy.getName().toLowerCase();
        
        if (enemyName.contains("fire") || enemyName.contains("dragon")) {
            Map<String, Object> burnProps = new HashMap<>();
            burnProps.put("damage", 8);
            player.addStatusEffect("burn", 2, burnProps);
            System.out.println("You are burned by " + enemy.getName() + "!");
        } else if (enemyName.contains("ice") || enemyName.contains("frost")) {
            player.addStatusEffect("freeze", 1, new HashMap<>());
            System.out.println("You are frozen by " + enemy.getName() + "!");
        } else if (enemyName.contains("poison") || enemyName.contains("toxic")) {
            Map<String, Object> poisonProps = new HashMap<>();
            poisonProps.put("damage", 5);
            player.addStatusEffect("poison", 3, poisonProps);
            System.out.println("You are poisoned by " + enemy.getName() + "!");
        }
    }
    
    private static void giveVictoryRewards(Player player, Enemy enemy) {
        // Experience and gold
        int expReward = enemy.getExpReward();
        int goldReward = enemy.getGoldReward();
        
        // Faction bonuses
        if ("Shadow Brotherhood".equals(player.getFaction())) {
            goldReward += 500;
            System.out.println("Shadow Brotherhood bonus: +500 gold!");
        }
        
        player.gainExp(expReward);
        player.setGold(player.getGold() + goldReward);
        System.out.println("You earned " + expReward + " EXP and " + goldReward + " gold.");
        
        // Update kill count
        player.getEnemyKillCount().put(enemy.getName(), 
            player.getEnemyKillCount().getOrDefault(enemy.getName(), 0) + 1);
        
        // Boss rewards
        if (enemy.isBoss()) {
            System.out.println("*** Boss " + enemy.getName() + " vanquished! You are victorious! ***");
            
            // Random boss loot
            List<Item> bossLoot = new ArrayList<>();
            bossLoot.addAll(GameData.createWeapons());
            bossLoot.addAll(GameData.createArmor());
            bossLoot.addAll(GameData.createConsumables());
            
            Item reward = bossLoot.get(ThreadLocalRandom.current().nextInt(bossLoot.size()));
            player.getInventory().add(reward);
            System.out.println("Boss dropped: " + reward.getName() + "!");
            
            // Achievements
            if (enemy.getHp() >= 10000) {
                player.addAchievement("Mythic Slayer");
            } else if (enemy.getHp() >= 1000) {
                player.addAchievement("Boss Hunter");
            }
        }
        
        // Random item drop chance
        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            List<Item> commonLoot = GameData.createConsumables();
            Item drop = commonLoot.get(ThreadLocalRandom.current().nextInt(commonLoot.size()));
            player.getInventory().add(drop);
            System.out.println("You found a " + drop.getName() + "!");
        }
    }
}

// Spell class for the battle system
class Spell {
    private String name;
    private int cost;
    private int damage;
    private String element;
    private int heal;
    
    public Spell(String name, int cost, int damage, String element) {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.element = element;
        this.heal = 0;
    }
    
    public Spell(String name, int cost, int damage, String element, int heal) {
        this(name, cost, damage, element);
        this.heal = heal;
    }
    
    // Getters
    public String getName() { return name; }
    public int getCost() { return cost; }
    public int getDamage() { return damage; }
    public String getElement() { return element; }
    public int getHeal() { return heal; }
}

// ******************************************************************************
// **                        MAIN GAME CLASS                                 **
// ******************************************************************************

// Main Game Class with Enhanced Features
        import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 * BATTLEMASTERXX - ENHANCED JAVA RPG
 * ============================================================================
 * File Structure:
 * - GameTime.java (Time and Weather System)
 * - Item.java (All item classes)
 * - Companion.java (Companion system)
 * - Player.java (Player class and related systems)
 * - Enemy.java (Enemy and combat system)
 * - Domain.java (World and location system)
 * - NPC.java (NPC and quest system)
 * - Game.java (Main game loop)
 * ============================================================================
 */

// ******************************************************************************
// **                        TIME AND WEATHER SYSTEM                          **
// ******************************************************************************

// Enhanced Game Time and Weather System
class GameTime {
    private int hour = 6; // Start at 6 AM
    private int day = 1;
    private Weather currentWeather = new Weather();
    
    public void advanceTime(int hours) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.hour %= 24;
            this.day++;
            currentWeather.maybeChangeWeather();
        }
    }
    
    public String getTimeOfDay() {
        if (hour >= 6 && hour < 12) return "Morning";
        else if (hour >= 12 && hour < 18) return "Afternoon";
        else if (hour >= 18 && hour < 21) return "Evening";
        else return "Night";
    }
    
    // Getters and setters
    public int getHour() { return hour; }
    public int getDay() { return day; }
    public Weather getCurrentWeather() { return currentWeather; }
}

class Weather {
    private static final String[] WEATHER_STATES = {
        "Clear", "Rain", "Storm", "Fog", "Snow", "Sunny", "Windy", 
        "Blizzard", "Sandstorm", "Thunderstorm", "Hail", "Tornado", 
        "Aurora", "Eclipse", "Meteor Shower", "Toxic Rain", "Acid Fog", 
        "Flame Storm", "Ice Storm", "Plasma Storm", "Void Winds", "Time Distortion"
    };
    
    private String type = "Clear";
    private int duration = 0;
    
    public void maybeChangeWeather() {
        if (duration <= 0 || ThreadLocalRandom.current().nextDouble() < 0.3) {
            type = WEATHER_STATES[ThreadLocalRandom.current().nextInt(WEATHER_STATES.length)];
            duration = ThreadLocalRandom.current().nextInt(1, 4);
            
            if (List.of("Aurora", "Eclipse", "Meteor Shower", "Plasma Storm", "Void Winds", "Time Distortion").contains(type)) {
                System.out.println("\n*** SPECIAL WEATHER EVENT: " + type + "! ***");
            } else if (List.of("Toxic Rain", "Acid Fog", "Flame Storm").contains(type)) {
                System.out.println("\n*** DANGEROUS WEATHER: " + type + "! Be careful! ***");
            }
        } else {
            duration--;
        }
    }
    
    public String getType() { return type; }
    public int getDuration() { return duration; }
}

// ******************************************************************************
// **                           ITEM SYSTEM                                   **
// ******************************************************************************

// Enhanced Item System
abstract class Item {
    protected String name;
    protected String description;
    protected String rarity;
    
    public Item(String name, String description, String rarity) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRarity() { return rarity; }
}

class Weapon extends Item {
    private int power;
    private String element;
    private Map<String, Object> enchantments;
    
    public Weapon(String name, int power, String element, String rarity) {
        super(name, "A " + rarity.toLowerCase() + " weapon", rarity);
        this.power = power;
        this.element = element != null ? element : "physical";
        this.enchantments = new HashMap<>();
    }
    
    public Weapon(String name, int power) {
        this(name, power, "physical", "Common");
    }
    
    public void addEnchantment(String enchantment, Object value) {
        enchantments.put(enchantment, value);
    }
    
    public int getPower() { return power; }
    public String getElement() { return element; }
    public Map<String, Object> getEnchantments() { return enchantments; }
}

class Armor extends Item {
    private int defense;
    private String element;
    private Map<String, Object> enchantments;
    
    public Armor(String name, int defense, String element, String rarity) {
        super(name, "A " + rarity.toLowerCase() + " armor piece", rarity);
        this.defense = defense;
        this.element = element != null ? element : "physical";
        this.enchantments = new HashMap<>();
    }
    
    public Armor(String name, int defense) {
        this(name, defense, "physical", "Common");
    }
    
    public void addEnchantment(String enchantment, Object value) {
        enchantments.put(enchantment, value);
    }
    
    public int getDefense() { return defense; }
    public String getElement() { return element; }
    public Map<String, Object> getEnchantments() { return enchantments; }
}

class Consumable extends Item {
    private String effect;
    private int value;
    
    public Consumable(String name, String effect, int value, String rarity) {
        super(name, "A " + rarity.toLowerCase() + " consumable", rarity);
        this.effect = effect;
        this.value = value;
    }
    
    public Consumable(String name, String effect, int value) {
        this(name, effect, value, "Common");
    }
    
    public String getEffect() { return effect; }
    public int getValue() { return value; }
}

// Enhanced Companion System
class Companion {
    private String name;
    private String role;
    private int hp;
    private int maxHp;
    private int attack;
    private int heal;
    private int level;
    private int exp;
    private List<String> abilities;
    private Map<String, Integer> stats;
    
    public Companion(String name, String role) {
        this.name = name;
        this.role = role;
        this.level = 1;
        this.exp = 0;
        this.abilities = new ArrayList<>();
        this.stats = new HashMap<>();
        
        switch (role.toLowerCase()) {
            case "warrior":
                this.hp = this.maxHp = 80;
                this.attack = 25;
                this.heal = 0;
                this.abilities.add("Power Strike");
                this.abilities.add("Shield Bash");
                break;
            case "healer":
                this.hp = this.maxHp = 60;
                this.attack = 10;
                this.heal = 40;
                this.abilities.add("Greater Heal");
                this.abilities.add("Cure Ailments");
                break;
            case "mage":
                this.hp = this.maxHp = 50;
                this.attack = 30;
                this.heal = 0;
                this.abilities.add("Fireball");
                this.abilities.add("Lightning Bolt");
                break;
            case "archer":
                this.hp = this.maxHp = 70;
                this.attack = 35;
                this.heal = 0;
                this.abilities.add("Piercing Shot");
                this.abilities.add("Multi-Shot");
                break;
            default:
                this.hp = this.maxHp = 50;
                this.attack = 15;
                this.heal = 0;
        }
    }
    
    public void act(Player player, Enemy enemy) {
        if ("healer".equals(role) && player.getHp() < player.getMaxHp()) {
            int healAmount = heal + (level * 5);
            player.heal(healAmount);
            System.out.println(name + " heals you for " + healAmount + " HP!");
        } else if (List.of("warrior", "mage", "archer").contains(role)) {
            int damage = attack + (level * 3);
            enemy.takeDamage(damage);
            System.out.println(name + " attacks " + enemy.getName() + " for " + damage + " damage!");
        }
    }
    
    public void gainExp(int amount) {
        exp += amount;
        int expNeeded = level * 100;
        if (exp >= expNeeded) {
            level++;
            exp -= expNeeded;
            maxHp += 10;
            hp = maxHp;
            attack += 5;
            heal += 5;
            System.out.println(name + " leveled up to level " + level + "!");
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getLevel() { return level; }
    public List<String> getAbilities() { return abilities; }
}

// ******************************************************************************
// **                           PLAYER SYSTEM                                 **
// ******************************************************************************

// Enhanced Status Effects System
class StatusEffect {
    private String name;
    private int turnsRemaining;
    private Map<String, Object> properties;
    
    public StatusEffect(String name, int turns) {
        this.name = name;
        this.turnsRemaining = turns;
        this.properties = new HashMap<>();
    }
    
    public StatusEffect(String name, int turns, Map<String, Object> properties) {
        this(name, turns);
        this.properties = properties;
    }
    
    public void tick() {
        turnsRemaining--;
    }
    
    public boolean isExpired() {
        return turnsRemaining <= 0;
    }
    
    // Getters
    public String getName() { return name; }
    public int getTurnsRemaining() { return turnsRemaining; }
    public Map<String, Object> getProperties() { return properties; }
}

// Enhanced Player Class
class Player {
    private String name;
    private double hp;
    private double maxHp;
    private double mana;
    private double maxMana;
    private int gold;
    private int exp;
    private int level;
    private int prestige;
    private String location;
    private String currentDomain;
    private List<Item> inventory;
    private Weapon weapon;
    private Armor armor;
    private List<Companion> companions;
    private int maxCompanions;
    private Horse horse;
    private boolean mythicMode;
    private String faction;
    private Map<String, StatusEffect> statusEffects;
    private Quest currentQuest;
    private List<NPCQuest> npcQuests;
    
    // New attributes for enhanced gameplay
    private Map<String, Integer> skills;
    private List<String> achievements;
    private int reputation;
    private Map<String, Integer> domainReputation;
    private List<String> discoveredLocations;
    private Map<String, Integer> enemyKillCount;
    
    public Player(String name) {
        this.name = name;
        this.hp = this.maxHp = 100.0;
        this.mana = this.maxMana = 50.0;
        this.gold = 0;
        this.exp = 0;
        this.level = 1;
        this.prestige = 0;
        this.location = "Town Square";
        this.currentDomain = "Central Domain";
        this.inventory = new ArrayList<>();
        this.companions = new ArrayList<>();
        this.maxCompanions = 3;
        this.mythicMode = false;
        this.statusEffects = new HashMap<>();
        this.npcQuests = new ArrayList<>();
        this.skills = new HashMap<>();
        this.achievements = new ArrayList<>();
        this.reputation = 0;
        this.domainReputation = new HashMap<>();
        this.discoveredLocations = new ArrayList<>();
        this.enemyKillCount = new HashMap<>();
        
        // Initialize skills
        initializeSkills();
        
        // Add starting location to discovered
        discoveredLocations.add(location);
    }
    
    private void initializeSkills() {
        skills.put("Combat", 1);
        skills.put("Magic", 1);
        skills.put("Stealth", 1);
        skills.put("Diplomacy", 1);
        skills.put("Crafting", 1);
        skills.put("Trading", 1);
        skills.put("Exploration", 1);
        skills.put("Survival", 1);
    }
    
    public void gainSkillExp(String skill, int amount) {
        int currentLevel = skills.get(skill);
        int expNeeded = currentLevel * 50;
        amount += expNeeded;
        
        if (amount >= expNeeded * 2) {
            skills.put(skill, currentLevel + 1);
            System.out.println("*** " + skill + " skill increased to level " + (currentLevel + 1) + "! ***");
        }
    }
    
    public void addAchievement(String achievement) {
        if (!achievements.contains(achievement)) {
            achievements.add(achievement);
            System.out.println("*** ACHIEVEMENT UNLOCKED: " + achievement + " ***");
            reputation += 10;
        }
    }
    
    public int attackValue() {
        int base = 10 + level * 2;
        int weaponPower = weapon != null ? weapon.getPower() : 0;
        int prestigeBonus = prestige * 10;
        int skillBonus = skills.get("Combat") * 5;
        return base + weaponPower + prestigeBonus + skillBonus;
    }
    
    public void defend(int damage) {
        int defense = armor != null ? armor.getDefense() : 0;
        int prestigeDefense = prestige * 5;
        int skillDefense = skills.get("Combat") * 2;
        
        // Faction bonus
        if ("Knights of the Phoenix".equals(faction)) {
            defense += 80;
        }
        
        int realDamage = Math.max(damage - defense - prestigeDefense - skillDefense, 0);
        hp -= realDamage;
        System.out.println("You received " + realDamage + " damage (after armor, prestige, and skill bonuses).");
    }
    
    public void heal(int amount) {
        // Check for bleeding first
        if (statusEffects.containsKey("bleed")) {
            System.out.println("You are bleeding and cannot heal!");
            return;
        }
        
        // Poison reduces healing
        if (statusEffects.containsKey("poison")) {
            amount = amount / 2;
            System.out.println("Poison reduces your healing!");
        }
        
        hp = Math.min(maxHp, hp + amount);
    }
    
    public void gainExp(int amount) {
        this.exp += amount;
        int needed = level * 100;
        
        if (exp >= needed) {
            level++;
            exp -= needed;
            maxHp += 20;
            hp = maxHp;
            maxMana += 10;
            mana = maxMana;
            System.out.println("*** You leveled up! You are now level " + level + "! ***");
            
            // Skill points on level up
            System.out.println("You gained skill points! Choose a skill to improve.");
            gainSkillExp("Combat", 25);
        }
        
        if (level >= 50) {
            System.out.println("*** PRESTIGE AVAILABLE! You can now prestige at level 50! ***");
            System.out.println("Visit the Prestige Hall to prestige and gain exclusive rewards!");
        }
    }
    
    public boolean canPrestige() {
        return level >= 50;
    }
    
    public boolean doPrestige() {
        if (!canPrestige()) {
            System.out.println("You must reach level 50 to prestige!");
            return false;
        }
        
        prestige++;
        level = 1;
        exp = 0;
        hp = 100 + (prestige * 50);
        maxHp = hp;
        mana = 50 + (prestige * 25);
        maxMana = mana;
        
        System.out.println("*** PRESTIGE " + prestige + " ACHIEVED! ***");
        System.out.println("Level reset to 1, but you gained permanent bonuses!");
        System.out.println("New HP: " + (int)maxHp + ", New Mana: " + (int)maxMana);
        System.out.println("Attack bonus: +" + (prestige * 10) + ", Defense bonus: +" + (prestige * 5));
        
        if (prestige >= 10 && !mythicMode) {
            mythicMode = true;
            System.out.println("*** MYTHIC PRESTIGE UNLOCKED! ***");
            System.out.println("You now have access to the Mythic Gate and Mythic Challenges!");
            addAchievement("Mythic Ascendant");
        }
        
        if (prestige >= 25) {
            addAchievement("True Master");
        }
        
        return true;
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
    
    public void applyStatusEffects() {
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, StatusEffect> entry : statusEffects.entrySet()) {
            String effectName = entry.getKey();
            StatusEffect effect = entry.getValue();
            
            switch (effectName) {
                case "burn":
                    int burnDamage = (Integer) effect.getProperties().getOrDefault("damage", 10);
                    hp -= burnDamage;
                    System.out.println("You take " + burnDamage + " burn damage!");
                    break;
                case "regeneration":
                    int healAmount = (Integer) effect.getProperties().getOrDefault("heal", 15);
                    heal(healAmount);
                    System.out.println("You regenerate " + healAmount + " HP!");
                    break;
                case "mana_burn":
                    int manaBurn = (Integer) effect.getProperties().getOrDefault("burn", 5);
                    mana = Math.max(0, mana - manaBurn);
                    System.out.println("You lose " + manaBurn + " mana!");
                    break;
            }
            
            effect.tick();
            if (effect.isExpired()) {
                toRemove.add(effectName);
            }
        }
        
        toRemove.forEach(statusEffects::remove);
    }
    
    public void addStatusEffect(String name, int turns) {
        addStatusEffect(name, turns, new HashMap<>());
    }
    
    public void addStatusEffect(String name, int turns, Map<String, Object> properties) {
        statusEffects.put(name, new StatusEffect(name, turns, properties));
    }
    
    public void addCompanion(Companion companion) {
        if (companions.size() < maxCompanions) {
            companions.add(companion);
            System.out.println(companion.getName() + " joined your party!");
        } else {
            System.out.println("You can only have " + maxCompanions + " companions!");
        }
    }
    
    // Enhanced getters and setters
    public String getName() { return name; }
    public double getHp() { return hp; }
    public double getMaxHp() { return maxHp; }
    public double getMana() { return mana; }
    public double getMaxMana() { return maxMana; }
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getExp() { return exp; }
    public int getLevel() { return level; }
    public int getPrestige() { return prestige; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCurrentDomain() { return currentDomain; }
    public void setCurrentDomain(String domain) { this.currentDomain = domain; }
    public List<Item> getInventory() { return inventory; }
    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public Armor getArmor() { return armor; }
    public void setArmor(Armor armor) { this.armor = armor; }
    public List<Companion> getCompanions() { return companions; }
    public Horse getHorse() { return horse; }
    public void setHorse(Horse horse) { this.horse = horse; }
    public boolean isMythicMode() { return mythicMode; }
    public String getFaction() { return faction; }
    public void setFaction(String faction) { this.faction = faction; }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
    public Map<String, Integer> getSkills() { return skills; }
    public List<String> getAchievements() { return achievements; }
    public int getReputation() { return reputation; }
    public List<String> getDiscoveredLocations() { return discoveredLocations; }
    public Map<String, Integer> getEnemyKillCount() { return enemyKillCount; }
}

// Horse System
class Horse {
    private String name;
    private double speed;
    private int price;
    private String rarity;
    private List<String> abilities;
    
    public Horse(String name, double speed, int price, String rarity) {
        this.name = name;
        this.speed = speed;
        this.price = price;
        this.rarity = rarity;
        this.abilities = new ArrayList<>();
    }
    
    public Horse(String name, double speed, int price) {
        this(name, speed, price, "Common");
    }
    
    // Getters
    public String getName() { return name; }
    public double getSpeed() { return speed; }
    public int getPrice() { return price; }
    public String getRarity() { return rarity; }
    public List<String> getAbilities() { return abilities; }
}

// ******************************************************************************
// **                           ENEMY SYSTEM                                  **
// ******************************************************************************

// Enhanced Enemy System
}

//