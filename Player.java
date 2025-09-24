// ================================================================================
// ENHANCED PLAYER CLASS - Should be in Player.java
// Contains all player-related data and methods with full functionality
// ================================================================================
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
class Player {
    // Core Stats
    private String name;
    private int hp, maxHp;
    private int mana, maxMana;
    private int stamina, maxStamina;
    private int level;
    private int exp;
    private int gold;
    private int prestige;
    
    // Location and Domain
    private String location;
    private String currentDomain;
    
    // Equipment
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private List<Accessory> equippedAccessories;
    
    // Collections
    private List<Item> inventory;
    private List<Companion> companions;
    private List<Quest> activeQuests;
    private Map<String, Integer> skills;
    private Set<Achievement> unlockedAchievements;
    
    // Special Features
    private Horse horse;
    private Faction faction;
    private Map<String, StatusEffect> statusEffects;
    private boolean mythicMode;
    private int maxCompanions;
    
    // Enhanced Stats
    private int reputation;
    private int combatRating;
    private Map<String, Integer> elementalResistances;
    private List<String> knownSpells;
    private int totalGoldEarned;
    private int enemiesDefeated;
    private int questsCompleted;
    private Map<String, Integer> skillExperience;
    
    private static Scanner scanner = new Scanner(System.in);
    
    /**
     * Constructor for creating a new player
     */
    public Player(String name) {
        this.name = name;
        initializeBaseStats();
        initializeCollections();
        initializeLocation();
        initializeSkills();
        
        System.out.println("Hero " + name + " rises to face the challenges ahead!");
    }
    
    private void initializeBaseStats() {
        this.hp = 100;
        this.maxHp = 100;
        this.mana = 50;
        this.maxMana = 50;
        this.stamina = 100;
        this.maxStamina = 100;
        this.level = 1;
        this.exp = 0;
        this.gold = 100;
        this.prestige = 0;
        this.reputation = 0;
        this.combatRating = 10;
        this.maxCompanions = 3;
        this.mythicMode = false;
        this.totalGoldEarned = 0;
        this.enemiesDefeated = 0;
        this.questsCompleted = 0;
    }
    
    private void initializeCollections() {
        this.inventory = new ArrayList<>();
        this.companions = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.skills = new LinkedHashMap<>();
        this.skillExperience = new HashMap<>();
        this.unlockedAchievements = new HashSet<>();
        this.equippedAccessories = new ArrayList<>();
        this.statusEffects = new HashMap<>();
        this.elementalResistances = new HashMap<>();
        this.knownSpells = new ArrayList<>();
        
        // Initialize elemental resistances
        String[] elements = {"fire", "ice", "lightning", "earth", "dark", "light", "void", "divine"};
        for (String element : elements) {
            this.elementalResistances.put(element, 0);
        }
    }
    
    private void initializeLocation() {
        this.location = "Town Square";
        this.currentDomain = "Central Domain";
    }
    
    private void initializeSkills() {
        String[] skillNames = {"Combat", "Magic", "Crafting", "Trading", "Exploration", 
                              "Leadership", "Stealth", "Diplomacy", "Survival", "Alchemy"};
        
        for (String skill : skillNames) {
            this.skills.put(skill, 1);
            this.skillExperience.put(skill, 0);
        }
        
        // Starting spells
        this.knownSpells.addAll(List.of("heal", "fireball", "mana_restore"));
    }
    
    /**
     * Advanced attack calculation with multiple bonuses
     */
    public int calculateAttackPower() {
        int basePower = 10 + (level * 3) + (prestige * 15);
        int weaponPower = equippedWeapon != null ? equippedWeapon.getPower() : 0;
        int skillBonus = skills.get("Combat") * 3;
        int companionBonus = companions.size() * 8;
        int factionBonus = faction != null ? faction.getAttackBonus() : 0;
        
        // Accessory bonuses
        int accessoryBonus = equippedAccessories.stream()
            .mapToInt(acc -> acc.getBonuses().getOrDefault("attack", 0))
            .sum();
        
        return basePower + weaponPower + skillBonus + companionBonus + factionBonus + accessoryBonus;
    }
    
