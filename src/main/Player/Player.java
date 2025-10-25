package Player;


import Items.Item;

import java.util.*;


public class Player {
    Map<String, Object> currentQuest;
    List<Map<String, Object>> npcQuests;
    String name;
    int hp;
    int maxHp;
    int mana;
    int maxMana;
	int stamina;
	int maxStamina;
    int gold;
    int exp;
    int level;
    int prestige;
	int reputation;
    Location location;
    Domain currentDomain;
    List<Map<String, Object>> inventory;
    Map<String, Integer> weapon;
    Map<String, Integer> armor;
    List<Companion> companions;
    final int MAX_COMPANIONS = 3;
    Horse horse;
    boolean mythicMode;
    Faction faction;
    List<StatusEffects> statusEffects;
    StartQuest startQuest;
    final List<Map<String, Object>> enemyTypes;
    final Random random = new Random();

    public Player(String _name) {
        currentQuest = null;
        npcQuests = new ArrayList<>();
        name = _name;
        hp = 100;
        maxHp = 100;
        mana = 50;
        maxMana = 50;
	    stamina = 100;
	    maxStamina = 100;
        gold = 0;
        exp = 0;
        level = 1;
        prestige = 0;
		reputation = 0;
        location = new Location("Town Square");
        currentDomain = new Domain("Central Domain");
        inventory = new ArrayList<>();
        weapon = null;
        armor = null;
        companions = new ArrayList<>();
        horse = null;
        mythicMode = false;
        faction = null;
        statusEffects = new ArrayList<>();
        startQuest = null;

        enemyTypes = List.of(
            Map.of("name", "Fire Elemental", "hp", 150, "attack", 45, "exp", 400, "gold", 150, "is_boss", false, "weight", 5, "resist", List.of("fire"), "weak", List.of("ice")),
            Map.of("name", "Ice Giant", "hp", 180, "attack", 50, "exp", 500, "gold", 180, "is_boss", false, "weight", 4, "resist", List.of("ice"), "weak", List.of("fire")),
            Map.of("name", "Undead Warrior", "hp", 90, "attack", 35, "exp", 250, "gold", 100, "is_boss", false, "weight", 8, "resist", List.of("physical"), "weak", List.of("fire", "holy")),
            Map.of("name", "Shadow Beast", "hp", 120, "attack", 40, "exp", 350, "gold", 120, "is_boss", false, "weight", 6, "resist", List.of("dark"), "weak", List.of("light")),
            Map.of("name", "Goblin", "hp", 30, "attack", 10, "exp", 50, "gold", 20, "is_boss", false, "weight", 40),
            Map.of("name", "Orc", "hp", 50, "attack", 15, "exp", 100, "gold", 40, "is_boss", false, "weight", 15),
            Map.of("name", "Troll", "hp", 80, "attack", 20, "exp", 150, "gold", 70, "is_boss", false, "weight", 10),
            Map.of("name", "Skeleton", "hp", 40, "attack", 12, "exp", 60, "gold", 30, "is_boss", false, "weight", 15),
            Map.of("name", "Bandit", "hp", 35, "attack", 14, "exp", 70, "gold", 25, "is_boss", false, "weight", 15),
            Map.of("name", "Serpent", "hp", 100, "attack", 30, "exp", 300, "gold", 60, "is_boss", false, "weight", 30),
            Map.of("name", "Robber", "hp", 50, "attack", 15, "exp", 100, "gold", 40, "is_boss", false, "weight", 15),
            Map.of("name", "Dark Elf", "hp", 70, "attack", 25, "exp", 200, "gold", 80, "is_boss", false, "weight", 12),
            Map.of("name", "Dragon", "hp", 200, "attack", 40, "exp", 1000, "gold", 500, "is_boss", true, "weight", 5),
            Map.of("name", "Dark Knight", "hp", 150, "attack", 35, "exp", 800, "gold", 400, "is_boss", true, "weight", 5),
            Map.of("name", "Goblin lord", "hp", 350, "attack", 50, "exp", 1100, "gold", 650, "is_boss", true, "weight", 3),
            Map.of("name", "Central Guardian", "hp", 1500, "attack", 120, "exp", 3000, "gold", 1500, "is_boss", true, "weight", 0.1),
            Map.of("name", "Forest Titan", "hp", 2500, "attack", 180, "exp", 5000, "gold", 2500, "is_boss", true, "weight", 0.08),
            Map.of("name", "Eternal Emperor", "hp", 25000, "attack", 1000, "exp", 50000, "gold", 25000, "is_boss", true, "weight", 0.001)
        );
    }

    public int attackValue() {
        int base = 10 + level * 2;
        int weaponPower = (weapon != null && weapon.containsKey("power")) ? weapon.get("power") : 0;
        int prestigeBonus = prestige * 10;
        return base + weaponPower + prestigeBonus;
    }

