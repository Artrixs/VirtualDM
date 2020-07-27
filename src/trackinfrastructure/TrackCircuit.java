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

import java.util.ArrayList;
import java.util.List;

import trackinfrastructure.trackelements.TrackElement;
import utils.ID;
import utils.IUpdatable;
import utils.UpdateList;

public class TrackCircuit implements IUpdatable {
	static enum State {FREE, OCCUPIED, BLOCKED}
	
	private ID id;
	private State state;
	private List<TrackElement> tracks;
	private UpdateList updateList;
	
	public TrackCircuit (ID id, UpdateList updateList) {
		this.id = id;
		this.state = State.FREE;
		tracks = new ArrayList<TrackElement>();
		
		this.updateList = updateList;
	}
	
	public State getState() { return this.state; }
	
	public void addTrack(TrackElement track) { 
		this.tracks.add(track); 
		update();
	}
	
	public void scheduleUpdate() {
		updateList.registerElementForUpdate(this);
	}
	
	public void update() {
		for(TrackElement t : tracks) {
			if (t.isOccupied() ) {
				this.state = State.OCCUPIED;
				return;
			}
		}
		//TODO: Implement State.BLOCKED
		this.state = State.FREE;
	}
	
	public String toString() {
		String result = "TrackCircuit " + id + " is " + state(this.state);
		
		return result;
	}
	
	static public String state(State state) {
		String result = "free";
		switch(state) {
		case FREE:
			result = "free";
			break;
		case OCCUPIED:
			result = "occupied";
			break;
		case BLOCKED:
			result = "blocked";
			break;
		}
		return result;
	}

}
