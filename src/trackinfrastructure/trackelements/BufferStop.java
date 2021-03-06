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

import utils.ID;

public class BufferStop extends SingleRouteTrackElement {
	
	public BufferStop( ID id, double length ) {
		super( id, Type.BUFFERSTOP, length );
		
	}

	@Override
	public void setConnection(char point, Point connectsTo ) {
		if(point == 'B')
			throw new RuntimeException("BufferStop: can connect only on one side");
		super.setConnection(point, connectsTo);
	}
	
	@Override
	public Point getPoint(char point) {
		if(point == 'B')
			throw new RuntimeException("BufferStop: connects only on one side");
		return super.getPoint(point);
	}

}
