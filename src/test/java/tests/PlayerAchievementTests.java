package tests;

import Achievements.AchievementSystem;
import Enemy.Enemy;
import Items.ItemFactory;
import Player.Player;

public final class PlayerAchievementTests {
	public static void main(String[] args) {
		ItemFactory.initialize();
		AchievementSystem.initialize();
		testLevelUpAwardsSkillPoints();
		testAchievementUnlocksOnVictory();
		testSnapshotRestore();
		System.out.println("All player and achievement tests passed.");
	}

	private static void testLevelUpAwardsSkillPoints() {
		Player player = new Player("Tester");
		player.gainExperience(120);
		assertTrue(player.getLevel() >= 2, "Player should level up to at least 2");
		assertTrue(player.getSkillPoints() >= 1, "Level up should award skill points");
	}

	private static void testAchievementUnlocksOnVictory() {
		Player player = new Player("Hero");
		Enemy enemy = new Enemy("Training Dummy", 50, 5, 10, 5);
		AchievementSystem.checkCombatAchievements(player, enemy);
		assertTrue(player.getUnlockedAchievements().contains("FIRST_VICTORY"),
			"First victory achievement should be unlocked");
	}

	private static void testSnapshotRestore() {
		Player original = new Player("Archivist");
		original.addGold(500);
		original.addToInventory(ItemFactory.createHealthPotion("Test Potion", 60));
		Player.Snapshot snapshot = original.createSnapshot();
		Player restored = new Player("Restored");
		restored.restoreFromSnapshot(snapshot);
		assertTrue(restored.getGold() >= 500, "Restored player should keep gold");
		assertTrue(!restored.getInventory().isEmpty(), "Restored player should have inventory items");
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
