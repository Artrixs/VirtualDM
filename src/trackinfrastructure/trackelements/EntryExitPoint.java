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

import java.util.List;

import trackinfrastructure.trackside.TracksideElement;
import train.Train;
import utils.ID;

public class EntryExitPoint extends TrackElement {

	private final double MAX_LENGTH = 10000;
	
	private Point connectionPoint, endPoint;
	private Route entryRoute, exitRoute;
	private List<Train> trains;
	
	public EntryExitPoint( ID id, List<Train> trains ) {
		super( id, Type.BUFFERSTOP );
		this.trains = trains;
		
		this.connectionPoint = new Point( this );
		this.endPoint 		 = new Point( this );
		addPoint( connectionPoint );
		addPoint( endPoint );
		
		this.entryRoute = new Route( endPoint, connectionPoint, MAX_LENGTH );
		this.exitRoute  = new Route( connectionPoint, endPoint, MAX_LENGTH );
		this.activeRoutes.add( entryRoute );
		this.activeRoutes.add( exitRoute );
	}
	
	@Override
	public void setConnection(char point, Point connectsTo ) {
		switch (point) {
		case 'A':
			setPointConnection(this.connectionPoint, connectsTo);
			break;
		default:
			throw new RuntimeException("EntryExitPoint: " + point + " is not a valid point for this element");
		}
	}
	
	@Override
	public Point getPoint(char point) {
		switch (point) {
		case 'A':
			return this.connectionPoint;
		default:
			throw new RuntimeException("EntryExitPoint: " + point + " is not a valid point for this element");
		}
	}
	
	public Train enterTrain( double length, double speed ) {
		Train train = new Train( length );
		
		//TODO: Decide if train enters in speed, reduced speed or stopped based on the next block status
		// Allow to create a queue of trains waiting.
		train.setPosition( entryRoute, MAX_LENGTH-50, speed );			
		this.trains.add( train );
		
		return train;
	}
	
	/**
	 * When a train is fully within the EntryExitPoint and it's on a exit route you can remove it.
	 */
	@Override
	public void updateOccupation( double start, double end, Route route, Train train ) {
		if( route == exitRoute && start > 0 )
			train.setToBeRemoved( true );
		else
			super.updateOccupation( start, end, route, train );
	}
	

	@Override
	public void getOccupation( Route route, double distance ) {
		// TODO Auto-generated method stub

	}

	@Override
	public Route getOppositeRoute( Route route ) {
		if( route == entryRoute )
			return exitRoute;
		
		if ( route == exitRoute )
			return entryRoute;
		
		throw new RuntimeException("getOppositRoute: route does not belong to this track!");
	}

	@Override
	public void addTracksideElement( TracksideElement element, Point from, double distance, int direction ) {
			throw new RuntimeException("addTracksideElement: You can't add a trackside element to Entry/Exit!");	
	}
}
