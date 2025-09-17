 // ============== NPC.java ==============

import java.util.*;

public class NPC {
    private String name;
    private List<String> dialogues;
    private Map<String, Object> reward;
    private Map<String, Object> quest;
    private Map<String, Object> riddle;
    private int dialogueIndex = 0;
    private boolean questGiven = false;
    private boolean riddleSolved = false;
    
    public NPC(String name, List<String> dialogues) {
        this.name = name;
        this.dialogues = dialogues;
    }
    
    public NPC(String name, List<String> dialogues, Map<String, Object> reward, 
              Map<String, Object> quest, Map<String, Object> riddle) {
        this.name = name;
        this.dialogues = dialogues;
        this.reward = reward;
        this.quest = quest;
        this.riddle = riddle;
    }
    
    public void talk(Player player, Scanner scanner) {
        System.out.println("\n" + name + " says:");
        System.out.println(dialogues.get(dialogueIndex));
        dialogueIndex = (dialogueIndex + 1) % dialogues.size();
        
        if (riddle != null && !riddleSolved) {
            System.out.print("\n" + name + " has a riddle for you. Would you like to hear it? (yes/no): ");
            String tryRiddle = scanner.nextLine().toLowerCase();
            if ("yes".equals(tryRiddle)) {
                System.out.println("\n" + name + " asks: " + riddle.get("question"));
                System.out.print("Your answer: ");
                String answer = scanner.nextLine().toLowerCase().trim();
                
                @SuppressWarnings("unchecked")
                List<String> answers = (List<String>) riddle.get("answers");
                boolean correct = answers.stream()
                    .anyMatch(a -> a.toLowerCase().equals(answer));
                
                if (correct) {
                    System.out.println("\n" + name + ": Correct! Well done, adventurer!");
                    riddleSolved = true;
                    @SuppressWarnings("unchecked")
                    Map<String, Object> riddleReward = (Map<String, Object>) riddle.get("reward");
                    
                    if (riddleReward.containsKey("gold")) {
                        player.setGold(player.getGold() + (Integer) riddleReward.get("gold"));
                        System.out.println("You received " + riddleReward.get("gold") + " gold for solving the riddle!");
                    }
                    if (riddleReward.containsKey("exp")) {
                        player.gainExp((Integer) riddleReward.get("exp"));
                        System.out.println("You received " + riddleReward.get("exp") + " experience!");
                    }
                    if (riddleReward.containsKey("item")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> item = (Map<String, Object>) riddleReward.get("item");
                        String itemName = (String) item.get("name");
                        player.getInventory().add(new Item(itemName, "none", 0));
                        System.out.println("You received " + itemName + "!");
                    }
                } else {
                    System.out.println("\n" + name + ": Hmm, that's not quite right. Think more carefully next time.");
                }
            }
        }
        
        if (quest != null && !questGiven) {
            System.out.print("\n" + name + " seems to have a request. Accept their quest? (yes/no): ");
            String offerQuest = scanner.nextLine().toLowerCase();
            if ("yes".equals(offerQuest)) {
                if (player.acceptNpcQuest(name, quest)) {
                    questGiven = true;
                }
            }
        }
        
        // Check for quest completion
        for (Quest playerQuest : new ArrayList<>(player.getNpcQuests())) {
            if (playerQuest.getNpc().equals(name) && playerQuest.isComplete()) {
                if ("find_item".equals(playerQuest.getType())) {
                    player.getInventory().removeIf(item -> item.getName().equals(playerQuest.getTarget()));
                }
                player.completeNpcQuest(playerQuest);
                questGiven = false;
                break;
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
}