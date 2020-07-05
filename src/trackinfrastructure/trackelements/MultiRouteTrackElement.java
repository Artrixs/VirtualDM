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

import utils.ID;

/*
 * Idea per gestire la trackoccupation, si ragiona su due corridoio, il primo per un treno che non è in manovra deve fermarsi prima
 * non serve molta granularità nella gestione. Se però è in manovra deve potersi accostare se l'occupazione è sulla stessa route
 * se no deve fermarsi prima.
 */

public abstract class MultiRouteTrackElement extends TrackElement {

	protected List<Route> possibleRoutes;
	
	public MultiRouteTrackElement(ID id, Type type) {
		super(id, type);
		this.possibleRoutes = new ArrayList<Route>();
	}
	
	protected void addPossibleRoute(Route route) {
		if ( possibleRoutes.contains(route ) ) 
			return;
		possibleRoutes.add(route);
	}
	
	protected Route getRoute(Point start, Point end) {
		if( !this.points.contains(start) || !this.points.contains(end) )
			throw new RuntimeException("getRoute: A point does not belong to this track!");
		
		for(Route r : possibleRoutes) {
			if(r.getStart() == start && r.getEnd() == end)
				return r;
		}
		throw new RuntimeException("getRoute: There is no route connecting this points!");
	}
	
	
	/*
	 * Add a possible Route to the active ones, if there is a route with the same origin it gets removed.
	 */
	protected void addActiveRoute(Route route) {
		if( !possibleRoutes.contains( route ) )
			throw new RuntimeException("addActiveRoute: Route does not belong to possible routes!");
			
		for (int i = 0; i < activeRoutes.size(); i++) {
			if( activeRoutes.get(i).getStart() == route.getStart() ) {
				activeRoutes.set(i, route);
				return;
			}
		}
		
		activeRoutes.add( route );
	}
	
	@Override
	public Route getOppositeRoute(Route route) {
		if( possibleRoutes.contains( route ) ) {
			for( Route r : possibleRoutes ) {
				if (r.getStart() == route.getStart() && r.getEnd() == route.getEnd())
					return r;
			}
		} 
		throw new RuntimeException("getOppositRoute: route does not belong to this track!");
	}
	
	
	@Override
	public void getOccupation(Route route, double distance) {
		
	}

}
