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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import trackinfrastructure.trackelements.Point;
import trackinfrastructure.trackelements.Route;
import trackinfrastructure.trackside.Signal;
import trackinfrastructure.trackside.TracksideElement;
import train.TargetSpeed.Type;
import train.Train.State;
import utils.Speed;
import utils.Pair;

/* TODO:
 * fix calcualteTargetSpeed(), improve Signal handling, implement Station hanling
 */

public class Driver {
	
	private Train train;
	private double trainPosition, trainAcceleration, trainSpeed;
	private State trainState;
	
	private Map<TargetSpeed.Type, Double> currentSpeedLimits;
	private List<TargetSpeed> nextTargetSpeeds;
	
	private double targetSpeed;
	
	private List<Pair<TracksideElement, Double>> nextTracksideElements;
	
	protected Driver(Train train) {
		this.train = train;
		
		this.nextTargetSpeeds = new ArrayList<TargetSpeed>();
		this.currentSpeedLimits = new HashMap<TargetSpeed.Type, Double>();
		this.currentSpeedLimits.put(Type.TRAIN, train.trainComposition.getMaximumSpeed());
		
		updateTrainParameters();
	}
	
	protected void update() {
		updateTrainParameters();
		
		//TODO: Calculate targetSpeed (get rid of the actual method!) 
		//		- first of all get tracksideElement further along, and obstruction along the track,
		//		- add those to the list of tracksideElements we have ahead
		//		- from the list and currentSpeedLimits calculate the target speed
		calculateTargetSpeed();
		
		switch ( trainState ) {
		case ACCELERATE:
			if ( trainSpeed > targetSpeed) {
				train.setState( State.IDLE );
				train.brakePower = 0;
				train.tractionPower = 0;
				break;
			}		
			break;
		case BRAKE:				
			if ( trainSpeed < targetSpeed) {
				train.setState( State.IDLE );
				train.tractionPower = 0;
				train.brakePower = 0;
				break;
			}
			
			break;
		case IDLE:
			if ( trainSpeed < targetSpeed - 1/3.6) {
				train.setState( State.ACCELERATE );
				train.tractionPower = 2;
				train.brakePower = 0;
				break;
			}

			if( trainSpeed < 0.5 && trainSpeed > targetSpeed) {
				train.setState( State.BRAKE );
				train.tractionPower = 0;
				train.brakePower = 2;
				break;
			}

			if ( trainSpeed >=0.5 && trainSpeed > targetSpeed + 1/3.6) {
				train.setState( State.BRAKE );
				train.tractionPower = 0;
				train.brakePower = 2;
				break;
			}
			
			break;
		case STOP:
			break;
		}
	}
	
	private void calculateTargetSpeed() {
		double target = Collections.min(currentSpeedLimits.values());
		
		double tempTarget = targetSpeedFromTrack();
		if ( tempTarget >= 0 && tempTarget < target ) 
			target = tempTarget;
		
		tempTarget = targetSpeedFromTrackside();
		if ( tempTarget >= 0 && tempTarget < target ) 
			target = tempTarget;
		
		targetSpeed = target;
	}
	
	//Check for obstructions along the route, stop before them
	private double targetSpeedFromTrack() {
		boolean hasObstruction = false;
		Route r = train.getFrontCurrentRoute();
		double distanceToEnd = r.getLength() - train.getPosition();
		
		// while distance is less than the visible sight of the driver
		// FIXME: Change the fixed 500 limit
		while ( distanceToEnd < 500) {
			Point connection = r.getEnd().getConnectsTo();
			
			if ( connection == null ) {
				hasObstruction = true;
				break;
			}
			
			r = connection.getParentTrack().getRoute( connection );
			if ( r == null ) {
				hasObstruction = true;
				break;
			}
			
			distanceToEnd = distanceToEnd + r.getLength();
		}
		
		if ( hasObstruction && Speed.distanceToAchieveSpeed( train.getSpeed(), 0, trainAcceleration ) > distanceToEnd - 5)  {
			return 0;
		}
		
		return -1;
	}
	
