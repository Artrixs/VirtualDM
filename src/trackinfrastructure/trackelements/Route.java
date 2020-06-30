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

import trackinfrastructure.trackside.TracksideElement;
import utils.Pair;

/**
 * A Route is a path between 2 end points of a TrackElement on which a train can travel.
 * It has a length and a list of TracksideElements attached to it.
 * @author Arturo Misino
 *
 */
public class Route {
	
	private Point start;
	private Point end;
	private double length;
	
	private List<Pair<TracksideElement, Double>> tracksideElements = null;;
	
	/**
	 * Create a route from 2 end points of a track with a given length.
	 * @param start Start point of the Route
	 * @param end	End point of the Route
	 * @param length Length of the Route
	 */
	public Route( Point start, Point end, double length ) {
		this.start = start;
		this.end = end;
		if( start.getParentTrack() != end.getParentTrack() ) throw new RuntimeException("Route: start and end parent track are different");
		this.length = length;
	}
	
	public Point getStart() { return this.start; }
	public Point getEnd() { return this.end; }
	public double getLength() { return this.length; }
	public TrackElement getParentTrack() { return this.start.getParentTrack(); }
	public List<Pair<TracksideElement, Double>> getTracksideElements() { return this.tracksideElements; }
	
	/**
	 * Add  a tracksideElement to this route
	 * @param element
	 * @param distance distance from the start of the route
	 */
	public void addTracksideElement(TracksideElement element, double distance) {
		if(tracksideElements == null) {
			tracksideElements = new ArrayList<Pair<TracksideElement, Double>>();
		}
		
		int index = 0;
		while ( index < tracksideElements.size() && distance > tracksideElements.get( index ).getRight() ) {
			index++;
		}
		tracksideElements.add( index, new Pair<TracksideElement, Double>( element, distance ) );
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		long temp;
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return this.start == other.start && this.end == other.end && this.length == other.length;
	}	
}
