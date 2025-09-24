// ================================================================================
// COMPLETE COMBAT SYSTEM - Should be in CombatSystem.java
// Advanced turn-based combat with multiple mechanics
// ================================================================================

import java.util.*;

/**
 * Complete combat system with advanced mechanics
 */
class CombatSystem {
    private static Scanner scanner = new Scanner(System.in);
    
    /**
     * Initiate battle between player and enemy
     */
    public boolean initiateBattle(Player player, Enemy enemy) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    BATTLE BEGINS!");
        System.out.printf("            %s vs %s\n", player.getName(), enemy.getName());
        System.out.println("=".repeat(60));
        
        boolean playerWon = false;
        int turnCount = 0;
        
        while (player.isAlive() && enemy.isAlive() && turnCount < 50) {
            turnCount++;
            
            // Player turn
            if (player.isAlive()) {
                displayBattleStatus(player, enemy, turnCount);
                
                if (!handlePlayerTurn(player, enemy)) {
                    // Player fled
                    return false;
                }
            }
            
            // Enemy turn
            if (enemy.isAlive() && player.isAlive()) {
                handleEnemyTurn(player, enemy);
            }
            
            // Process status effects
            processStatusEffects(player, enemy);
            
            // Check win conditions
            if (!enemy.isAlive()) {
                playerWon = true;
                break;
            }
            if (!player.isAlive()) {
                break;
            }
        }
        
