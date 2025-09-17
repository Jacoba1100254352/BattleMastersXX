// ============== Companion.java ==============

public class Companion {
    private String name;
    private String role;
    private int hp = 50;
    private int attack;
    private int heal;
    
    public Companion(String name, String role) {
        this.name = name;
        this.role = role;
        if ("warrior".equals(role)) {
            this.attack = 15;
            this.heal = 0;
        } else {
            this.attack = 5;
            this.heal = 35;
        }
    }
    
    public void act(Player player, Enemy enemy) {
        if ("healer".equals(role) && player.getHp() < player.getMaxHp()) {
            player.setHp(Math.min(player.getMaxHp(), player.getHp() + heal));
            System.out.println(name + " heals you for " + heal + " HP!");
        } else if ("warrior".equals(role)) {
            enemy.takeDamage(attack);
            System.out.println(name + " attacks " + enemy.getName() + " for " + attack + " damage!");
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getHp() { return hp; }
}