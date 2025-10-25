package Scene.support;

import Player.Companion;
import Player.Player;
import Systems.CompanionSystem;
import Systems.TreasureSystem;

public class NPC {
	private final String name;
	private final String location;
	private final Role role;

	public NPC(String name, String location, Role role) {
		this.name = name;
		this.location = location;
		this.role = role;
	}

	public String getName() { return name; }
	public String getLocation() { return location; }
	public Role getRole() { return role; }

	public void interact(Player player) {
		role.interact(this, player);
	}

	public enum Role {
		QUEST_GIVER {
			@Override
			void interact(NPC npc, Player player) {
				System.out.println(npc.name + ": I have a mission for you, adventurer!");
				player.startQuest();
			}
		},
		HEALER {
			@Override
			void interact(NPC npc, Player player) {
				System.out.println(npc.name + " restores your vitality.");
				player.heal(player.getMaxHp());
				player.restoreMana(player.getMaxMana());
			}
		},
		TRAINER {
			@Override
			void interact(NPC npc, Player player) {
				System.out.println(npc.name + ": I can teach you a few tricks.");
				player.addSkillPoints(1);
			}
		},
		SCOUT {
			@Override
			void interact(NPC npc, Player player) {
				System.out.println(npc.name + " shares valuable supplies with you.");
				TreasureSystem.generateRandomTreasure(player.getLevel()).forEach(player::addToInventory);
			}
		},
		COMPANION_HANDLER {
			@Override
			void interact(NPC npc, Player player) {
				System.out.println(npc.name + ": Let me introduce you to a potential ally.");
				if (player.getCompanions().size() >= 3) {
					System.out.println("Your party is already full.");
					return;
				}
				if (player.getCompanionByName("Luna") == null) {
					Companion companion = CompanionSystem.createCompanionByName("Luna");
					if (companion != null) {
						player.addCompanion(companion);
					}
				} else {
					System.out.println("She is already travelling with you.");
				}
			}
		};

		abstract void interact(NPC npc, Player player);
	}
}