        // Battle conclusion
        if (turnCount >= 50) {
            System.out.println("\nThe battle drags on too long and both fighters retreat!");
            return false;
        } else if (playerWon) {
            handleVictory(player, enemy);
            return true;
        } else {
            handleDefeat(player);
            return false;
        }
    }
    
    /**
     * Display current battle status
     */
    private void displayBattleStatus(Player player, Enemy enemy, int turn) {
        System.out.println("\n┌─ Turn " + turn + " ─────────────────────────────────────┐");
        System.out.printf("│ %s: %d/%d HP, %d/%d MP, %d/%d SP    │\n", 
            player.getName(), player.getHp(), player.getMaxHp(), 
            player.getMana(), player.getMaxMana(),
            player.getStamina(), player.getMaxStamina());
        System.out.printf("│ %s: %d/%d HP                        │\n", 
            enemy.getName(), enemy.getHp(), enemy.getMaxHp());
        
        // Show status effects
        if (!player.getStatusEffects().isEmpty()) {
            System.out.printf("│ Your effects: %s │\n", 
                String.join(", ", player.getStatusEffects().keySet()));
        }
        if (!enemy.getStatusEffects().isEmpty()) {
            System.out.printf("│ Enemy effects: %s │\n", 
                String.join(", ", enemy.getStatusEffects().keySet()));
        }
        
        System.out.println("└────────────────────────────────────────────────┘");
    }
    
    /**
     * Handle player's turn in combat
     */
    private boolean handlePlayerTurn(Player player, Enemy enemy) {
        // Check for paralysis/stun
        if (player.getStatusEffects().containsKey("stunned")) {
            System.out.println("You are stunned and cannot act this turn!");
            return true;
        }
        
        System.out.println("\nChoose your action:");
        System.out.println("1. Attack        2. Cast Spell    3. Use Item");
        System.out.println("4. Defend        5. Special       6. Flee");
        
        if (!player.getCompanions().isEmpty()) {
            System.out.println("7. Companion Command");
        }
        
        System.out.print("Action: ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1", "attack" -> handlePlayerAttack(player, enemy);
            case "2", "spell", "magic" -> handlePlayerSpell(player, enemy);
            case "3", "item" -> handlePlayerItem(player);
            case "4", "defend" -> handlePlayerDefend(player);
            case "5", "special" -> handlePlayerSpecial(player, enemy);
            case "6", "flee" -> {
                if (attemptFlee(player, enemy)) {
                    System.out.println("You successfully flee from battle!");
                    return false;
                } else {
                    System.out.println("You couldn't escape!");
                }
            }
            case "7", "companion" -> {
                if (!player.getCompanions().isEmpty()) {
                    handleCompanionCommand(player, enemy);
                } else {
                    System.out.println("Invalid action.");
                }
            }
            default -> {
                System.out.println("Invalid action. You lose your turn!");
            }
        }
        
        return true;
    }
    
    /**
     * Handle player attack
     */
    private void handlePlayerAttack(Player player, Enemy enemy) {
        if (player.getStamina() < 5) {
            System.out.println("You're too tired to attack effectively!");
            return;
        }
        
        player.consumeStamina(5);
        
        int damage = player.calculateAttackPower();
        
        // Critical hit check
        double critChance = 0.1; // Base 10%
        if (player.getEquippedWeapon() != null) {
            critChance = player.getEquippedWeapon().getCriticalChance();
        }
        
        boolean isCritical = ThreadLocalRandom.current().nextDouble() < critChance;
        if (isCritical) {
            damage = (int)(damage * 1.5);
            System.out.println("CRITICAL HIT!");
        }
        
        // Apply weapon element
        String element = "physical";
        if (player.getEquippedWeapon() != null) {
            element = player.getEquippedWeapon().getElement();
        }
        
        // Status effect applications
        if (player.getStatusEffects().containsKey("enraged")) {
            damage = (int)(damage * 1.3);
            System.out.println("Rage empowers your attack!");
        }
        
        enemy.takeDamage(damage, element);
        
        // Weapon durability
        if (player.getEquippedWeapon() != null) {
            player.getEquippedWeapon().use();
        }
        
        // Skill experience
        player.gainSkillExperience("Combat", 3);
        
        // Weapon special effects
        handleWeaponSpecialEffects(player, enemy, damage);
    }
    
    /**
     * Handle weapon special effects
     */
    private void handleWeaponSpecialEffects(Player player, Enemy enemy, int damage) {
        if (player.getEquippedWeapon() == null) return;
        
        List<String> effects = player.getEquippedWeapon().getSpecialEffects();
        
        for (String effect : effects) {
            switch (effect) {
                case "Vampiric" -> {
                    int healing = damage / 4;
                    player.heal(healing);
                    System.out.println("Your weapon drains life force! (+" + healing + " HP)");
                }
                case "Poisonous" -> {
                    if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                        enemy.addStatusEffect("poison", new PoisonEffect(3, 15));
                        System.out.println("Your weapon poisons the enemy!");
                    }
                }
                case "Burning" -> {
                    if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                        enemy.addStatusEffect("burning", new StatusEffect("Burning", 4, 12, false) {
                            @Override
                            public void apply(Object target) {
                                if (target instanceof Enemy e) {
                                    e.takeDamage(strength, "fire");
                                    System.out.println("Flames burn the enemy!");
                                }
                            }
                        });
                    }
                }
            }
        }
    }
    
    /**
     * Handle player spellcasting
     */
    private void handlePlayerSpell(Player player, Enemy enemy) {
        SpellSystem.showCombatSpells(player);
        System.out.print("Cast which spell? (name or 'cancel'): ");
        String spellName = scanner.nextLine();
        
        if (!spellName.equals("cancel")) {
            SpellSystem.castCombatSpell(player, enemy, spellName);
        }
    }
    
    /**
     * Handle player item usage
     */
    private void handlePlayerItem(Player player) {
        List<Consumable> usableItems = player.getInventory().stream()
            .filter(item -> item instanceof Consumable)
            .map(item -> (Consumable) item)
            .toList();
            
        if (usableItems.isEmpty()) {
            System.out.println("No usable items in combat!");
            return;
        }
        
        System.out.println("Combat Items:");
        for (int i = 0; i < Math.min(5, usableItems.size()); i++) {
            System.out.printf("%d. %s\n", i + 1, usableItems.get(i).getName());
        }
        
        System.out.print("Use which item? (number or 'cancel'): ");
        String choice = scanner.nextLine();
        
        if (!choice.equals("cancel")) {
            try {
                int itemIndex = Integer.parseInt(choice) - 1;
                if (itemIndex >= 0 && itemIndex < usableItems.size()) {
                    Consumable item = usableItems.get(itemIndex);
                    item.use(player);
                    player.removeFromInventory(item);
                    System.out.println("Used " + item.getName() + "!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice!");
            }
        }
    }
    
    /**
     * Handle player defend action
     */
    private void handlePlayerDefend(Player player) {
        System.out.println("You raise your guard!");
        player.addStatusEffect("defending", new StatusEffect("Defending", 1, 50, true) {
            @Override
            public void apply(Object target) {
                // Passive defense bonus applied in damage calculation
            }
        });
        
        // Restore some stamina while defending
        player.restoreStamina(10);
    }
    
    /**
     * Handle player special abilities
     */
    private void handlePlayerSpecial(Player player, Enemy enemy) {
        System.out.println("Special Abilities:");
        
        List<String> abilities = new ArrayList<>();
        
        // Combat skill abilities
        if (player.getSkills().get("Combat") >= 10) {
            abilities.add("Berserker Rage");
        }
        if (player.getSkills().get("Combat") >= 15) {
            abilities.add("Perfect Strike");
        }
        
        // Faction abilities
        if (player.getFaction() != null) {
            abilities.addAll(player.getFaction().getCombatAbilities());
        }
        
        if (abilities.isEmpty()) {
            System.out.println("No special abilities available!");
            return;
        }
        
        for (int i = 0; i < abilities.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, abilities.get(i));
        }
        
        System.out.print("Use which ability? (number or 'cancel'): ");
        String choice = scanner.nextLine();
        
        if (!choice.equals("cancel")) {
            try {
                int abilityIndex = Integer.parseInt(choice) - 1;
                if (abilityIndex >= 0 && abilityIndex < abilities.size()) {
                    useSpecialAbility(player, enemy, abilities.get(abilityIndex));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice!");
            }
        }
    }
    
    /**
     * Use a special ability
     */
    private void useSpecialAbility(Player player, Enemy enemy, String ability) {
        switch (ability) {
            case "Berserker Rage" -> {
                if (player.getMana() >= 30) {
                    player.restoreMana(-30);
                    player.addStatusEffect("berserker", new StatusEffect("Berserker", 5, 50, true) {
                        @Override
                        public void apply(Object target) {
                            // Passive damage bonus
                        }
                    });
                    System.out.println("You enter a berserker rage! (+50% damage for 5 turns)");
                } else {
                    System.out.println("Not enough mana!");
                }
            }
            case "Perfect Strike" -> {
                if (player.getStamina() >= 20) {
                    player.consumeStamina(20);
                    int damage = player.calculateAttackPower() * 2;
                    enemy.takeDamage(damage, "physical");
                    System.out.println("Perfect Strike hits for " + damage + " damage!");
                } else {
                    System.out.println("Not enough stamina!");
                }
            }
        }
    }
    
    /**
     * Handle companion commands
     */
    private void handleCompanionCommand(Player player, Enemy enemy) {
        System.out.println("Companion Commands:");
        System.out.println("1. Attack    2. Heal    3. Defend    4. Special");
        
        System.out.print("Command: ");
        String command = scanner.nextLine();
        
        for (Companion companion : player.getCompanions()) {
            companion.executeCommand(command, player, enemy);
        }
    }
    
    /**
     * Attempt to flee from battle
     */
    private boolean attemptFlee(Player player, Enemy enemy) {
        if (enemy.isBoss()) {
            System.out.println("You cannot flee from a boss battle!");
            return false;
        }
        
        // Flee chance based on agility and stamina
        double fleeChance = 0.6 + (player.getStamina() / (double)player.getMaxStamina()) * 0.3;
        
        if (player.getStatusEffects().containsKey("paralyzed")) {
            fleeChance = 0.1;
        }
        
        return ThreadLocalRandom.current().nextDouble() < fleeChance;
    }
    
    /**
     * Handle enemy turn
     */
    private void handleEnemyTurn(Player player, Enemy enemy) {
        // Process enemy status effects first
        enemy.processStatusEffects();
        
        if (!enemy.isAlive()) return;
        
        // Check for stun/paralysis
        if (enemy.getStatusEffects().containsKey("stunned") || 
            enemy.getStatusEffects().containsKey("frozen")) {
            System.out.println(enemy.getName() + " is unable to act this turn!");
            return;
        }
        
        // Enemy AI decides action
        EnemyAction action = enemy.decideAction(player);
        
        // Execute action
        executeEnemyAction(player, enemy, action);
        
        // Companion interrupts (chance)
        for (Companion companion : player.getCompanions()) {
            if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                companion.interruptEnemyAction(player, enemy);
            }
        }
    }
    
    /**
     * Execute enemy action
     */
    private void executeEnemyAction(Player player, Enemy enemy, EnemyAction action) {
        System.out.println("\n" + action.getMessage());
        
        switch (action.getActionType()) {
            case "attack", "berserk_attack" -> {
                int damage = action.getDamage();
                
                // Check if player is defending
                if (player.getStatusEffects().containsKey("defending")) {
                    damage = (int)(damage * 0.5);
                    System.out.println("Your defense reduces the damage!");
                }
                
                player.takeDamage(damage, action.getElement());
            }
            case "fire_blast", "ice_storm", "lightning", "poison" -> {
                player.takeDamage(action.getDamage(), action.getElement());
                
                // Apply status effect if specified
                if (action.getStatusEffect() != null) {
                    applyEnemyStatusEffect(player, action.getStatusEffect());
                }
            }
            case "heal" -> {
                // Enemy heals (already handled in decideAction)
            }
            case "buff" -> {
                // Enemy buffs (already handled in decideAction)
            }
        }
    }
    
    /**
     * Apply status effect from enemy action
     */
    private void applyEnemyStatusEffect(Player player, String effectName) {
        switch (effectName) {
            case "poison" -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.4) {
                    player.addStatusEffect("poison", new PoisonEffect(4, 10));
                    System.out.println("You have been poisoned!");
                }
            }
            case "freeze" -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                    player.addStatusEffect("frozen", new StatusEffect("Frozen", 1, 0, false) {
                        @Override
                        public void apply(Object target) {
                            System.out.println("You are frozen solid!");
                        }
                    });
                }
            }
            case "stun" -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                    player.addStatusEffect("stunned", new StatusEffect("Stunned", 1, 0, false) {
                        @Override
                        public void apply(Object target) {
                            System.out.println("You are stunned!");
                        }
                    });
                }
            }
        }
    }
    
    /**
     * Process status effects for both combatants
     */
    private void processStatusEffects(Player player, Enemy enemy) {
        // Process player status effects
        List<String> expiredPlayerEffects = new ArrayList<>();
        for (Map.Entry<String, StatusEffect> entry : player.getStatusEffects().entrySet()) {
            StatusEffect effect = entry.getValue();
            effect.apply(player);
            effect.decrementDuration();
            
            if (effect.isExpired()) {
                expiredPlayerEffects.add(entry.getKey());
            }
        }
        
        for (String effectName : expiredPlayerEffects) {
            player.removeStatusEffect(effectName);
            System.out.println("You recover from " + effectName + ".");
        }
        
        // Enemy status effects are processed in enemy.processStatusEffects()
    }
    
    /**
     * Handle battle victory
     */
    private void handleVictory(Player player, Enemy enemy) {
        System.out.println("\n" + "★".repeat(50));
        System.out.println("                    VICTORY!");
        System.out.println("★".repeat(50));
        
        // Experience and gold rewards
        int expGained = enemy.getExpReward();
        int goldGained = enemy.getGoldReward();
        
        // Bonus for boss fights
        if (enemy.isBoss()) {
            expGained = (int)(expGained * 1.5);
            goldGained = (int)(goldGained * 1.5);
            System.out.println("BOSS DEFEATED! Bonus rewards granted!");
        }
        
        // Faction bonuses
        if (player.getFaction() != null) {
            expGained += player.getFaction().getExpBonus();
            goldGained += player.getFaction().getGoldBonus();
        }
        
        player.gainExperience(expGained);
        player.addGold(goldGained);
        
        System.out.printf("Gained %d experience and %d gold!\n", expGained, goldGained);
        
        // Loot drops
        List<Item> loot = enemy.getLoot();
        if (!loot.isEmpty()) {
            System.out.println("\nLoot found:");
            for (Item item : loot) {
                player.addToInventory(item);
                System.out.println("• " + item.getName());
            }
        }
        
        // Restore some health and mana after victory
        player.heal(player.getMaxHp() / 10);
        player.restoreMana(player.getMaxMana() / 10);
        
        // Check achievements
        AchievementSystem.checkCombatAchievements(player, enemy);
    }
    
    /**
     * Handle battle defeat
     */
    private void handleDefeat(Player player) {
        System.out.println("\n" + "☠".repeat(50));
        System.out.println("                   DEFEAT...");
        System.out.println("☠".repeat(50));
        
        // Player death is handled in Player.takeDamage()
        // This method can add additional defeat consequences
        
        // Lose some gold (but not all)
        int goldLost = player.getGold() / 10;
        player.subtractGold(goldLost);
        
        if (goldLost > 0) {
            System.out.println("You lost " + goldLost + " gold in the defeat.");
        }
    }
}

