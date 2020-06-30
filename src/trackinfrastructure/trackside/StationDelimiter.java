package trackinfrastructure.trackside;

import train.Train;

public class StationDelimiter extends TracksideElement {

	public StationDelimiter(String id) {
		super(id);
		this.type = Type.STATION;
	}
	
	@Override
	public void onPass(Train train) {
		//Nothing happens
		return;
	}

}
