// ============== Enemy.java ==============

import java.util.*;

public class EnemyData {
    public static class EnemyTemplate {
        private String name;
        private int hp;
        private int attack;
        private int exp;
        private int gold;
        private boolean isBoss;
        private double weight;
        private List<String> resist;
        private List<String> weak;
        
        public EnemyTemplate(String name, int hp, int attack, int exp, int gold, 
                           boolean isBoss, double weight) {
            this.name = name;
            this.hp = hp;
            this.attack = attack;
            this.exp = exp;
            this.gold = gold;
            this.isBoss = isBoss;
            this.weight = weight;
            this.resist = new ArrayList<>();
            this.weak = new ArrayList<>();
        }
        
        public EnemyTemplate(String name, int hp, int attack, int exp, int gold, 
                           boolean isBoss, double weight, List<String> resist, List<String> weak) {
            this(name, hp, attack, exp, gold, isBoss, weight);
            if (resist != null) this.resist = new ArrayList<>(resist);
            if (weak != null) this.weak = new ArrayList<>(weak);
        }
        
        public Enemy createEnemy() {
            return new Enemy(name, hp, attack, exp, gold, isBoss, resist, weak);
        }
        
        // Getters
        public String getName() { return name; }
        public int getHp() { return hp; }
        public int getAttack() { return attack; }
        public int getExp() { return exp; }
        public int getGold() { return gold; }
        public boolean isBoss() { return isBoss; }
        public double getWeight() { return weight; }
        public List<String> getResist() { return resist; }
        public List<String> getWeak() { return weak; }
    }
    