// ================================================================================
// SPELL SYSTEM - Should be in SpellSystem.java
// Complete magic system with multiple schools
// ================================================================================

/**
 * Complete spell system with multiple magic schools
 */
class SpellSystem {
    private static Map<String, Spell> allSpells;
    private static Map<String, List<String>> spellSchools;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void initialize() {
        allSpells = new HashMap<>();
        spellSchools = new HashMap<>();
        
        initializeSpells();
        initializeSchools();
    }
    
    /**
     * Initialize all available spells
     */
    private static void initializeSpells() {
        // Healing School
        allSpells.put("heal", new Spell("Heal", "Restoration", 15, 0, 
            "Restores health to the caster", List.of("healing:30")));
        allSpells.put("greater_heal", new Spell("Greater Heal", "Restoration", 35, 0,
            "Powerful healing spell", List.of("healing:80")));
        allSpells.put("mass_heal", new Spell("Mass Heal", "Restoration", 60, 0,
            "Heals entire party", List.of("party_healing:50")));
        allSpells.put("resurrection", new Spell("Resurrection", "Restoration", 100, 10,
            "Brings back fallen allies", List.of("revive:full")));
        
        // Destruction School
        allSpells.put("fireball", new Spell("Fireball", "Destruction", 20, 0,
            "Hurls a ball of fire", List.of("damage:35:fire")));
        allSpells.put("ice_shard", new Spell("Ice Shard", "Destruction", 15, 0,
            "Sharp projectile of ice", List.of("damage:25:ice", "chance_freeze:0.2")));
        allSpells.put("lightning_bolt", new Spell("Lightning Bolt", "Destruction", 25, 0,
            "Strikes with electric fury", List.of("damage:40:lightning", "chance_stun:0.3")));
        allSpells.put("meteor", new Spell("Meteor", "Destruction", 80, 0,
            "Calls down a meteor", List.of("damage:120:fire", "area_damage:true")));
        
        // Alteration School
        allSpells.put("shield", new Spell("Shield", "Alteration", 25, 0,
            "Creates magical protection", List.of("defense_buff:30:10")));
        allSpells.put("haste", new Spell("Haste", "Alteration", 30, 0,
            "Increases speed and agility", List.of("speed_buff:50:8")));
        allSpells.put("slow", new Spell("Slow", "Alteration", 20, 0,
            "Reduces enemy speed", List.of("enemy_slow:40:6")));
        allSpells.put("transmute", new Spell("Transmute", "Alteration", 40, 5,
            "Changes materials", List.of("material_change:random")));
        
        // Illusion School
        allSpells.put("invisibility", new Spell("Invisibility", "Illusion", 35, 0,
            "Becomes invisible", List.of("stealth:100:5")));
        allSpells.put("confusion", new Spell("Confusion", "Illusion", 25, 0,
            "Confuses enemy actions", List.of("enemy_confusion:3")));
        allSpells.put("fear", new Spell("Fear", "Illusion", 30, 0,
            "Causes terror in enemies", List.of("enemy_fear:4")));
        
        // Conjuration School
        allSpells.put("summon_ally", new Spell("Summon Ally", "Conjuration", 50, 0,
            "Summons a temporary ally", List.of("summon:elemental:5")));
        allSpells.put("create_food", new Spell("Create Food", "Conjuration", 10, 0,
            "Conjures nourishment", List.of("create_item:food")));
        allSpells.put("teleport", new Spell("Teleport", "Conjuration", 60, 0,
            "Instantly travel to known location", List.of("teleport:known_location")));
        
        // Divine School
        allSpells.put("divine_intervention", new Spell("Divine Intervention", "Divine", 150, 25,
            "Calls upon divine aid", List.of("full_heal", "status_clear", "divine_blessing:10")));
        allSpells.put("holy_light", new Spell("Holy Light", "Divine", 40, 0,
            "Sears undead with holy power", List.of("damage:60:holy", "undead_bonus:2.0")));
        allSpells.put("bless", new Spell("Bless", "Divine", 20, 0,
            "Grants divine blessing", List.of("all_stats_buff:15:12")));
        
        // Void School
        allSpells.put("void_drain", new Spell("Void Drain", "Void", 45, 0,
            "Drains life and mana", List.of("damage:35:void", "mana_drain:20", "life_steal:0.5")));
        allSpells.put("reality_tear", new Spell("Reality Tear", "Void", 100, 15,
            "Tears through reality itself", List.of("damage:150:void", "ignore_defense:true")));
        allSpells.put("banish", new Spell("Banish", "Void", 80, 10,
            "Removes enemy from reality", List.of("banish:5", "boss_immune:true")));
    }
    
