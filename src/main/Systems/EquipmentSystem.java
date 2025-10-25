package Systems;

import Items.Armor;
import Items.Item;
import Items.Weapon;
import Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class EquipmentSystem {
	private static final Scanner SCANNER = new Scanner(System.in);

	private EquipmentSystem() {
	}

	public static void showEquipmentMenu(Player player) {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Equipment Manager ===");
			System.out.println("Weapon: " + (player.getEquippedWeapon() != null ? player.getEquippedWeapon().getName() : "None"));
			System.out.println("Armor : " + (player.getEquippedArmor() != null ? player.getEquippedArmor().getName() : "None"));
			System.out.println("1) View details\n2) Equip weapon\n3) Equip armor\n4) Unequip weapon\n5) Unequip armor\n6) Back");
			System.out.print("Choose an option: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> showEquippedDetails(player);
				case "2" -> equipWeaponFlow(player);
				case "3" -> equipArmorFlow(player);
				case "4" -> player.unequipWeapon();
				case "5" -> player.unequipArmor();
				case "6", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	private static void showEquippedDetails(Player player) {
		Weapon weapon = player.getEquippedWeapon();
		if (weapon != null) {
			System.out.println("Weapon: " + weapon.getName() + " | Power: " + weapon.getPower() + " | Element: " + weapon.getElement());
		}
		Armor armor = player.getEquippedArmor();
		if (armor != null) {
			System.out.println("Armor : " + armor.getName() + " | Defense: " + armor.getDefense() + " | Type: " + armor.getArmorType());
		}
		if (weapon == null && armor == null) {
			System.out.println("You are travelling light.");
		}
	}

	private static void equipWeaponFlow(Player player) {
		List<Weapon> weapons = new ArrayList<>();
		for (Item item : player.getInventory()) {
			if (item instanceof Weapon weapon) {
				weapons.add(weapon);
			}
		}
		if (weapons.isEmpty()) {
			System.out.println("You don't carry any weapons.");
			return;
		}
		System.out.println("Available weapons:");
		for (int i = 0; i < weapons.size(); i++) {
			Weapon weapon = weapons.get(i);
			System.out.printf("%d) %s (Power %d, Element %s)%n", i + 1, weapon.getName(), weapon.getPower(), weapon.getElement());
		}
		System.out.print("Equip which weapon? (number): ");
		try {
			int index = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
			if (index >= 0 && index < weapons.size()) {
				player.equipWeapon(weapons.get(index));
			} else {
				System.out.println("Invalid selection.");
			}
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number.");
		}
	}

	private static void equipArmorFlow(Player player) {
		List<Armor> armors = new ArrayList<>();
		for (Item item : player.getInventory()) {
			if (item instanceof Armor armor) {
				armors.add(armor);
			}
		}
		if (armors.isEmpty()) {
			System.out.println("You don't carry any armor.");
			return;
		}
		System.out.println("Available armor sets:");
		for (int i = 0; i < armors.size(); i++) {
			Armor armor = armors.get(i);
			System.out.printf("%d) %s (Defense %d, Type %s)%n", i + 1, armor.getName(), armor.getDefense(), armor.getArmorType());
		}
		System.out.print("Equip which armor? (number): ");
		try {
			int index = Integer.parseInt(SCANNER.nextLine().trim()) - 1;
			if (index >= 0 && index < armors.size()) {
				player.equipArmor(armors.get(index));
			} else {
				System.out.println("Invalid selection.");
			}
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number.");
		}
	}
}
