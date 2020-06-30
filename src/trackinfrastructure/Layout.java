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

/**
 * Store all the trackinfrastructure (tracks and tracksideElements) of the game
 * @author Arturo Misino
 *
 */
public class Layout {
	
	private Map<ID, TrackElement> trackElements;
	private Map<ID, TracksideElement> tracksideElements;
	
	public Layout() {
		this.trackElements = new HashMap<ID, TrackElement>();
		this.tracksideElements = new HashMap<ID, TracksideElement>();
	}
	
	public void addTrackElement( ID id, TrackElement el ) { trackElements.put( id, el ) ; }
	public void addTracksideElement( ID id, TracksideElement el) { tracksideElements.put( id, el ) ; }
	
	public TrackElement getTrackElement( ID id ) { return trackElements.get( id ); }
	public TracksideElement getTracksideElement( ID id ) { return tracksideElements.get( id ) ; }
	

}