    public static final List<EnemyTemplate> ENEMY_TYPES = Arrays.asList(
        new EnemyTemplate("Fire Elemental", 150, 45, 400, 150, false, 5, 
            Arrays.asList("fire"), Arrays.asList("ice")),
        new EnemyTemplate("Ice Giant", 180, 50, 500, 180, false, 4, 
            Arrays.asList("ice"), Arrays.asList("fire")),
        new EnemyTemplate("Undead Warrior", 90, 35, 250, 100, false, 8, 
            Arrays.asList("physical"), Arrays.asList("fire", "holy")),
        new EnemyTemplate("Shadow Beast", 120, 40, 350, 120, false, 6, 
            Arrays.asList("dark"), Arrays.asList("light")),
        new EnemyTemplate("Goblin", 30, 10, 50, 20, false, 40),
        new EnemyTemplate("Orc", 50, 15, 100, 40, false, 15),
        new EnemyTemplate("Troll", 80, 20, 150, 70, false, 10),
        new EnemyTemplate("Skeleton", 40, 12, 60, 30, false, 15),
        new EnemyTemplate("Bandit", 35, 14, 70, 25, false, 15),
        new EnemyTemplate("Serpent", 100, 30, 300, 60, false, 30),
        new EnemyTemplate("Robber", 50, 15, 100, 40, false, 15),
        new EnemyTemplate("Dark Elf", 70, 25, 200, 80, false, 12),
        
        // Bosses
        new EnemyTemplate("Dragon", 200, 40, 1000, 500, true, 5),
        new EnemyTemplate("Dark Knight", 150, 35, 800, 400, true, 5),
        new EnemyTemplate("Goblin lord", 350, 50, 1100, 650, true, 3),
        new EnemyTemplate("Medieval army", 500, 80, 1500, 840, true, 2),
        new EnemyTemplate("Blood knight", 2000, 100, 2000, 2000, true, 0.5),
        new EnemyTemplate("High mage", 5000, 300, 10000, 5000, true, 0.1),
        new EnemyTemplate("Paladin", 5000, 550, 15000, 10000, true, 0.05),
        new EnemyTemplate("Ancient Dragon", 8000, 600, 20000, 15000, true, 0.05),
        new EnemyTemplate("Death Incarnate", 15000, 2000, 50000, 50000, true, 0.01),
        
        // Domain Bosses
        new EnemyTemplate("Central Guardian", 1500, 120, 3000, 1500, true, 0.1, 
            Arrays.asList("physical"), Arrays.asList("magic")),
        new EnemyTemplate("Forest Titan", 2500, 180, 5000, 2500, true, 0.08, 
            Arrays.asList("nature", "earth"), Arrays.asList("fire")),
        new EnemyTemplate("Samurai", 500, 150, 5000, 3500, true, 0.1),
        new EnemyTemplate("Void Lord", 10000, 700, 25000, 20000, true, 0.02),
        new EnemyTemplate("Grove Dragon", 20000, 2350, 100000, 70000, true, 0.00000000000000001),
        new EnemyTemplate("Mountain King", 3000, 200, 6000, 3000, true, 0.07,
            Arrays.asList("earth", "physical"), Arrays.asList("air")),
        new EnemyTemplate("Sand Pharaoh", 2800, 190, 5500, 2800, true, 0.07,
            Arrays.asList("sand", "wind"), Arrays.asList("water")),
        new EnemyTemplate("Swamp Lord", 2200, 160, 4500, 2200, true, 0.08,
            Arrays.asList("poison", "water"), Arrays.asList("fire", "light")),
        new EnemyTemplate("Frost Emperor", 3500, 220, 7000, 3500, true, 0.06,
            Arrays.asList("ice", "water"), Arrays.asList("fire")),
        new EnemyTemplate("Magma King", 4000, 250, 8000, 4000, true, 0.05,
            Arrays.asList("fire", "earth"), Arrays.asList("ice", "water")),
        new EnemyTemplate("Sea Emperor", 3200, 210, 6500, 3200, true, 0.06,
            Arrays.asList("water", "ice"), Arrays.asList("lightning")),
        new EnemyTemplate("Storm Lord", 2900, 200, 6000, 2900, true, 0.07,
            Arrays.asList("air", "lightning"), Arrays.asList("earth")),
        new EnemyTemplate("Earth Titan", 4500, 280, 9000, 4500, true, 0.04,
            Arrays.asList("earth", "physical"), Arrays.asList("water")),
        new EnemyTemplate("Shadow Master", 3300, 230, 7500, 3300, true, 0.05,
            Arrays.asList("dark", "void"), Arrays.asList("light", "holy")),
        new EnemyTemplate("Light Seraph", 3800, 240, 8500, 3800, true, 0.04,
            Arrays.asList("light", "holy"), Arrays.asList("dark")),
        new EnemyTemplate("Chrono Master", 5000, 300, 12000, 5000, true, 0.03,
            Arrays.asList("time", "temporal"), Arrays.asList("chaos")),
        new EnemyTemplate("Void Emperor", 5500, 320, 14000, 5500, true, 0.02,
            Arrays.asList("void", "cosmic"), Arrays.asList("creation")),
        new EnemyTemplate("Dream Weaver", 2600, 170, 5200, 2600, true, 0.07,
            Arrays.asList("illusion", "psychic"), Arrays.asList("reality")),
        new EnemyTemplate("Ancient Guardian", 4200, 260, 9500, 4200, true, 0.04,
            Arrays.asList("ancient", "time"), Arrays.asList("modern")),
        new EnemyTemplate("Beast King", 3600, 240, 8000, 3600, true, 0.05,
            Arrays.asList("nature", "physical"), Arrays.asList("magic")),
        new EnemyTemplate("Elemental Avatar", 4800, 290, 11000, 4800, true, 0.03,
            Arrays.asList("fire", "water", "earth", "air"), Arrays.asList("void")),
        new EnemyTemplate("Chaos Lord", 6000, 350, 15000, 6000, true, 0.02,
            Arrays.asList("chaos", "random"), Arrays.asList("order")),
        new EnemyTemplate("Order Master", 5800, 340, 14500, 5800, true, 0.02,
            Arrays.asList("order", "law"), Arrays.asList("chaos")),
        new EnemyTemplate("Spirit King", 3400, 220, 7800, 3400, true, 0.05,
            Arrays.asList("spirit", "ethereal"), Arrays.asList("physical")),
        
        // Mythic Realm Bosses
        new EnemyTemplate("Eternal Emperor", 25000, 1000, 50000, 25000, true, 0.001,
            Arrays.asList("time", "death"), Arrays.asList("entropy")),
        new EnemyTemplate("Cosmic Entity", 40000, 1500, 80000, 40000, true, 0.0005,
            Arrays.asList("cosmic", "reality"), Arrays.asList("void")),
        new EnemyTemplate("God Emperor", 60000, 2000, 120000, 60000, true, 0.0002,
            Arrays.asList("divine", "holy"), Arrays.asList("mortal")),
        new EnemyTemplate("Devil Emperor", 55000, 1800, 110000, 55000, true, 0.0003,
            Arrays.asList("infernal", "evil"), Arrays.asList("divine")),
        new EnemyTemplate("Void Lord", 35000, 1200, 70000, 35000, true, 0.0008,
            Arrays.asList("void", "nothing"), Arrays.asList("existence")),
        new EnemyTemplate("Creator God", 100000, 3000, 200000, 100000, true, 0.0001,
            Arrays.asList("creation", "all"), Arrays.asList("destruction")),
        new EnemyTemplate("Destroyer", 90000, 2800, 180000, 90000, true, 0.0001,
            Arrays.asList("destruction", "end"), Arrays.asList("creation")),
        new EnemyTemplate("Gate Master", 75000, 2200, 150000, 75000, true, 0.0001,
            Arrays.asList("portal", "reality"), Arrays.asList("closure"))
            
    );
    
    public static EnemyTemplate getWeightedRandomEnemy() {
        Random random = new Random();
        double totalWeight = ENEMY_TYPES.stream().mapToDouble(EnemyTemplate::getWeight).sum();
        double r = random.nextDouble() * totalWeight;
        double upto = 0;
        
        for (EnemyTemplate template : ENEMY_TYPES) {
            upto += template.getWeight();
            if (upto >= r) {
                return template;
            }
        }
        return ENEMY_TYPES.get(0);
    }
}