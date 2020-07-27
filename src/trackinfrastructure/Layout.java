/*
 * Copyright (c) 2020 Arturo Misino <misino.arturo@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package trackinfrastructure;

import java.util.HashMap;
import java.util.Map;

import trackinfrastructure.trackelements.TrackElement;
import trackinfrastructure.trackside.TracksideElement;
import utils.ID;
import utils.UpdateList;

/**
 * Store all the trackinfrastructure (tracks and tracksideElements) of the game
 * @author Arturo Misino
 *
 */
public class Layout {
	
	private Map<ID, TrackElement> trackElements;
	private Map<ID, TracksideElement> tracksideElements;
	private Map<ID, TrackCircuit> trackCircuits;
	private UpdateList updateList;
	
	public Layout() {
		this.trackElements = new HashMap<ID, TrackElement>();
		this.tracksideElements = new HashMap<ID, TracksideElement>();
		this.trackCircuits = new HashMap<ID, TrackCircuit>();
		this.updateList = new UpdateList();
	}
	
	public TrackElement getTrackElement( ID id ) { return trackElements.get( id ); }
	public TracksideElement getTracksideElement( ID id ) { return tracksideElements.get( id ) ; }
	public TrackCircuit getTrackCircuit( ID id ) { return trackCircuits.get( id ) ; }
	public UpdateList getUpdateList() { return updateList ; }
 	
	public void addTrackElement( ID id, TrackElement el ) { 
		if ( trackElements.containsKey( id ) ) {
			throw new RuntimeException("Layout: There is already an element with this ID in the layout!");
		}
		trackElements.put( id, el ); 
	}
	
	public void addTracksideElement( ID id, TracksideElement el) {
		if ( tracksideElements.containsKey( id ) ) {
			throw new RuntimeException("Layout: There is already an element with this ID in the layout!");
		}
		tracksideElements.put( id, el );
	}
	
	public void addTrackCircuit( ID id, TrackCircuit el ) {
		if ( trackCircuits.containsKey( id ) ) {
			throw new RuntimeException("Layout: There is already an element with this ID in the layout");
		}	
		trackCircuits.put( id,  el );
	}

	

}
