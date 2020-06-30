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

/**
 * A Point is the start or the end of every route. It can connects to another Point creating
 * a connection between 2 routes.
 * @author Arturo Misino
 *
 */
public class Point {
	
	private TrackElement parentTrack;
	private Point connectsTo;
	
	/**
	 * Creates a point with the parentTrack and a connection to another point.
	 * @param parentTrack The track to which the point belongs
	 * @param connectsTo The other Point to connect.
	 */
	public Point( TrackElement parentTrack, Point connectsTo ) {
		this.parentTrack = parentTrack;
		this.connectsTo = connectsTo;
	}
	
	/**
	 * Creates a point with only the parentTrack, no other connection.
	 * @param parentTrack
	 */
	public Point( TrackElement parentTrack ) {
		this.parentTrack = parentTrack;
		this.connectsTo = null;
	}
	
	public TrackElement getParentTrack() { return this.parentTrack; }
	
	public Point getConnectsTo() { return this.connectsTo; }	
	public void setConnectsTo( Point connectsTo ) { this.connectsTo = connectsTo; }
}