    /**
     * Advanced defense calculation
     */
    public int calculateDefense() {
        int baseDefense = prestige * 8;
        int armorDefense = equippedArmor != null ? equippedArmor.getDefense() : 0;
        int skillBonus = skills.get("Combat") * 2;
        int factionBonus = faction != null ? faction.getDefenseBonus() : 0;
        
        // Accessory bonuses
        int accessoryBonus = equippedAccessories.stream()
            .mapToInt(acc -> acc.getBonuses().getOrDefault("defense", 0))
            .sum();
        
        return baseDefense + armorDefense + skillBonus + factionBonus + accessoryBonus;
    }
    
    /**
     * Take damage with comprehensive defense system
     */
    public void takeDamage(int damage, String element) {
        // Apply elemental resistance
        int resistance = elementalResistances.getOrDefault(element, 0);
        if (resistance > 0) {
            damage = Math.max(1, damage - resistance);
            System.out.printf("Your %s resistance reduces damage by %d!\n", element, resistance);
        }
        
        // Apply armor defense
        int actualDamage = Math.max(1, damage - calculateDefense());
        
        // Status effect modifications
        if (statusEffects.containsKey("vulnerable")) {
            actualDamage = (int)(actualDamage * 1.5);
            System.out.println("Vulnerability increases damage taken!");
        }
        
        this.hp = Math.max(0, this.hp - actualDamage);
        System.out.printf("You take %d %s damage! HP: %d/%d\n", actualDamage, element, hp, maxHp);
        
        // Check for death
        if (this.hp <= 0) {
            handleDeath();
        }
    }
    