    /**
     * Initialize spell schools
     */
    private static void initializeSchools() {
        spellSchools.put("Restoration", List.of("heal", "greater_heal", "mass_heal", "resurrection"));
        spellSchools.put("Destruction", List.of("fireball", "ice_shard", "lightning_bolt", "meteor"));
        spellSchools.put("Alteration", List.of("shield", "haste", "slow", "transmute"));
        spellSchools.put("Illusion", List.of("invisibility", "confusion", "fear"));
        spellSchools.put("Conjuration", List.of("summon_ally", "create_food", "teleport"));
        spellSchools.put("Divine", List.of("divine_intervention", "holy_light", "bless"));
        spellSchools.put("Void", List.of("void_drain", "reality_tear", "banish"));
    }
    
    /**
     * Show spell menu for player
     */
    public static void showSpellMenu(Player player) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        SPELL BOOK                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        Map<String, List<String>> knownBySchool = categorizeKnownSpells(player);
        
        if (knownBySchool.isEmpty()) {
            System.out.println("You know no spells yet. Learn spells by leveling up or finding spell books!");
            return;
        }
        
        for (Map.Entry<String, List<String>> entry : knownBySchool.entrySet()) {
            System.out.println("\n🔮 " + entry.getKey() + " School:");
            for (String spellName : entry.getValue()) {
                Spell spell = allSpells.get(spellName);
                if (spell != null) {
                    System.out.printf("  • %s (Cost: %d MP) - %s\n", 
                        spell.getName(), spell.getManaCost(), spell.getDescription());
                }
            }
        }
        
