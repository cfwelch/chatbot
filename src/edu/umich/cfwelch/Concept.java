package edu.umich.cfwelch;

import java.util.ArrayList;

public class Concept {
	
	private ArrayList<String> matches;
	
	public Concept(ArrayList<String> matches) {
		this.matches = matches;
	}
	
	public boolean match(String input) {
		return matches.contains(input);
	}

}
