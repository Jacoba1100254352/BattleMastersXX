import Player.Companion;
import Player.Player;

public final class CompanionSystem {
	private CompanionSystem() {}

	public static void showCompanionMenu(Player player) {
		if (player.getCompanions().isEmpty()) {
			System.out.println("You currently have no companions.");
			return;
		}
		System.out.println("Your companions:");
		for (Companion companion : player.getCompanions()) {
			System.out.println(" - " + companion.getName());
		}
	}
}
