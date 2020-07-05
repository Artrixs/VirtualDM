package loader;

import java.util.HashMap;
import java.util.Map;
import utils.ID;

public class LoaderElement {
	
	static enum Type { TRACK }
	
	Type type;
	ID id;
	Map<String, Object> attributes;

	
	public LoaderElement(ID id, Type type) {
		this.id = id;
		this.type = type;
		attributes = new HashMap<String, Object>();
	}

	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	public boolean hasAttribute(String name) { return attributes.containsKey(name); } 
	public Object getAttribute(String name) { return attributes.get(name); }
	
	@Override
	public String toString() {
		String result = id + "\n";
		for( String name : attributes.keySet() ) {
			result += "\t" + name +"=" + attributes.get(name) + "\n";
		}
		return result;
	}
}
