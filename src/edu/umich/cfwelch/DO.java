package edu.umich.cfwelch;

import java.util.HashMap;

/**
 * @author Charlie Welch
 * Represents a dynamic object that can store any values in memory.
 */
public class DO {
	
	private HashMap<String, DO> values;
	private String value;
	
	public DO(String value, HashMap<String, DO> values) {
		this.values = values;
		this.value = value;
	}
	
	public DO(String value) {
		this(value, new HashMap<String, DO>());
	}
	
	public DO get(String value) {
		if (values.containsKey(value)) {
			return values.get(value);
		} else {
			return null;
		}
	}
	
	public void add(DO toadd) {
		int ind = values.size();
		values.put(String.valueOf(ind), toadd);
	}
	
	public DO remove(String value) {
		DO retval = values.remove(value);
		try {
			int ind = Integer.parseInt(value);
			HashMap<String, DO> newvalues = new HashMap<String, DO>();
			for (String s : values.keySet()) {
				try {
					int nind = Integer.parseInt(s);
					if (ind < nind) {
						newvalues.put(String.valueOf(nind - 1), values.get(String.valueOf(nind)));
					}
				} catch (Exception e) {
					newvalues.put(s, values.get(s));
				}
			}
			values = newvalues;
		} catch (Exception e) {
			//OM NOM NOM
		}
		return retval;
	}
	
	public String value() {
		return value;
	}
	
	public HashMap<String, DO> values() {
		return this.values;
	}

}
