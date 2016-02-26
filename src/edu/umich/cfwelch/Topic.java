package edu.umich.cfwelch;

import java.util.HashMap;

/**
 * @author Charlie Welch
 * This object represents a topic in conversation. Responses trigger a topic change,
 * and the current topic determines priority of response patterns.
 * 
 * TODO: this is wrong you shouldn't have basic responses that repeat infinitely and 
 * gambits that only repeat once. this should use the response flag to decide whether 
 * each is repeatable or not.
 * 
 * TODO: gambits should be able to promt rejoinders and vice versa - not just nested
 * gambits within gambits. R-G patterns and G-R patterns.
 */
public class Topic {
	
	private HashMap<String, Response> patterns = new HashMap<String, Response>();
	private Gambit gambits = new Gambit();
	
	public Topic(HashMap<String, Response> patterns) {
		this.patterns = patterns;
	}
	
	public Topic(HashMap<String, Response> patterns, Gambit gambits) {
		this.patterns = patterns;
		this.gambits = gambits;
	}
	
	public void pushGambit(Response response) {
		gambits.add(response);
	}

	public boolean hasMatch(String input) throws ChatException {
		Response ret = null;
		for (String s : patterns.keySet()) {
			if (input.matches(s)) {
				ret = patterns.get(s);
			}
		}
		return ret != null;
	}
	
	public Response respond(String input) throws ChatException {
		Response ret = null;
		for (String s : patterns.keySet()) {
			if (input.matches(s)) {
				ret = patterns.get(s);
			}
		}
		return ret;
	}
	
	public boolean hasGambit(HashMap<String, DO> memory) throws ChatException {
		return gambits.hasNext(memory);
	}
	
	public Response gambit(HashMap<String, DO> memory) throws ChatException {
		return gambits.getNext(memory);
	}
	
}
