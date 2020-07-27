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
package trackinfrastructure.trackelements;

import java.util.ArrayList;
import java.util.List;

import train.Train;
import utils.ID;
import trackinfrastructure.TrackCircuit;
import trackinfrastructure.trackside.TracksideElement;

/**
 * 
 * @author Arturo Misino
 *
 */
public abstract class TrackElement {
	
	static enum Type { TRACK, BUFFERSTOP, JOINT, ENTRYEXIT, SWITCH };
	
	private ID id;
	private Type type = null;
	
	protected List<Route> activeRoutes;
	protected List<Point> points;
	
	protected boolean occupied;
	protected List<Train> occupiedBy;
	
	protected TrackCircuit parentTrackCircuit;
	
	//This two function return a list of trackside elements or occupations on the specific route after a distance.
	public abstract void addTracksideElement( TracksideElement element, Point from, double distance, int direction );
	public abstract void getOccupation( Route route, double distance );
	public abstract Route getOppositeRoute( Route route );
	public abstract void setConnection(char point, Point connectsTo);
	public abstract Point getPoint(char point);
		
	public TrackElement( ID id, Type type ) {
		this.id = id;
		this.type = type;
		
		this.points = new ArrayList<Point>();
		this.activeRoutes = new ArrayList<Route>();
		
		this.occupied = false;
		this.occupiedBy = new ArrayList<Train>();
		
		this.parentTrackCircuit = null;	
	}
	
	public Type getType() { return this.type; }
	
	protected void addPoint( Point point ) {
		if ( points.contains( point ) ) 
			return;
		points.add( point );
	}
	
	public void setPointConnection( Point point, Point connectsTo ) {
		for( Point p : this.points ) {		
			if ( p == point ) {
				p.setConnectsTo( connectsTo );
				if ( connectsTo != null )
					connectsTo.setConnectsTo( p );
				
				return;
			}
		}
		
		throw new RuntimeException("setPointConnection: No matching point found!");
	}
		
	public Route getRoute( Point startPoint ) {
		for( Route r : activeRoutes ) {
			if ( r.getStart() == startPoint )
				return r;
		}
		return null;
	}
	
	public Route getRouteFromEnd( Point endPoint ) {
		for( Route r: activeRoutes ) {
			if ( r.getEnd() == endPoint ) 
				return r;
		}
		return null;
	}

	public TrackCircuit getParentTrackCircuit() { return this.parentTrackCircuit; }
	public void setParentTrackCircuit( TrackCircuit circuit ) {
		this.parentTrackCircuit = circuit;
		parentTrackCircuit.addTrack( this );
	}
	
	public boolean isOccupied() { return this.occupied; }	
	
	protected void setOccupied( boolean value ) {
		if ( this.occupied != value ) {
			this.occupied = value;
			if ( parentTrackCircuit != null ) {
				parentTrackCircuit.scheduleUpdate();
			}
		}
	}
	
	public void clearOccupation( Route route, Train train ) {
		if( this.occupiedBy.contains( train ) ) {
			this.occupiedBy.remove( train );
			
			if( this.occupiedBy.size() == 0 )
				setOccupied( false );
			
			return;
		} 
		
		throw new RuntimeException("clearOccupation: trying to clear occupation on track not occupied by the train");
	}
	
	public void updateOccupation( double start, double end, Route route, Train train ) {
		if( !this.occupiedBy.contains( train ) ) {
			this.occupiedBy.add( train );
			setOccupied( true) ;
		}
	}
		
	@Override
	public String toString() {
		return id.toString();
	}
}
