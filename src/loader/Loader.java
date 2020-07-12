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
package loader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import trackinfrastructure.Layout;
import trackinfrastructure.trackelements.Point;
import trackinfrastructure.trackelements.Track;
import trackinfrastructure.trackelements.TrackElement;
import utils.ID;

public class Loader {
	
	private static Layout layout;
	private static List<LoaderElement> loaderElements;
	
	public static Layout load( String file ) {
		layout = new Layout();
		loaderElements = new ArrayList<LoaderElement>();
		
		//Loads all the files
		Path filePath = Paths.get(file);
		loaderElements.addAll( Parser.parse( filePath, layout ));
		
		for (LoaderElement el : loaderElements) {
			updateElement(el);
		}
		
		return layout;
	}
	
	private static void updateElement(LoaderElement el) {
		switch( el.type ) {
			case TRACK:
				setConnections(el, 'A', 'B');
				break;
			case SWITCH:
				setConnections(el, 'A', 'B', 'C');
				break;			
			default:
				throw new RuntimeException("Loader: Element type not supported");
		}
	}
		
	private static void setConnections(LoaderElement el, char... points) {
		TrackElement trackElement = layout.getTrackElement(el.id);
		for(char c : points) {
			String value = "connection" + c;
			if( el.hasAttribute( value ) ) {
				trackElement.setConnection(c, getPointConnection(el, (String) el.getAttribute( value )));
			}
		}
	}
	
	private static Point getPointConnection(LoaderElement el, String string) {
		String[] parts = string.split(":");
		
		// Extract the point of connection
		char pointOfConnection;
		if (parts[parts.length-1].length() > 1) throw new RuntimeException("Loader: Connection point must be a single letter");
		pointOfConnection = string.charAt(string.length() - 1 );
		if( !(pointOfConnection >= 'A' && pointOfConnection <= 'Z') ) {
			throw new RuntimeException("Loader: Connection point is not a uppercase letter");
		}
		
		//Get the other trackElement
		ID id;
		if ( parts.length == 2)
			id = ID.fromString(el.id.getLocation() + ":" + parts[0]);
		else
			id = ID.fromString(parts[0] + ":" + parts[1]);

		TrackElement track = layout.getTrackElement( id );
		
		return track.getPoint(pointOfConnection);
	}

}