        System.out.printf("\nCurrent Mana: %d/%d\n", player.getMana(), player.getMaxMana());
        System.out.print("\nCast a spell? (spell name or 'back'): ");
        String spellName = scanner.nextLine().toLowerCase().replace(" ", "_");
        
        if (!spellName.equals("back")) {
            castSpell(player, spellName);
        }
    }
    
    /**
     * Categorize known spells by school
     */
    private static Map<String, List<String>> categorizeKnownSpells(Player player) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        for (String school : spellSchools.keySet()) {
            List<String> knownInSchool = spellSchools.get(school).stream()
                .filter(spell -> player.getKnownSpells().contains(spell))
                .toList();
            
            if (!knownInSchool.isEmpty()) {
                result.put(school, knownInSchool);
            }
        }
        
        return result;
    }
    
    /**
     * Cast a spell outside combat
     */
    public static void castSpell(Player player, String spellName) {
        if (!player.getKnownSpells().contains(spellName)) {
            System.out.println("You don't know that spell!");
            return;
        }
        
        Spell spell = allSpells.get(spellName);
        if (spell == null) {
            System.out.println("Unknown spell!");
            return;
        }
        
        // Check mana cost
        if (player.getMana() < spell.getManaCost()) {
            System.out.println("Not enough mana! Need " + spell.getManaCost() + " mana.");
            return;
        }
        
        // Check skill level requirement
        if (player.getLevel() < spell.getLevelRequirement()) {
            System.out.println("You need to be level " + spell.getLevelRequirement() + " to cast this spell!");
            return;
        }
        
        // Cast the spell
        player.restoreMana(-spell.getManaCost());
        executeSpellEffects(player, null, spell);
        
        // Gain magic skill experience
        player.gainSkillExperience("Magic", spell.getManaCost() / 5 + 2);
        
        System.out.println("You cast " + spell.getName() + "!");
    }
    
    /**
     * Show combat spells only
     */
    public static void showCombatSpells(Player player) {
        List<String> combatSpells = player.getKnownSpells().stream()
            .filter(spellName -> {
                Spell spell = allSpells.get(spellName);
                return spell != null && spell.isCombatSpell();
            })
            .toList();
            
        if (combatSpells.isEmpty()) {
            System.out.println("No combat spells available!");
            return;
        }
        
        System.out.println("Combat Spells:");
        for (int i = 0; i < combatSpells.size(); i++) {
            Spell spell = allSpells.get(combatSpells.get(i));
            System.out.printf("%d. %s (Cost: %d MP) - %s\n", 
                i + 1, spell.getName(), spell.getManaCost(), spell.getDescription());
        }
    }
    
    /**
     * Cast spell in combat
     */
    public static void castCombatSpell(Player player, Enemy enemy, String spellName) {
        spellName = spellName.toLowerCase().replace(" ", "_");
        
        if (!player.getKnownSpells().contains(spellName)) {
            System.out.println("You don't know that spell!");
            return;
        }
        
        Spell spell = allSpells.get(spellName);
        if (spell == null) {
            System.out.println("Unknown spell!");
            return;
        }
        
        if (player.getMana() < spell.getManaCost()) {
            System.out.println("Not enough mana!");
            return;
        }
        
        // Cast the spell
        player.restoreMana(-spell.getManaCost());
        executeSpellEffects(player, enemy, spell);
        
        // Gain experience
        player.gainSkillExperience("Magic", spell.getManaCost() / 3 + 3);
        
        System.out.println("You cast " + spell.getName() + "!");
    }
    
    /**
     * Execute spell effects
     */
    private static void executeSpellEffects(Player player, Enemy enemy, Spell spell) {
        for (String effect : spell.getEffects()) {
            String[] parts = effect.split(":");
            String effectType = parts[0];
            
            switch (effectType) {
                case "healing" -> {
                    int amount = Integer.parseInt(parts[1]);
                    player.heal(amount);
                }
                case "damage" -> {
                    if (enemy != null) {
                        int damage = Integer.parseInt(parts[1]);
                        String element = parts.length > 2 ? parts[2] : "magical";
                        
                        // Magic skill bonus
                        int magicBonus = player.getSkills().get("Magic") * 2;
                        damage += magicBonus;
                        
                        // Spell power status effect bonus
                        if (player.getStatusEffects().containsKey("spell_power")) {
                            damage = (int)(damage * 1.25);
                        }
                        
                        enemy.takeDamage(damage, element);
                        System.out.println("The spell deals " + damage + " " + element + " damage!");
                    }
                }
                case "defense_buff" -> {
                    int amount = Integer.parseInt(parts[1]);
                    int duration = Integer.parseInt(parts[2]);
                    player.addStatusEffect("shield", new StatusEffect("Magical Shield", duration, amount, true) {
                        @Override
                        public void apply(Object target) {
                            // Passive defense bonus
                        }
                    });
                    System.out.println("Magical shield activated! (+" + amount + " defense for " + duration + " turns)");
                }
                case "chance_freeze" -> {
                    if (enemy != null && ThreadLocalRandom.current().nextDouble() < Double.parseDouble(parts[1])) {
                        enemy.addStatusEffect("frozen", new StatusEffect("Frozen", 2, 0, false) {
                            @Override
                            public void apply(Object target) {
                                System.out.println(((Enemy)target).getName() + " is frozen!");
                            }
                        });
                    }
                }
                case "full_heal" -> {
                    player.heal(player.getMaxHp());
                    player.restoreMana(player.getMaxMana());
                    player.restoreStamina(player.getMaxStamina());
                    System.out.println("Divine power fully restores you!");
                }
                case "teleport" -> {
                    System.out.println("You feel magical energy surrounding you...");
                    System.out.println("Teleportation will be available after combat.");
                }
                // Add more effect types as needed
            }
        }
    }
    
    /**
     * Learn a new spell
     */
    public static void learnSpell(Player player, String spellName) {
        if (player.getKnownSpells().contains(spellName)) {
            System.out.println("You already know " + spellName + "!");
            return;
        }
        
        Spell spell = allSpells.get(spellName);
        if (spell == null) {
            System.out.println("Unknown spell: " + spellName);
            return;
        }
        
        player.getKnownSpells().add(spellName);
        System.out.println("*** SPELL LEARNED: " + spell.getName() + " ***");
        System.out.println(spell.getDescription());
    }
}

/**
 * Spell class representing individual spells
 */
class Spell {
    private String name;
    private String school;
    private int manaCost;
    private int levelRequirement;
    private String description;
    private List<String> effects;
    
    public Spell(String name, String school, int manaCost, int levelRequirement, 
                 String description, List<String> effects) {
        this.name = name;
        this.school = school;
        this.manaCost = manaCost;
        this.levelRequirement = levelRequirement;
        this.description = description;
        this.effects = new ArrayList<>(effects);
    }
    
    /**
     * Check if this is a combat spell
     */
    public boolean isCombatSpell() {
        return effects.stream().anyMatch(effect -> 
            effect.startsWith("damage") || 
            effect.startsWith("healing") ||
            effect.startsWith("defense_buff") ||
            effect.contains("chance_"));
    }
    
    // Getters
    public String getName() { return name; }
    public String getSchool() { return school; }
    public int getManaCost() { return manaCost; }
    public int getLevelRequirement() { return levelRequirement; }
    public String getDescription() { return description; }
    public List<String> getEffects() { return effects; }
}