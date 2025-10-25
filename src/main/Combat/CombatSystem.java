package Combat;

import Achievements.AchievementSystem;
import Effects.PoisonEffect;
import Effects.StatusEffect;
import Enemy.Enemy;
import Enemy.EnemyAction;
import Items.Consumable;
import Items.Item;
import Player.Companion;
import Player.Player;
import Systems.SettingsSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Complete combat system with advanced mechanics
 */
public class CombatSystem {
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
			
			// Enemy.Enemy turn
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
		
		if (SettingsSystem.isDetailedCombatLog()) {
			if (!player.getStatusEffects().isEmpty()) {
				System.out.printf("│ Your effects: %s │\n",
					          String.join(", ", player.getStatusEffects().keySet()));
			}
			if (!enemy.getStatusEffects().isEmpty()) {
				System.out.printf("│ Enemy effects: %s │\n",
					          String.join(", ", enemy.getStatusEffects().keySet()));
			}
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
		System.out.println("1. Attack        2. Cast Combat.Spell    3. Use Items.Item");
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
		
		// Items.Weapon durability
		if (player.getEquippedWeapon() != null) {
			player.getEquippedWeapon().use();
		}
		
		// Skill experience
		player.gainSkillExperience("Combat", 3);
		
		// Items.Weapon special effects
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
		
		// Enemy.Enemy AI decides action
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
				// Enemy.Enemy heals (already handled in decideAction)
			}
			case "buff" -> {
				// Enemy.Enemy buffs (already handled in decideAction)
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
		
		// Enemy.Enemy status effects are processed in enemy.processStatusEffects()
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
