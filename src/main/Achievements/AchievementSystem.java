package Achievements;

import Enemy.Enemy;
import Player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple in-memory achievement tracking used by the command line game loop.
 */
public class AchievementSystem {
	private static boolean initialized = false;
	private static final Map<String, String> ACHIEVEMENT_DESCRIPTIONS = new HashMap<>();
	private static final Set<String> DOMAIN_ACHIEVEMENTS = new HashSet<>();
	
	private AchievementSystem() {
	}
	
	public static void initialize() {
		if (initialized) return;
		initialized = true;
		
		ACHIEVEMENT_DESCRIPTIONS.put("FIRST_VICTORY", "Win your first battle.");
		ACHIEVEMENT_DESCRIPTIONS.put("BOSS_SLAYER", "Defeat any boss enemy.");
		ACHIEVEMENT_DESCRIPTIONS.put("MASTER_OF_ARCANA", "Know at least 5 spells.");
		ACHIEVEMENT_DESCRIPTIONS.put("WEALTHY_ADVENTURER", "Earn a total of 5,000 gold.");
		ACHIEVEMENT_DESCRIPTIONS.put("MYTHIC_ASCENT", "Unlock mythic mode through prestige.");
	}
	
	public static void checkCombatAchievements(Player player, Enemy enemy) {
		if (!initialized) initialize();
		
		player.incrementEnemiesDefeated();
		if (!player.getUnlockedAchievements().contains("FIRST_VICTORY")) {
			unlockAchievement(player, "FIRST_VICTORY");
		}
		
		if (enemy != null && enemy.isBoss()) {
			unlockAchievement(player, "BOSS_SLAYER");
		}
		
		if (player.getKnownSpells().size() >= 5) {
			unlockAchievement(player, "MASTER_OF_ARCANA");
		}
		
		if (player.getTotalGoldEarned() >= 5000) {
			unlockAchievement(player, "WEALTHY_ADVENTURER");
		}
		
		if (player.isMythicMode()) {
			unlockAchievement(player, "MYTHIC_ASCENT");
		}
	}
	
	public static void checkDomainAchievement(Player player, String domainName) {
		if (!initialized) initialize();
		String key = "DOMAIN_PIONEER_" + domainName.replace(" ", "_").toUpperCase();
		if (DOMAIN_ACHIEVEMENTS.add(player.getName() + "_" + key)) {
			ACHIEVEMENT_DESCRIPTIONS.putIfAbsent(key, "Discover the domain: " + domainName);
			unlockAchievement(player, key);
		}
	}
	
	public static void showAchievements(Player player) {
		if (!initialized) initialize();
		Set<String> unlocked = player.getUnlockedAchievements();
		if (unlocked.isEmpty()) {
			System.out.println("No achievements unlocked yet.");
			return;
		}
		System.out.println("=== Achievements Unlocked ===");
		for (String achievement : unlocked) {
			String description = ACHIEVEMENT_DESCRIPTIONS.getOrDefault(achievement, "Special accomplishment.");
			System.out.println("• " + achievement + " - " + description);
		}
	}
	
	public static void unlockAchievement(Player player, String achievementId) {
		if (!initialized) initialize();
		if (player.getUnlockedAchievements().contains(achievementId)) return;
		player.addAchievement(achievementId);
		String description = ACHIEVEMENT_DESCRIPTIONS.getOrDefault(achievementId, "Special accomplishment.");
		System.out.println("*** ACHIEVEMENT UNLOCKED: " + achievementId + " ***");
		System.out.println(description);
	}
}
