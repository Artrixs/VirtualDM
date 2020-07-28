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
package utils;

import java.util.HashMap;
import java.util.Map;

public class ID {
	private static String global = "Global";
	private static Map<String, ID> allIDs = new HashMap<String, ID>();
	
	private String location;
	private String name;
	
	public static ID newFromString(String string) {
		if ( !string.contains(":" ) )
			return newFromGlobalName(string);
		
		var tokens = string.split(":");
		if( tokens.length != 2 )
			throw new RuntimeException("ID: The string : \"" + string +"\" does not respect the format for IDS");
		return newFromLocationName(tokens[0], tokens[1]);
		
	}
	
	public static ID newFromLocationName(String location, String name) {
		String key = key(location, name);
		
		if ( allIDs.containsKey( key ) ) {
			throw new RuntimeException("ID: this ID already exists!");
		}
		
		var id = new ID(location, name);
		allIDs.put(key, id);
		return id;
	}
	
	public static ID newFromGlobalName( String name ) {
		return newFromLocationName(global, name);
	}
	
	public static ID getFromString( String string ) {
		if ( !string.contains(":") ) {
			return getFromGlobalName(string);
		}
		
		var tokens = string.split(":");
		if( tokens.length != 2 )
			throw new RuntimeException("ID: The string : \"" + string +"\" does not respect the format for IDS");
		return getFromLocationName(tokens[0], tokens[1]);
	}
	
	public static ID getFromLocationName( String location, String name ) {
		String key = key(location, name);
		
		if ( !allIDs.containsKey( key ) ) {
			throw new RuntimeException("ID: this ID does not exists!");
		}
		
		return allIDs.get(key);
	}
	
	public static ID getFromGlobalName( String name ) {
		return getFromLocationName( global, name );
	}
	
	private ID(String location, String name) {
		this.location = location;
		this.name = name;
	}
	
	public String getLocation() { return location; }
	public String getName() { return name; }
		
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ID other = (ID) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return key(location, name);
	}
	
	private static String key(String location, String name ) {
		return location.strip() + ":" + name.strip();
	}

}