    public int defend(int dmg) {
        int defense = (armor != null && armor.containsKey("defense")) ? armor.get("defense") : 0;
        int prestigeDefense = prestige * 5;
        if (faction != null && "Knights of the Phoenix".equals(faction.getName())) {
            defense += 80;
        }
        int realDmg = Math.max(dmg - defense - prestigeDefense, 0);
        hp -= realDmg;
        System.out.println("You received " + realDmg + " damage (after armor, prestige, and faction bonuses).");
        return realDmg;
    }

    public void gainExp(int amount) {
        exp += amount;
        int needed = level * 100;
        while (exp >= needed) {
            level += 1;
            exp -= needed;
            maxHp += 20;
            hp = maxHp;
            maxMana += 10;
            mana = maxMana;
            System.out.println("*** You leveled up! You are now level " + level + "! ***");
            needed = level * 100;
        }
        if (level >= 50) {
            System.out.println("*** PRESTIGE AVAILABLE! You can now prestige at level 50! ***");
            System.out.println("Visit the Prestige Hall to prestige and gain exclusive rewards!");
        }
    }

    public boolean canPrestige() {
        return level >= 50;
    }

    public boolean doPrestige() {
        if (!canPrestige()) {
            System.out.println("You must reach level 50 to prestige!");
            return false;
        }
        prestige += 1;
        level = 1;
        exp = 0;
        maxHp = 100 + (prestige * 50);
        hp = maxHp;
        maxMana = 50 + (prestige * 25);
        mana = maxMana;
        System.out.println("*** PRESTIGE " + prestige + " ACHIEVED! ***");
        System.out.println("Level reset to 1, but you gained permanent bonuses!");
        System.out.println("New HP: " + maxHp + ", New Mana: " + maxMana);
        System.out.println("Attack bonus: " + (prestige * 10) + ", Defense bonus: " + (prestige * 5));
        if (prestige >= 10 && !mythicMode) {
            mythicMode = true;
            System.out.println("*** MYTHIC PRESTIGE UNLOCKED! ***");
            System.out.println("You now have access to the Mythic Gate and Mythic Challenges!");
        }
        return true;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void startQuest() {
        if (currentQuest != null) {
            System.out.println("You already have an active quest. Complete it before taking a new one.");
            return;
        }
        int effectiveLevel = level + (prestige * 20);
        List<Map<String, Object>> enemyPool = new ArrayList<>();
        for (Map<String, Object> e : enemyTypes) {
            boolean isBoss = (boolean) e.getOrDefault("is_boss", false);
            int enemyHp = ((Number) e.getOrDefault("hp", 0)).intValue();
            if (!isBoss && enemyHp <= effectiveLevel * 30 + 40) {
                enemyPool.add(e);
            }
        }
        if (enemyPool.isEmpty()) {
            for (Map<String, Object> e : enemyTypes) {
                if (!((boolean) e.getOrDefault("is_boss", false))) {
                    enemyPool.add(e);
                }
            }
        }
        if (enemyPool.isEmpty()) {
            System.out.println("No available enemies to start a quest.");
            return;
        }
        int randomIndex = random.nextInt(enemyPool.size());
        Map<String, Object> questEnemy = enemyPool.get(randomIndex);
        currentQuest = Map.of(
            "name", questEnemy.get("name"),
            "hp", questEnemy.get("hp"),
            "attack", questEnemy.get("attack"),
            "exp", questEnemy.get("exp"),
            "gold", questEnemy.get("gold")
        );
        System.out.println("A quest has started against: " + questEnemy.get("name"));
        System.out.print("A quest has been assigned: Defeat a " + questEnemy.get("name") + "!");
    }

    public boolean completeQuest(String defeatedEnemyName) {
        if (currentQuest == null || currentQuest.isEmpty()) {
            System.out.println("No active quest to complete.");
            return false;
        }
        Object nameObj = currentQuest.get("name");
        if (!(nameObj instanceof String questName)) {
            System.out.println("Invalid current quest data.");
            currentQuest = null;
            return false;
        }
	    if (!questName.equals(defeatedEnemyName)) {
            System.out.println("You have not defeated the required enemy: " + questName);
            return false;
        }
        System.out.println("*** Quests.Quest Completed: You defeated the " + questName + "! ***");
        int bonusExp = ((Number) currentQuest.getOrDefault("exp", 0)).intValue();
        int bonusGold = ((Number) currentQuest.getOrDefault("gold", 0)).intValue();
        Map<String, Object> bonusItem = randomRewardItem();
        gainExp(bonusExp);
        gold += bonusGold;
        inventory.add(bonusItem);
        System.out.println("You earned " + bonusExp + " EXP, " + bonusGold + " gold, and found a " + bonusItem.getOrDefault("name", "mysterious item") + "!");
        currentQuest = null;
        return true;
    }

    private Map<String, Object> randomRewardItem() {
        List<Map<String, Object>> pool = List.of(
            Map.of("name", "Small Health Potion", "type", "consumable", "heal", 50),
            Map.of("name", "Iron Sword", "type", "weapon", "power", 5),
            Map.of("name", "Leather Items.Armor", "type", "armor", "defense", 3),
            Map.of("name", "Mana Elixir", "type", "consumable", "mana", 30)
        );
        return pool.get(random.nextInt(pool.size()));
    }

    public boolean acceptNpcQuest(String npcName, Map<String, Object> questData) {
        for (Map<String, Object> q : npcQuests) {
            Object npc = q.get("npc");
            if (npcName.equals(npc)) {
                System.out.println("You already have a quest from " + npcName + "!");
                return false;
            }
        }
        Map<String, Object> newQuest = new HashMap<>();
        newQuest.put("npc", npcName);
        newQuest.put("type", questData.get("type"));
        newQuest.put("target", questData.getOrDefault("target", null));
        newQuest.put("quantity", ((Number) questData.getOrDefault("quantity", 1)).intValue());
        newQuest.put("progress", 0);
        newQuest.put("reward", questData.get("reward"));
        newQuest.put("description", questData.get("description"));
        npcQuests.add(newQuest);
        System.out.println("Quests.Quest accepted from " + npcName + ": " + questData.get("description"));
        return true;
    }

    public void updateNpcQuestProgress(String questType, String target) {
        List<Map<String, Object>> toComplete = new ArrayList<>();
        for (Map<String, Object> quest : npcQuests) {
            String type = (String) quest.get("type");
            Object qTarget = quest.get("target");
            int quantity = ((Number) quest.getOrDefault("quantity", 1)).intValue();
            int progress = ((Number) quest.getOrDefault("progress", 0)).intValue();
            if (!type.equals(questType)) continue;
            boolean match = "find_item".equals(type) && target != null && target.equals(qTarget);
	        if ("defeat_enemy".equals(type) && target != null && target.equals(qTarget)) match = true;
            if ("deliver_item".equals(type) && target != null && target.equals(qTarget)) match = true;
            if (match) {
                progress += 1;
                quest.put("progress", progress);
                if (progress >= quantity) toComplete.add(quest);
            }
        }
        for (Map<String, Object> q : toComplete) completeNpcQuest(q);
    }

    public void completeNpcQuest(Map<String, Object> quest) {
        System.out.println("*** NPC Quests.Quest Completed for " + quest.get("npc") + "! ***");
        Object rewardObj = quest.get("reward");
        if (rewardObj instanceof Map<?, ?> reward) {
	        if (reward.containsKey("gold")) {
                int g = ((Number) reward.get("gold")).intValue();
                gold += g;
                System.out.println("You received " + g + " gold!");
            }
            if (reward.containsKey("exp")) {
                int e = ((Number) reward.get("exp")).intValue();
                gainExp(e);
                System.out.println("You received " + e + " experience!");
            }
            if (reward.containsKey("item")) {
                Object item = reward.get("item");
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> itemMap = (Map<String, Object>) item;
                    inventory.add(itemMap);
                    System.out.println("You received " + itemMap.getOrDefault("name", "an item") + "!");
                }
            }
        }
        npcQuests.remove(quest);
    }

