package Systems;

import Enemy.Enemy;
import Player.Companion;
import Player.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public final class CompanionSystem {
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final Map<String, CompanionTemplate> TEMPLATES = new LinkedHashMap<>();

	static {
		registerTemplate(new CompanionTemplate("Luna", 1.2, CompanionRole.HEALER));
		registerTemplate(new CompanionTemplate("Garruk", 0.9, CompanionRole.BERSERKER));
		registerTemplate(new CompanionTemplate("Nyx", 1.5, CompanionRole.ROGUE));
	}

	private CompanionSystem() {
	}

	private static void registerTemplate(CompanionTemplate template) {
		TEMPLATES.put(template.name, template);
	}

	public static void showCompanionMenu(Player player) {
		boolean running = true;
		while (running) {
			System.out.println("\n=== Companions ===");
			if (player.getCompanions().isEmpty()) {
				System.out.println("You currently travel alone.");
			} else {
				player.getCompanions().forEach(companion ->
					System.out.println("- " + companion.getName() + " (Speed " + companion.getSpeed() + ")"));
			}
			System.out.println("1) Recruit companion\n2) Dismiss companion\n3) Back");
			System.out.print("Choose: ");
			String choice = SCANNER.nextLine().trim();
			switch (choice) {
				case "1" -> recruitCompanion(player);
				case "2" -> dismissCompanion(player);
				case "3", "back" -> running = false;
				default -> System.out.println("Unknown option.");
			}
		}
	}

	private static void recruitCompanion(Player player) {
		System.out.println("Available companions:");
		int index = 1;
		for (CompanionTemplate template : TEMPLATES.values()) {
			if (player.getCompanionByName(template.name) == null) {
				System.out.printf("%d) %s - %s%n", index++, template.name, template.role.description);
			}
		}
		if (index == 1) {
			System.out.println("No new companions available.");
			return;
		}
		System.out.print("Recruit which companion? (name): ");
		String name = SCANNER.nextLine().trim();
		CompanionTemplate template = TEMPLATES.get(name);
		if (template == null) {
			System.out.println("No companion called " + name + ".");
			return;
		}
		if (player.getCompanionByName(name) != null) {
			System.out.println(name + " already journeys with you.");
			return;
		}
		player.addCompanion(template.instantiate());
	}

	private static void dismissCompanion(Player player) {
		if (player.getCompanions().isEmpty()) {
			System.out.println("You have nobody to dismiss.");
			return;
		}
		System.out.print("Enter the name of the companion to dismiss: ");
		String name = SCANNER.nextLine().trim();
		player.removeCompanion(name);
	}

	public static Companion createCompanionByName(String name) {
		CompanionTemplate template = TEMPLATES.get(name);
		return template != null ? template.instantiate() : null;
	}

	private record CompanionTemplate(String name, double speed, CompanionRole role) {
		Companion instantiate() {
			return new Companion(name, speed) {
				@Override
				protected void special(Player player, Enemy enemy) {
					role.applySpecial(this, player, enemy);
				}
			};
		}
	}

	private enum CompanionRole {
		HEALER("Applies heals each special turn") {
			@Override
			void applySpecial(Companion companion, Player player, Enemy enemy) {
				player.heal(20 + player.getLevel());
				System.out.println(companion.getName() + " channels restorative light!");
			}
		},
		BERSERKER("Deals heavy damage when using specials") {
			@Override
			void applySpecial(Companion companion, Player player, Enemy enemy) {
				if (enemy != null) {
					enemy.takeDamage(30 + player.getLevel() * 2, "physical");
					System.out.println(companion.getName() + " unleashes a devastating strike!");
				}
			}
		},
		ROGUE("Distracts enemies, lowering their guard") {
			@Override
			void applySpecial(Companion companion, Player player, Enemy enemy) {
				if (enemy != null) {
					enemy.addStatusEffect("vulnerable", new Effects.StatusEffect("Vulnerable", 2, 0, false) {
						@Override
						public void apply(Object target) {
							System.out.println(enemy.getName() + " is off balance!");
						}
					});
				}
			}
		};

		private final String description;

		CompanionRole(String description) {
			this.description = description;
		}

		abstract void applySpecial(Companion companion, Player player, Enemy enemy);
	}
}
