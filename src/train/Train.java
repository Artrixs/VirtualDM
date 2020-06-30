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
package train;

import java.util.ArrayDeque;
import java.util.Deque;

import trackinfrastructure.trackelements.Point;
import trackinfrastructure.trackelements.Route;
import trackinfrastructure.trackelements.TrackElement;
import virtualdm.Clock;


/* TODO:
 * - better states and conditions ( split acceleration/ braking to continuos values)
 * - start() verify that it can proceed (i.e signal is green)
 * - fire onPass event for tracksideElements when passing over them
 */

/**
 * Represent a set of cars/engines on the layout
 * @author Arturo Misino
 *
 */
public class Train {
	
	static enum State {STOP, BRAKE, ACCELERATE, IDLE}
	
	protected Driver driver;
	protected TrainComposition trainComposition;

	private TrackElement frontCurrentTrack;
	private Route frontCurrentRoute;
	private double frontPosition;
	private TrackElement backCurrentTrack;
	private Route backCurrentRoute;
	private double backPosition;
	
	private Deque<Route> routesOccupiedByTrain;
	
	
	private State state;
	private double length;
	
	protected double speed;
	
	protected double tractionPower;
	protected double brakePower;
		
	private boolean toBeRemoved = false;
	private boolean derailed = false;
	
	/**
	 * Create a Train, it MUST be placed on the layout with the setPosition method!
	 * @param length The length of the train
	 */
	public Train(double length) {
		this.trainComposition = new TrainComposition(this);
		this.driver = new Driver(this);
		
		this.length = length;
		this.state = State.STOP;
		
		this.routesOccupiedByTrain = new ArrayDeque<Route>();
	}
	
	public double getSpeed() { return speed;}
	public double getPosition() { return frontPosition; }
	
	public boolean isToBeRemoved() { return this.toBeRemoved; }
	public void setToBeRemoved(boolean remove) { this.toBeRemoved = remove; }
	
	public void setState(State s) { this.state = s; }
	public State getState() { return this.state; }
		
	protected Route getFrontCurrentRoute() { return this.frontCurrentRoute; }
	

	public void setPosition(Route route, double position, double speed) {
		//Verify there is enough space in rear of the front to place the train
		if( length > position  ) {
			double tmpLength = length - position;
			Route tmpRoute = route;
			while( tmpLength > 0 ) {
				if( route.getStart().getConnectsTo() != null ) {
					TrackElement tmpTrack = route.getStart().getConnectsTo().getParentTrack();
					tmpRoute = tmpTrack.getRouteFromEnd(route.getStart().getConnectsTo());
					if( tmpRoute == null)
						throw new RuntimeException("setPosition: There is not an available route (with enough length) to place train");
					tmpLength -= tmpRoute.getLength();
				} else 
					throw new RuntimeException("setPosition: There is not enough space for the train!");
			}
		}
		
		this.frontCurrentRoute = route;
		this.frontCurrentTrack = route.getStart().getParentTrack();
		this.frontPosition = position;
		
		this.routesOccupiedByTrain = new ArrayDeque<Route>();
		routesOccupiedByTrain.addFirst(frontCurrentRoute);
		
		if ( length <= position ) {
			this.backCurrentRoute = frontCurrentRoute;
			this.backCurrentTrack = frontCurrentTrack;
			this.backPosition = position - length;
		} else {
			double tmpLength = length - position;
			TrackElement tmpTrack = frontCurrentRoute.getStart().getConnectsTo().getParentTrack();
			Route tmpRoute = tmpTrack.getRouteFromEnd(frontCurrentRoute.getStart().getConnectsTo());
			while ( tmpLength > tmpRoute.getLength()) {
				tmpLength -= tmpRoute.getLength();
				routesOccupiedByTrain.addLast(tmpRoute);
				tmpTrack = tmpRoute.getStart().getConnectsTo().getParentTrack();
				tmpRoute = tmpTrack.getRouteFromEnd(tmpRoute.getStart().getConnectsTo());			
			}

			this.backCurrentRoute = tmpRoute;
			this.backCurrentTrack = tmpTrack;
			this.backPosition = tmpRoute.getLength() - tmpLength;
			routesOccupiedByTrain.addLast(backCurrentRoute);
		}
		
		if( frontCurrentRoute == backCurrentRoute ) {
			frontCurrentTrack.updateOccupation(backPosition, frontPosition, frontCurrentRoute, this);
		} else {
			frontCurrentTrack.updateOccupation(0, frontPosition, frontCurrentRoute, this);
			for(Route r : routesOccupiedByTrain) {
				if( r == frontCurrentRoute || r == backCurrentRoute)
					continue;
				r.getParentTrack().updateOccupation(0, r.getLength(), r, this);
			}
			backCurrentTrack.updateOccupation(backPosition, backCurrentRoute.getLength(), route, this);
		}
		
		this.speed = speed;
		if( speed < 0.5) {
			this.state = State.STOP;
		} else {
			this.state = State.IDLE;
		}
	}
	
	public void setPosition(Route route, double position) {
		setPosition(route, position, 0.0);
	}
		
