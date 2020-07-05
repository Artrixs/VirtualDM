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

public class Switch extends MultiRouteTrackElement {
	
	private Point pointA, pointB, pointC;
	private double lengthA, lengthB, lengthC;
	private Route routeAB, routeAC, routeBA, routeCA;
	
	public Switch( ID id, double lengthA, double lengthB, double lengthC ) {
		super( id, Type.SWITCH );
		this.lengthA = lengthA;
		this.lengthB = lengthB;
		this.lengthC = lengthC;
		
		this.pointA = new Point( this );
		this.pointB = new Point( this );
		this.pointC = new Point( this );
		this.points.add( pointA );
		this.points.add( pointB );
		this.points.add( pointC );
		
		routeAB = new Route( pointA, pointB, this.lengthA + this.lengthB );
		routeBA = new Route( pointB, pointA, this.lengthA + this.lengthB );
		routeAC = new Route( pointA, pointC, this.lengthA + this.lengthC );
		routeCA = new Route( pointC, pointA, this.lengthA + this.lengthC );
		
		addPossibleRoute( routeAB );
		addPossibleRoute( routeBA );
		addPossibleRoute( routeAC );
		addPossibleRoute( routeCA );
				
		addActiveRoute( routeAB );
		addActiveRoute( routeBA );
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
		case 'C':
			setPointConnection(this.pointC, connectsTo);
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
		case 'C':
			return this.pointC;
		default:
			throw new RuntimeException("SingleRouteTrackElement: " + point + " is not a valid point for this element");
		}
	}
	
	public void setOnB() {
		if ( activeRoutes.contains( routeAB ) )
			return;
		
		addActiveRoute( routeAB );
		addActiveRoute( routeBA );
	}
	
	public void setOnC() {
		if ( activeRoutes.contains( routeAC ) )
			return;
		
		addActiveRoute( routeAC );
		addActiveRoute( routeCA );
	}

	@Override
	public void addTracksideElement( TracksideElement element, Point from, double distance, int direction ) {
		
		if ( from == pointA && distance <= lengthA ) {
			if ( direction >= 0 ) {
				routeAB.addTracksideElement( element, distance );
				routeAC.addTracksideElement( element, distance );
			}
			if ( direction <= 0 ) {
				routeBA.addTracksideElement( element, routeBA.getLength() - distance );
				routeCA.addTracksideElement( element, routeCA.getLength() - distance );
			}
			
			return;
		}
		
		if ( from == pointB && distance < lengthB ) {
			if ( direction >= 0 ) 
				routeBA.addTracksideElement( element, distance);
			if ( direction <= 0 )
				routeAB.addTracksideElement( element, routeAB.getLength() - distance );
			
			return;
		}
		
		if ( from == pointC && distance < lengthC ) {
			if ( direction >= 0 )
				routeCA.addTracksideElement( element, distance);
			if ( direction <= 0 )
				routeAC.addTracksideElement( element, routeAC.getLength() - distance );
			
			return;
		}
		
		throw new RuntimeException("addTracksideElement: Check that point belongs to this Switch and distance is correct!");
		
	}

}
