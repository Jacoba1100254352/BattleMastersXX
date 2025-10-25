package Prestige;

import Player.Player;

import java.util.Scanner;

/**
 * Handles the prestige flow. The CLI version keeps things lightweight:
 * we simply confirm with the player and call through to Player#doPrestige.
 */
public class PrestigeSystem {
	private static final Scanner SCANNER = new Scanner(System.in);
	private static boolean initialized = false;
	
	private PrestigeSystem() {
	}
	
	public static void initialize() {
		initialized = true;
	}
	
	public static void showPrestigeMenu(Player player) {
		if (!initialized) initialize();
		
		System.out.println("\n=== Prestige Hall ===");
		if (!player.canPrestige()) {
			System.out.println("Reach level 50 to prestige and earn powerful permanent bonuses.");
			return;
		}
		
		System.out.println("Prestiging will reset your level to 1 but grant permanent stat boosts.");
		System.out.print("Prestige now? (yes/no): ");
		String choice = SCANNER.nextLine().trim().toLowerCase();
		if ("yes".equals(choice) || "y".equals(choice)) {
			player.doPrestige();
		} else {
			System.out.println("You decide to wait before prestiging.");
		}
	}
}
