package utils;

import java.util.HashSet;
import java.util.Set;

public class UpdateList {

	private Set<IUpdatable> scheduledElementsForUpdate;
	
	public UpdateList() {
		this.scheduledElementsForUpdate = new HashSet<IUpdatable>();
	}
	
	public void registerElementForUpdate(IUpdatable element) {
		if ( scheduledElementsForUpdate.contains( element ) )
			return;
		scheduledElementsForUpdate.add(element);
	}
	
	public void runUpdates() {
		Set<IUpdatable> list = scheduledElementsForUpdate;
		scheduledElementsForUpdate = new HashSet<IUpdatable>();
		for( IUpdatable e : list ) {
			e.update();
		}
	}
}