	private double targetSpeedFromTrackside() {
		double target = Collections.min(currentSpeedLimits.values());
		double tmpTarget;
		
		Route r = train.getFrontCurrentRoute();
		if ( r.getTracksideElements() == null )
			return -1;
		for ( Pair<TracksideElement, Double> p : r.getTracksideElements() ) {
			if ( p.getRight() < train.getPosition() )
				continue;
			
			if ( p.getLeft() instanceof Signal && ( (Signal) p.getLeft()).isStop() ) {
				if ( Speed.distanceToAchieveSpeed(train.getSpeed(), 0, trainAcceleration) > p.getRight() - train.getPosition() - 5 ) {
					return 0;
				}
			}
		}
		
		double lookAheadDistance = r.getLength() - train.getPosition();;
		return -1;
	}
	
	
	public void trainAdvancedOf(double travelDistance) {
 		 
 		ListIterator<Pair<TracksideElement, Double>> iter = nextTracksideElements.listIterator();
 		Pair<TracksideElement, Double> p;
 		while(iter.hasNext()) {
 			p = iter.next();
 			p.setRight( p.getRight() - travelDistance );
 			if( p.getRight() < 0 )
 				iter.remove(); 				
 		}
 		
 		//lookAhead = lookAhead - travelDistance; 		
	}
		
	
	protected void addTargetSpeed(double speed, double distance, Type type) {
		nextTargetSpeeds.add(new TargetSpeed(speed, distance, type));
		sortTargetSpeeds();
	}
	
	protected void clearSpeedLimit(Type type) {
		this.currentSpeedLimits.remove(type);
	}
	
	protected void setSpeedLimit(double speed, Type type) {
		this.currentSpeedLimits.put(type, speed);
	}
	
	private void clearTargetSpeeds() {
		nextTargetSpeeds.clear();
	}
	
	private void clearTargetSpeedType(TargetSpeed.Type type) {
		List<TargetSpeed> toRemove =  new ArrayList<TargetSpeed>();
		for ( TargetSpeed t : nextTargetSpeeds ) {
			if( t.getType() == type )
				toRemove.add(t);
		}
		nextTargetSpeeds.removeAll(toRemove);
		
	}
	private void sortTargetSpeeds() {
		//TODO: Replace boublesort with someting far better!
		boolean switched = true;
		while ( switched ) {
			switched = false;
			for (int i=0; i < nextTargetSpeeds.size() - 1; i++) {
				if( nextTargetSpeeds.get(i).getDistance() > nextTargetSpeeds.get(i+1).getDistance()) {
					TargetSpeed tmp = nextTargetSpeeds.get(i);
					nextTargetSpeeds.set(i,  nextTargetSpeeds.get(i+1));
					nextTargetSpeeds.set(i+1, tmp);
					switched = true;
				}
			}
		}
	}
	
	protected void updateNextTargetSpeeds( double distanceTraveled ) {
		for ( TargetSpeed t : nextTargetSpeeds ) {
			t.setDistance(t.getDistance() - distanceTraveled);
		}
		
		while(nextTargetSpeeds.size() > 0 && nextTargetSpeeds.get(0).getDistance() < 0) {
			TargetSpeed t = nextTargetSpeeds.remove(0);
			if( t.getSpeed() < 0 )
				this.currentSpeedLimits.remove(t.getType());
			else
				this.currentSpeedLimits.put(t.getType(), t.getSpeed());
		}	
	}
	
	protected void computeTargetSpeed() {
		//Compute targetSpeed from the nextTargets
		double target = Collections.min(currentSpeedLimits.values());
		
		for ( TargetSpeed t : nextTargetSpeeds ) {
			if( t.getSpeed() < 0 )
				continue;
			if( t.getSpeed() >= target )
				continue;
			if( Speed.distanceToAchieveSpeed(train.speed, t.getSpeed(), trainAcceleration) > t.getDistance()) {
				target = t.getSpeed();
			}
		}
		
		targetSpeed = target;
	}
	
	private void updateTrainParameters() {
		this.trainPosition = train.getPosition();
		this.trainAcceleration = train.trainComposition.getAcceleration(train.speed, train.tractionPower, train.brakePower) ;
		this.trainSpeed = train.getSpeed();
		this.trainState = train.getState();
	}

}
