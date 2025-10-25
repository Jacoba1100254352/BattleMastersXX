import Player.Player;

public final class SkillSystem {
	private SkillSystem() {}

	public static void showSkillMenu(Player player) {
		System.out.println("=== Skills ===");
		player.getSkills().forEach((skill, value) ->
			System.out.printf("%s: %d%n", skill, value));
	}
}
