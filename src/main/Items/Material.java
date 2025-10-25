package Items;


/**
 * Enhanced Items.Material class for crafting
 */
class Material extends Item
{
	private String materialType; // Metal, Gem, Organic, Magical
	private int tier; // 1-10, higher tiers for better items
	private boolean isRare;
	
	public Material(String name, String materialType, int tier) {
		super(name, String.format("A %s material of tier %d", materialType, tier), "Items.Material", tier * 25, 1);
		this.materialType = materialType;
		this.tier = tier;
		this.isRare = tier > 7;
	}
	
	public String getMaterialType() {return materialType;}
	
	public int getTier() {return tier;}
	
	public boolean isRare() {return isRare;}
}