	public void update() {
		if ( derailed )
			return;
		
		driver.update();
		//FIXME: With better state defined you can remove this switch
		switch(this.state) {
		case ACCELERATE:
			speed += trainComposition.getAcceleration(speed, tractionPower, brakePower) * Clock.getDelta();
			travel();
			break;
		case IDLE:
			travel();
			break;
		case BRAKE:
			speed += trainComposition.getAcceleration(speed, tractionPower, brakePower)* Clock.getDelta();
			travel();
			if ( speed < 0.5 ) {
				this.state = State.STOP;
				this.speed = 0;
			}
			break;
		case STOP:
			break;
		}
	}
	
	public void start() {
		if(this.state == State.STOP) {
			this.state = State.IDLE;
		}
	}
	
	
	
	private void travel() {
		
		double travelDistance = speed * Clock.getDelta();
		travelFront(travelDistance);
		travelBack(travelDistance);
		
		if ( derailed )
			return;
		
		if( frontCurrentRoute == backCurrentRoute )
			frontCurrentTrack.updateOccupation(backPosition, frontPosition, frontCurrentRoute, this);
		else {
			frontCurrentTrack.updateOccupation(0, frontPosition, frontCurrentRoute, this);
			backCurrentTrack.updateOccupation(backPosition, backCurrentRoute.getLength(), backCurrentRoute, this);
		}
		
		//TODO: Change to update the distances on the list of trackisdeElements and obstructions,
		//		dispatch onPass events for the elements we have passed!
		driver.updateNextTargetSpeeds(travelDistance);
	}
	
	private void travelFront(double distance) {
		if ( derailed )
			return;
		
		while ( distance > 0 ) {
			if ( frontPosition + distance < frontCurrentRoute.getLength() ) {
				frontPosition += distance;
				break;
			} else {
				distance = distance - (frontCurrentRoute.getLength() - frontPosition );
				frontCurrentTrack.updateOccupation(0, frontCurrentRoute.getLength(), frontCurrentRoute, this);
				
				Point connection = frontCurrentRoute.getEnd().getConnectsTo();
				if ( connection == null || connection.getParentTrack().getRoute( connection ) == null ) {
					derailed = true;
					return;
				}
				frontCurrentTrack = connection.getParentTrack();
				frontCurrentRoute = frontCurrentTrack.getRoute(connection);
				frontPosition = 0;
				routesOccupiedByTrain.addFirst(frontCurrentRoute);
			}
		}
	}
	
	private void travelBack(double distance) {
		if ( derailed ) 
			return;
		
		while ( distance > 0 ) {
			if( backPosition + distance < backCurrentRoute.getLength() ) {
				backPosition += distance;
				break;
			} else {
				distance = distance - (backCurrentRoute.getLength() - backPosition);
				backCurrentTrack.clearOccupation(backCurrentRoute, this);
				
				routesOccupiedByTrain.removeLast();
				backCurrentRoute = routesOccupiedByTrain.getLast();
				backCurrentTrack = backCurrentRoute.getStart().getParentTrack();
				backPosition = 0;
			}
		}
	}
	
		
	public void reverse() {
		// Create the queue of opposite routes
		Deque<Route> newRoutesOccupiedByTrain = new ArrayDeque<Route>();
		while (routesOccupiedByTrain.size() > 0 ) {
			Route tempRoute = routesOccupiedByTrain.removeFirst();
			Route newRoute = tempRoute.getParentTrack().getOppositeRoute(tempRoute);
			newRoutesOccupiedByTrain.addFirst(newRoute);
		}		
		
		//Update the front and back accordingly
		double oldFrontPosition = frontPosition;
		frontCurrentRoute = newRoutesOccupiedByTrain.getFirst();
		frontCurrentTrack = frontCurrentRoute.getParentTrack();
		frontPosition = frontCurrentRoute.getLength() - backPosition;
		
		backCurrentRoute = newRoutesOccupiedByTrain.getLast();
		backCurrentTrack = backCurrentRoute.getParentTrack();
		backPosition = backCurrentRoute.getLength()  - oldFrontPosition;
		
		// Update the occupation on all tracks
		for (Route r : newRoutesOccupiedByTrain) {
			if(r == newRoutesOccupiedByTrain.getFirst() || r == newRoutesOccupiedByTrain.getLast())
				continue;
			r.getParentTrack().updateOccupation(0, r.getLength(), r, this);
		}
		
		if (frontCurrentRoute == backCurrentRoute) {
			frontCurrentTrack.updateOccupation(backPosition, frontPosition, frontCurrentRoute, this);
		} else {
			frontCurrentTrack.updateOccupation(0, frontPosition, frontCurrentRoute, this);
			backCurrentTrack.updateOccupation(backPosition, backCurrentRoute.getLength(), backCurrentRoute, this);
		}
		
		routesOccupiedByTrain = newRoutesOccupiedByTrain;		
	}
	
	
	public String toString() {
		if ( derailed )
			return "Train ID has derailed!";
		
		StringBuilder result = new StringBuilder();
		result.append("Train ID Speed:" + String.format("%.2f", speed*3.6) + "km/h \n");
		result.append("Front: " + frontCurrentTrack + " " + String.format("%.2f",  frontPosition) + "\n");
		result.append("Back: " + backCurrentTrack + " " + String.format("%.2f",  backPosition));
		return result.toString();
	}
}
