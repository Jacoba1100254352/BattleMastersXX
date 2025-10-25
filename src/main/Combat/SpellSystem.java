package Combat;// ================================================================================
// COMPLETE COMBAT SYSTEM - Should be in Combat.CombatSystem.java
// Advanced turn-based combat with multiple mechanics
// ================================================================================

import Effects.StatusEffect;
import Enemy.Enemy;

import java.util.*;

// ================================================================================
// SPELL SYSTEM - Should be in Combat.SpellSystem.java
// Complete magic system with multiple schools
// ================================================================================

/**
 * Complete spell system with multiple magic schools
 */
public class SpellSystem {
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
                        
                        // Combat.Spell power status effect bonus
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
