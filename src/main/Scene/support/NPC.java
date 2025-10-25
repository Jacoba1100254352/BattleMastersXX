package Scene.support;

import Player.Player;

public class NPC {
	private final String name;
	private final String location;

	public NPC(String name, String location) {
		this.name = name;
		this.location = location;
	}

	public String getName() { return name; }
	public String getLocation() { return location; }

	public void interact(Player player) {
		System.out.println(name + ": Safe travels, " + player.getName() + "!");
	}
}
