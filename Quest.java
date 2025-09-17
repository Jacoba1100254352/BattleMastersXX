 // ============== Quest.java ==============

import java.util.Map;

public class Quest {
    private String npc;
    private String type;
    private String target;
    private int quantity;
    private int progress;
    private Map<String, Object> reward;
    private String description;
    
    public Quest(String npc, String type, String target, int quantity, 
                Map<String, Object> reward, String description) {
        this.npc = npc;
        this.type = type;
        this.target = target;
        this.quantity = quantity;
        this.progress = 0;
        this.reward = reward;
        this.description = description;
    }
    
    public void incrementProgress() { this.progress++; }
    
    public boolean isComplete() { return progress >= quantity; }
    
    // Getters and setters
    public String getNpc() { return npc; }
    public String getType() { return type; }
    public String getTarget() { return target; }
    public int getQuantity() { return quantity; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public Map<String, Object> getReward() { return reward; }
    public String getDescription() { return description; }
}