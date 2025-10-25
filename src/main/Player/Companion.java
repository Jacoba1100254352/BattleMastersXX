package Player;


public abstract class Companion
{
	private final String name;
	private final double speed;
	
	public Companion(String _name, double _speed) {
		this.name = _name;
		this.speed = _speed;
	}
	
	public String getName() {return name;}
	
	public double getSpeed() {
		return speed;
	}
}