    public void checkNpcQuestItems() {
        for (Map<String, Object> quest : new ArrayList<>(npcQuests)) {
            String type = (String) quest.get("type");
            if (!"find_item".equals(type)) continue;
            String target = (String) quest.get("target");
            int quantity = ((Number) quest.getOrDefault("quantity", 1)).intValue();
            int itemCount = 0;
            for (Map<String, Object> item : inventory) {
                Object iname = item.get("name");
                if (iname instanceof String && ((String) iname).equals(target)) itemCount++;
            }
            int progress = ((Number) quest.getOrDefault("progress", 0)).intValue();
            if (itemCount >= quantity && progress < quantity) {
                quest.put("progress", quantity);
                completeNpcQuest(quest);
                break;
            }
        }
    }

    public boolean addCompanion(Companion companion) {
        if (companions.size() >= MAX_COMPANIONS) {
            System.out.println("You can only have " + MAX_COMPANIONS + " companions maximum!");
            return false;
        }
        companions.add(companion);
        System.out.println(companion.getName() + " has joined you.");
        return true;
    }
	
	public void addToInventory(Item item) {
	
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Domain getCurrentDomain() {
		return currentDomain;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getMaxHp() {
		return maxHp;
	}
	
	public int getMana() {
		return mana;
	}
	
	public int getMaxMana() {
		return maxMana;
	}
	
	public int getStamina() {
		return stamina;
	}
	
	public int getMaxStamina() {
		return maxStamina;
	}
	
	public int getGold() {
		return gold;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getPrestige() {
		return prestige;
	}
	
	public int getExp() {
		return exp;
	}
	
	public Horse getHorse() {
		return horse;
	}
	
	public Faction getFaction() {
		return faction;
	}
	
	public int getReputation() {
		return reputation;
	}
	
	public List<StatusEffects> getStatusEffects() {
		return statusEffects;
	}
}
