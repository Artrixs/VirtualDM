package train;

public class TargetSpeed {
	public static enum Type {LINE, TRAIN, SIGNALING, SERVICE, WORKING}
	
	private final double speed;
	private double distance;
	private Type type;
	
	public TargetSpeed(double speed, double distance, Type type) {
		this.speed = speed;
		this.distance = distance;
		this.type = type;
	}
	
	public double getSpeed() { return this.speed; }
	public double getDistance() { return this.distance; }
	public Type getType() { return this.type; }
	public void setDistance(double distance) { this.distance = distance; }
}