    /**
     * Handle player death with revival options
     */
    private void handleDeath() {
        System.out.println("\n*** YOU HAVE FALLEN IN BATTLE ***");
        
        // Check for resurrection items
        List<Item> reviveItems = inventory.stream()
            .filter(item -> item.getName().contains("Phoenix") || item.getName().contains("Resurrection"))
            .toList();
            
        if (!reviveItems.isEmpty()) {
            System.out.println("You have items that can revive you:");
            for (int i = 0; i < reviveItems.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, reviveItems.get(i).getName());
            }
            System.out.print("Use which item? (number or 'accept death'): ");
            
            String choice = scanner.nextLine();
            try {
                int itemChoice = Integer.parseInt(choice) - 1;
                if (itemChoice >= 0 && itemChoice < reviveItems.size()) {
                    inventory.remove(reviveItems.get(itemChoice));
                    this.hp = maxHp / 2;
                    System.out.println("*** YOU HAVE BEEN REVIVED! ***");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        
        // Faction revival
        if (faction != null && faction.getName().equals("Order of the Phoenix")) {
            System.out.println("The Phoenix faction's blessing revives you!");
            this.hp = maxHp / 4;
            return;
        }
        
        System.out.println("Your adventure ends here...");
    }
    
    /**
     * Enhanced healing system
     */
    public void heal(int amount) {
        if (statusEffects.containsKey("bleeding")) {
            System.out.println("Bleeding reduces healing effectiveness!");
            amount /= 2;
        }
        
        if (statusEffects.containsKey("cursed")) {
            System.out.println("A curse prevents healing!");
            return;
        }
        
        int oldHp = this.hp;
        this.hp = Math.min(this.maxHp, this.hp + amount);
        int actualHealing = this.hp - oldHp;
        
        System.out.printf("Healed %d HP. Current: %d/%d\n", actualHealing, hp, maxHp);
        
        // Skill experience for healing
        if (actualHealing > 0) {
            gainSkillExperience("Magic", 2);
        }
    }
    
    /**
     * Enhanced mana restoration
     */
    public void restoreMana(int amount) {
        int oldMana = this.mana;
        this.mana = Math.min(this.maxMana, this.mana + amount);
        int actualRestore = this.mana - oldMana;
        
        System.out.printf("Restored %d mana. Current: %d/%d\n", actualRestore, mana, maxMana);
    }
    
    /**
     * Stamina system for exploration and combat
     */
    public void restoreStamina(int amount) {
        this.stamina = Math.min(this.maxStamina, this.stamina + amount);
        System.out.printf("Restored %d stamina. Current: %d/%d\n", amount, stamina, maxStamina);
    }
    
    public void consumeStamina(int amount) {
        this.stamina = Math.max(0, this.stamina - amount);
        if (this.stamina == 0) {
            System.out.println("You are exhausted! Find a place to rest.");
        }
    }
    
    /**
     * Enhanced experience gain with skill bonuses
     */
    public void gainExperience(int amount) {
        // Leadership skill increases XP gain
        int leadershipBonus = skills.get("Leadership") * 2;
        int totalExp = amount + leadershipBonus;
        
        this.exp += totalExp;
        System.out.printf("Gained %d experience! Total: %d\n", totalExp, exp);
        
        checkForLevelUp();
        AchievementSystem.checkExperienceAchievements(this);
    }
    
    /**
     * Skill experience gain system
     */
    public void gainSkillExperience(String skillName, int amount) {
        if (!skills.containsKey(skillName)) return;
        
        int currentExp = skillExperience.get(skillName);
        int newExp = currentExp + amount;
        skillExperience.put(skillName, newExp);
        
        // Check for skill level up
        int currentLevel = skills.get(skillName);
        int requiredExp = currentLevel * 100;
        
        if (newExp >= requiredExp) {
            skills.put(skillName, currentLevel + 1);
            skillExperience.put(skillName, newExp - requiredExp);
            System.out.printf("*** %s skill increased to level %d! ***\n", skillName, currentLevel + 1);
            
            // Unlock new abilities
            unlockSkillAbilities(skillName, currentLevel + 1);
        }
    }
    
    /**
     * Unlock abilities based on skill levels
     */
    private void unlockSkillAbilities(String skillName, int level) {
        switch (skillName) {
            case "Magic" -> {
                if (level == 5) {
                    knownSpells.add("ice_storm");
                    System.out.println("Learned new spell: Ice Storm!");
                } else if (level == 10) {
                    knownSpells.add("meteor");
                    System.out.println("Learned new spell: Meteor!");
                }
            }
            case "Combat" -> {
                if (level == 10) {
                    System.out.println("Unlocked: Critical Strike ability!");
                } else if (level == 15) {
                    System.out.println("Unlocked: Berserker Rage ability!");
                }
            }
            case "Crafting" -> {
                if (level == 5) {
                    System.out.println("Can now craft magical items!");
                } else if (level == 10) {
                    System.out.println("Can now craft legendary equipment!");
                }
            }
        }
    }
    
    /**
     * Enhanced level up system
     */
    private void checkForLevelUp() {
        int expNeeded = level * 100;
        while (exp >= expNeeded) {
            levelUp();
            expNeeded = level * 100;
        }
    }
    
    private void levelUp() {
        exp -= level * 100;
        level++;
        
        // Enhanced stat increases
        int hpIncrease = 25 + (prestige * 8) + ThreadLocalRandom.current().nextInt(5, 15);
        int manaIncrease = 15 + (prestige * 5) + ThreadLocalRandom.current().nextInt(3, 10);
        int staminaIncrease = 10 + ThreadLocalRandom.current().nextInt(5, 10);
        
        maxHp += hpIncrease;
        hp = maxHp; // Full heal on level up
        maxMana += manaIncrease;
        mana = maxMana;
        maxStamina += staminaIncrease;
        stamina = maxStamina;
        
        System.out.println("\n*** ⭐ LEVEL UP! ⭐ ***");
        System.out.printf("You are now level %d!\n", level);
        System.out.printf("HP increased by %d to %d\n", hpIncrease, maxHp);
        System.out.printf("Mana increased by %d to %d\n", manaIncrease, maxMana);
        System.out.printf("Stamina increased by %d to %d\n", staminaIncrease, maxStamina);
        
        // Attribute point allocation
        allocateAttributePoints();
        
        // Check for prestige availability
        if (level >= 50) {
            System.out.println("*** PRESTIGE AVAILABLE! ***");
            System.out.println("Visit the Prestige Hall to unlock powerful bonuses!");
        }
        
        // Check for mythic mode
        if (prestige >= 10 && !mythicMode) {
            mythicMode = true;
            System.out.println("*** MYTHIC MODE UNLOCKED! ***");
            System.out.println("You can now access the Mythic Realm!");
        }
        
        AchievementSystem.checkLevelAchievements(this);
    }
    
    /**
     * Allow player to allocate attribute points on level up
     */
    private void allocateAttributePoints() {
        int points = 3 + (prestige > 0 ? 2 : 0);
        System.out.printf("You have %d attribute points to allocate:\n", points);
        
        while (points > 0) {
            System.out.println("1. Strength (+2 Attack Power)");
            System.out.println("2. Constitution (+15 HP)");
            System.out.println("3. Intelligence (+10 Mana)");
            System.out.println("4. Agility (+8 Stamina)");
            System.out.println("5. Wisdom (+5% Magic Resistance)");
            System.out.printf("Points remaining: %d\n", points);
            System.out.print("Choose attribute: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> {
                        combatRating += 2;
                        System.out.println("Strength increased! Attack power improved.");
                    }
                    case 2 -> {
                        maxHp += 15;
                        hp += 15;
                        System.out.println("Constitution increased! HP improved.");
                    }
                    case 3 -> {
                        maxMana += 10;
                        mana += 10;
                        System.out.println("Intelligence increased! Mana improved.");
                    }
                    case 4 -> {
                        maxStamina += 8;
                        stamina += 8;
                        System.out.println("Agility increased! Stamina improved.");
                    }
                    case 5 -> {
                        for (String element : elementalResistances.keySet()) {
                            elementalResistances.put(element, elementalResistances.get(element) + 2);
                        }
                        System.out.println("Wisdom increased! Elemental resistances improved.");
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }
                points--;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
    
    /**
     * Enhanced inventory display with categorization
     */
    public void showInventory() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        INVENTORY                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }
        
        Map<String, List<Item>> categorizedItems = categorizeInventory();
        
        for (Map.Entry<String, List<Item>> entry : categorizedItems.entrySet()) {
            System.out.println("\n┌─ " + entry.getKey() + " ─┐");
            for (int i = 0; i < entry.getValue().size(); i++) {
                Item item = entry.getValue().get(i);
                String rarity = getRarityIndicator(item);
                System.out.printf("│ %d. %s %s %s\n", i + 1, rarity, item.getName(), item.getDescription());
            }
            System.out.println("└" + "─".repeat(entry.getKey().length() + 4) + "┘");
        }
        
        System.out.printf("\nInventory: %d/%d items | Weight: %d/%d\n", 
            inventory.size(), getMaxInventorySize(), getCurrentWeight(), getMaxWeight());
    }
    
    /**
     * Get rarity indicator for items
     */
    private String getRarityIndicator(Item item) {
        if (item.getName().contains("Legendary")) return "🟣";
        if (item.getName().contains("Epic")) return "🟠";
        if (item.getName().contains("Rare")) return "🔵";
        if (item.getName().contains("Uncommon")) return "🟢";
        return "⚪";
    }
    
    /**
     * Categorize inventory items for better organization
     */
    private Map<String, List<Item>> categorizeInventory() {
        Map<String, List<Item>> categories = new LinkedHashMap<>();
        categories.put("⚔️ Weapons", new ArrayList<>());
        categories.put("🛡️ Armor", new ArrayList<>());
        categories.put("💍 Accessories", new ArrayList<>());
        categories.put("🧪 Consumables", new ArrayList<>());
        categories.put("🔨 Materials", new ArrayList<>());
        categories.put("📜 Quest Items", new ArrayList<>());
        categories.put("💎 Treasures", new ArrayList<>());
        categories.put("📚 Miscellaneous", new ArrayList<>());
        
        for (Item item : inventory) {
            if (item instanceof Weapon) {
                categories.get("⚔️ Weapons").add(item);
            } else if (item instanceof Armor) {
                categories.get("🛡️ Armor").add(item);
            } else if (item instanceof Accessory) {
                categories.get("💍 Accessories").add(item);
            } else if (item instanceof Consumable) {
                categories.get("🧪 Consumables").add(item);
            } else if (item.getType().equals("Material")) {
                categories.get("🔨 Materials").add(item);
            } else if (item.isQuestItem()) {
                categories.get("📜 Quest Items").add(item);
            } else if (item.getValue() > 500) {
                categories.get("💎 Treasures").add(item);
            } else {
                categories.get("📚 Miscellaneous").add(item);
            }
        }
        
        // Remove empty categories
        categories.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return categories;
    }
    
    /**
     * Enhanced item usage system
     */
    public void useItem() {
        List<Consumable> usableItems = inventory.stream()
            .filter(item -> item instanceof Consumable)
            .map(item -> (Consumable) item)
            .toList();
            
        if (usableItems.isEmpty()) {
            System.out.println("You have no usable items.");
            return;
        }
        
        System.out.println("\n=== USABLE ITEMS ===");
        for (int i = 0; i < usableItems.size(); i++) {
            Consumable item = usableItems.get(i);
            System.out.printf("%d. %s - %s\n", i + 1, item.getName(), item.getDescription());
        }
        
        System.out.print("Use which item? (number or 'back'): ");
        String input = scanner.nextLine();
        
        if (!input.equals("back")) {
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < usableItems.size()) {
                    Consumable item = usableItems.get(choice);
                    item.use(this);
                    inventory.remove(item);
                    
                    // Skill experience for using items
                    if (item instanceof HealthPotion || item instanceof ManaPotion) {
                        gainSkillExperience("Alchemy", 3);
                    }
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
    
    /**
     * Enhanced equipment display
     */
    public void showEquipment() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                       EQUIPMENT                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        System.out.printf("⚔️ Weapon: %s\n", 
            equippedWeapon != null ? equippedWeapon.getName() + " (Power: " + equippedWeapon.getPower() + ")" : "None");
        System.out.printf("🛡️ Armor: %s\n", 
            equippedArmor != null ? equippedArmor.getName() + " (Defense: " + equippedArmor.getDefense() + ")" : "None");
        
        if (!equippedAccessories.isEmpty()) {
            System.out.println("💍 Accessories:");
            for (Accessory accessory : equippedAccessories) {
                System.out.printf("  • %s\n", accessory.getName());
            }
        } else {
            System.out.println("💍 Accessories: None");
        }
        
        System.out.println("\n📊 Combat Statistics:");
        System.out.printf("  Attack Power: %d\n", calculateAttackPower());
        System.out.printf("  Defense: %d\n", calculateDefense());
        System.out.printf("  Combat Rating: %d\n", combatRating);
    }
    
    /**
     * Enhanced weapon equipping with comparison
     */
    public void equipWeapon() {
        List<Weapon> weapons = inventory.stream()
            .filter(item -> item instanceof Weapon)
            .map(item -> (Weapon) item)
            .sorted((w1, w2) -> Integer.compare(w2.getPower(), w1.getPower()))
            .toList();
            
        if (weapons.isEmpty()) {
            System.out.println("You have no weapons to equip.");
            return;
        }
        
        System.out.println("\n=== AVAILABLE WEAPONS ===");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon weapon = weapons.get(i);
            String comparison = "";
            if (equippedWeapon != null) {
                int diff = weapon.getPower() - equippedWeapon.getPower();
                comparison = diff > 0 ? " (+" + diff + ")" : diff < 0 ? " (" + diff + ")" : " (=)";
            }
            System.out.printf("%d. %s (Power: %d)%s\n", i + 1, weapon.getName(), weapon.getPower(), comparison);
        }
        
        System.out.print("Equip which weapon? (number or 'back'): ");
        String input = scanner.nextLine();
        
        if (!input.equals("back")) {
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < weapons.size()) {
                    equippedWeapon = weapons.get(choice);
                    System.out.printf("Equipped %s! New attack power: %d\n", 
                        equippedWeapon.getName(), calculateAttackPower());
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
    
    /**
     * Enhanced armor equipping with comparison
     */
    public void equipArmor() {
        List<Armor> armors = inventory.stream()
            .filter(item -> item instanceof Armor)
            .map(item -> (Armor) item)
            .sorted((a1, a2) -> Integer.compare(a2.getDefense(), a1.getDefense()))
            .toList();
            
        if (armors.isEmpty()) {
            System.out.println("You have no armor to equip.");
            return;
        }
        
        System.out.println("\n=== AVAILABLE ARMOR ===");
        for (int i = 0; i < armors.size(); i++) {
            Armor armor = armors.get(i);
            String comparison = "";
            if (equippedArmor != null) {
                int diff = armor.getDefense() - equippedArmor.getDefense();
                comparison = diff > 0 ? " (+" + diff + ")" : diff < 0 ? " (" + diff + ")" : " (=)";
            }
            System.out.printf("%d. %s (Defense: %d)%s\n", i + 1, armor.getName(), armor.getDefense(), comparison);
        }
        
        System.out.print("Equip which armor? (number or 'back'): ");
        String input = scanner.nextLine();
        
        if (!input.equals("back")) {
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < armors.size()) {
                    equippedArmor = armors.get(choice);
                    System.out.printf("Equipped %s! New defense: %d\n", 
                        equippedArmor.getName(), calculateDefense());
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
    
    /**
     * Show all active quests with detailed information
     */
    public void showQuests() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      ACTIVE QUESTS                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        if (activeQuests.isEmpty()) {
            System.out.println("📜 You have no active quests.");
            System.out.println("💡 Visit NPCs or explore to find new quests!");
            return;
        }
        
        for (int i = 0; i < activeQuests.size(); i++) {
            Quest quest = activeQuests.get(i);
            System.out.printf("\n┌─ Quest %d: %s ─┐\n", i + 1, quest.getName());
            System.out.printf("│ %s\n", quest.getDescription());
            System.out.printf("│ Progress: %s\n", quest.getProgressString());
            System.out.printf("│ Reward: %s\n", quest.getRewardDescription());
            System.out.printf("│ Difficulty: %s\n", quest.getDifficulty());
            System.out.println("└" + "─".repeat(quest.getName().length() + 12) + "┘");
        }
        
        System.out.printf("\nCompleted Quests: %d | Success Rate: %.1f%%\n", 
            questsCompleted, questsCompleted > 0 ? (questsCompleted * 100.0 / (questsCompleted + activeQuests.size())) : 0.0);
    }
    
    /**
     * Show comprehensive player statistics
     */
    public void showDetailedStats() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   CHARACTER STATISTICS                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        System.out.printf("👤 Name: %s\n", name);
        System.out.printf("⭐ Level: %d (XP: %d/%d)\n", level, exp, level * 100);
        System.out.printf("🏆 Prestige: %d\n", prestige);
        System.out.println();
        
        System.out.println("📊 Core Statistics:");
        System.out.printf("  ❤️  HP: %d/%d\n", hp, maxHp);
        System.out.printf("  🔵 Mana: %d/%d\n", mana, maxMana);
        System.out.printf("  💚 Stamina: %d/%d\n", stamina, maxStamina);
        System.out.printf("  💰 Gold: %d (Total Earned: %d)\n", gold, totalGoldEarned);
        System.out.println();
        
        System.out.println("⚔️ Combat Statistics:");
        System.out.printf("  🗡️  Attack Power: %d\n", calculateAttackPower());
        System.out.printf("  🛡️  Defense: %d\n", calculateDefense());
        System.out.printf("  🎯 Combat Rating: %d\n", combatRating);
        System.out.printf("  💀 Enemies Defeated: %d\n", enemiesDefeated);
        System.out.println();
        
        System.out.println("🔮 Elemental Resistances:");
        elementalResistances.forEach((element, resistance) -> 
            System.out.printf("  %s: %d%%\n", 
                element.substring(0, 1).toUpperCase() + element.substring(1), resistance));
        System.out.println();
        
        System.out.println("🎓 Skills:");
        skills.forEach((skill, level) -> {
            int exp = skillExperience.get(skill);
            int expNeeded = level * 100;
            System.out.printf("  %s: Level %d (XP: %d/%d)\n", skill, level, exp, expNeeded);
        });
        System.out.println();
        
        if (faction != null) {
            System.out.printf("🏛️ Faction: %s (Reputation: %d)\n", faction.getName(), reputation);
        }
        
        System.out.printf("👥 Companions: %d/%d\n", companions.size(), maxCompanions);
        System.out.printf("📜 Active Quests: %d (Completed: %d)\n", activeQuests.size(), questsCompleted);
        System.out.printf("🏅 Achievements: %d unlocked\n", unlockedAchievements.size());
        
        if (mythicMode) {
            System.out.println("✨ MYTHIC MODE: ACTIVE");
        }
    }
    
    // Weight and inventory management
    private int getCurrentWeight() {
        return inventory.stream().mapToInt(Item::getWeight).sum();
    }
    
    private int getMaxWeight() {
        return 100 + (level * 5) + (skills.get("Survival") * 10);
    }
    
    // Enhanced getter methods
    public boolean isAlive() { return hp > 0; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getGold() { return gold; }
    public int getPrestige() { return prestige; }
    public String getLocation() { return location; }
    public String getCurrentDomain() { return currentDomain; }
    public Horse getHorse() { return horse; }
    public Faction getFaction() { return faction; }
    public List<Companion> getCompanions() { return companions; }
    public Map<String, Integer> getSkills() { return skills; }
    public List<String> getKnownSpells() { return knownSpells; }
    public int getMaxInventorySize() { return 50 + (level * 2) + (prestige * 5); }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
    public int getReputation() { return reputation; }
    public int getTotalGoldEarned() { return totalGoldEarned; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getQuestsCompleted() { return questsCompleted; }
    public Set<Achievement> getUnlockedAchievements() { return unlockedAchievements; }
    public boolean isMythicMode() { return mythicMode; }
    public List<Item> getInventory() { return inventory; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    
    // Enhanced setter methods
    public void setLocation(String location) { this.location = location; }
    public void setCurrentDomain(String domain) { this.currentDomain = domain; }
    public void setHorse(Horse horse) { this.horse = horse; }
    public void setFaction(Faction faction) { this.faction = faction; }
    public void addGold(int amount) { 
        this.gold += amount; 
        this.totalGoldEarned += amount;
    }
    public void subtractGold(int amount) { this.gold = Math.max(0, this.gold - amount); }
    public void addToInventory(Item item) { 
        if (getCurrentWeight() + item.getWeight() <= getMaxWeight()) {
            this.inventory.add(item); 
        } else {
            System.out.println("Item too heavy! Cannot carry more.");
        }
    }
    public void removeFromInventory(Item item) { this.inventory.remove(item); }
    public void incrementEnemiesDefeated() { this.enemiesDefeated++; }
    public void incrementQuestsCompleted() { this.questsCompleted++; }
    public void addQuest(Quest quest) { this.activeQuests.add(quest); }
    public void removeQuest(Quest quest) { this.activeQuests.remove(quest); }
    public void addAchievement(Achievement achievement) { this.unlockedAchievements.add(achievement); }
    public void addStatusEffect(String name, StatusEffect effect) { this.statusEffects.put(name, effect); }
    public void removeStatusEffect(String name) { this.statusEffects.remove(name); }
}