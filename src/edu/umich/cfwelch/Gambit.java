package edu.umich.cfwelch;

import java.util.ArrayList;
import java.util.HashMap;

public class Gambit {
	
	private ArrayList<Response> sequence;
	private int said = 1;
	
	public Gambit(ArrayList<Response> sequence) {
		this.sequence = sequence;
	}
	
	public Gambit() {
		this.sequence = new ArrayList<Response>();
	}
	
	public boolean hasNext(HashMap<String, DO> memory) throws ChatException {
		for (int i = 0; i < sequence.size(); i += 1) {
			if (sequence.get(i).canExecute(memory) && !sequence.get(i).wasSaid()) {
				return true;
			}
		}
		return false;
	}
	
	public Response getNext(HashMap<String, DO> memory) throws ChatException {
		while (this.hasNext(memory)) {
			for (int i = 0; i < sequence.size(); i += 1) {
				if (sequence.get(i).canExecute(memory) && !sequence.get(i).wasSaid() && sequence.get(i).getSaids() < said) {
					sequence.get(i).said();
					return sequence.get(i);
				}
			}
			said += 1;
		}
		return null;
	}
	
	public void add(Response resp) {
		sequence.add(resp);
	}

}
