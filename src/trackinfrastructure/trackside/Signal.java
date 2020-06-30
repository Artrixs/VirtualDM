package trackinfrastructure.trackside;

import train.Train;

public class Signal extends TracksideElement {

	public static enum Aspect {RED, GREEN};
	
	private Aspect aspect;
	
	public Signal(String id) {
		super(id);
		this.type = Type.SIGNAL;
		this.aspect = Aspect.RED;
	}
	
	public void setAspect(Aspect aspect) { this.aspect = aspect; }
	public Aspect getAspect() { return this.aspect; }
	
	@Override
	public void onPass(Train train) {
		aspect = Aspect.RED;
	}
	
	public boolean isStop() { return aspect == Aspect.RED; }

}
