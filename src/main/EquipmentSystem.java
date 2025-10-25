import Player.Player;

public final class EquipmentSystem {
	private EquipmentSystem() {}

	public static void showEquipmentMenu(Player player) {
		System.out.println("Equipment management is under construction. Current weapon: " +
			(player.getEquippedWeapon() != null ? player.getEquippedWeapon().getName() : "None") +
			", armor: " + (player.getEquippedArmor() != null ? player.getEquippedArmor().getName() : "None"));
	}
}
