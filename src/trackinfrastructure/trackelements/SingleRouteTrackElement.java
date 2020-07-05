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

import trackinfrastructure.trackside.TracksideElement;
import utils.ID;


public abstract class SingleRouteTrackElement extends TrackElement {
	
	private Point pointA, pointB;
	private Route routeAB, routeBA;
	private double length;
	
	
	public SingleRouteTrackElement( ID id, Type type, double length ) {
		super( id, type );
		this.length = length;
		
		this.pointA = new Point( this );
		this.pointB = new Point( this );
		addPoint( pointA );
		addPoint( pointB );
			
		routeAB = new Route( pointA, pointB, this.length );
		routeBA = new Route( pointB, pointA, this.length );
		this.activeRoutes.add( routeAB );
		this.activeRoutes.add( routeBA );
	}
	
	@Override
	public void setConnection(char point, Point connectsTo ) {
		switch (point) {
		case 'A':
			setPointConnection(this.pointA, connectsTo);
			break;
		case 'B':
			setPointConnection(this.pointB, connectsTo);
			break;
		default:
			throw new RuntimeException("SingleRouteTrackElement: " + point + " is not a valid point for this element");
		}
	}
	
	@Override
	public Point getPoint(char point) {
		switch (point) {
		case 'A':
			return this.pointA;
		case 'B':
			return this.pointB;
		default:
			throw new RuntimeException("SingleRouteTrackElement: " + point + " is not a valid point for this element");
		}
	}
	
	
	
	@Override
	public void addTracksideElement( TracksideElement element, Point from, double distance, int direction ) {
		if ( distance > length )
			throw new RuntimeException("addTracksideElement: Distance is greater then track length!");
		
		if ( from == pointA ) {
			if ( direction >= 0 )
				routeAB.addTracksideElement(element, distance);
			if ( direction <= 0 ) 
				routeBA.addTracksideElement(element, length - distance);
			return;
		}
		
		if ( from == pointB ) {
			if ( direction >= 0 )
				routeBA.addTracksideElement(element, distance);
			if ( direction <= 0 ) 
				routeAB.addTracksideElement(element, length - distance);
			return;
		}

		throw new RuntimeException("addTracksideElement: The from Point does not belong to this track!");
	}


	@Override
	public void getOccupation(Route route, double distance) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public Route getOppositeRoute( Route route ) {
		if ( route == routeAB )
			return routeBA;
		if ( route == routeBA )
			return routeAB;
		
		throw new RuntimeException("getOppositRoute: route does not belong to this track!");
	}

}
