package edu.umich.cfwelch;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Make generic with two types (1) condition (2) object to evaluate
public class Response {
	
	private HashMap<String, Response> sequence;
	private String text;
	private Operation<Double> condition;
	private String expression;
	private boolean repeatable;
	private boolean said = false;
	private int saids = 0;
	
	public int getSaids() {
		return this.saids;
	}
	
	public void said() {
		this.saids += 1;
	}
	
	public void printSequence() {
		System.out.println("---------------------keys----------------------");
		for (String s : sequence.keySet()) {
			System.out.println(s + ":" + sequence.get(s));
		}
	}
	
	public Response(String text, HashMap<String, Response> sequence) {
		this(text, sequence, null, null);
	}
	
	public Response(String text) {
		this(text, new HashMap<String, Response>());
	}
	
	public Response(String text, boolean repeatable) {
		this(text, new HashMap<String, Response>(), null, null, repeatable);
	}
	
	public Response(String text, HashMap<String, Response> sequence, boolean repeatable) {
		this(text, sequence, null, null, repeatable);
	}
	
	public Response(String text, HashMap<String, Response> sequence, Operation<Double> condition, String expression) {
		this(text, sequence, condition, expression, false);
	}
	
	public Response(String text, Operation<Double> condition, String expression, boolean repeatable) {
		this(text, new HashMap<String, Response>(), condition, expression, repeatable);
	}
	
	public Response(String text, HashMap<String, Response> sequence,
			Operation<Double> condition, String expression, boolean repeatable) {
		this.text = text;
		this.sequence = sequence;
		this.condition = condition;
		this.expression = expression;
		this.repeatable = repeatable;
	}
	
	public void push(String key, Response response) {
		sequence.put(key, response);
	}
	
	public String message(HashMap<String, DO> memory) throws ChatException {
		if (!repeatable) {
			said = true;
		}
		String out = lookup(memory, text);
		String[] parts = out.split("\\|");
		int index = (int) (Math.random() * parts.length);
		out = parts[index];
		return out;
	}
	
	public Response getNext(String input) {
		Response ret = null;
		for (String s : sequence.keySet()) {
			if (input.matches(s)) {
				ret = sequence.get(s);
			}
		}
		return ret;
	}
	
	public boolean canExecute(HashMap<String, DO> memory) throws ChatException {
		if (condition != null) {
			String alookup = lookup(memory, expression);
//			System.out.println("ALOOKUP: " + alookup);
			return condition.evaluate(Double.parseDouble(alookup));
		}
		return true;
	}
	
	// TODO: This is terrible - I will rewrite this function entirely.
	private String lookup(HashMap<String, DO> memory, String input) throws ChatException {
		String retext = input;
		Pattern pat = Pattern.compile("<.*?>");
		Matcher mat = pat.matcher(input);
		while (mat.find()) {
			if (mat.group().contains(":")) {
				// this is an action
				String whole = mat.group().substring(1, mat.group().length() - 1);
				String action = whole.substring(0, whole.indexOf(":"));
				boolean replace = false;
				String value = null;
				if ("move".equals(action)) {
//					System.out.println("ACTION: " + action);
					String rest = whole.substring(whole.indexOf(":") + 1);
					String[] param = rest.split(",");
					Pattern pat2 = Pattern.compile("\\(.*?\\)");
					Matcher mat2 = pat2.matcher(mat.group());
					if (mat2.find()) {
						String index = mat2.group();
						index = index.substring(1, index.length() - 1);
						String from = param[0].replace(mat2.group(), "");
						String to = param[1];
//						System.out.println("INDEX: " + index);
//						System.out.println("FROM: " + from);
//						System.out.println("TO: " + to);
						DO tmp = memory.get(from).remove(index);
						memory.get(to).add(tmp);
					} else {
						throw new ChatException();
					}
				} else if ("clear".equals(action)) {
					String rest = whole.substring(whole.indexOf(":") + 1);
					Pattern pat2 = Pattern.compile("\\(.*?\\)");
					Matcher mat2 = pat2.matcher(mat.group());
					if (mat2.find()) {
						String index = mat2.group();
						index = index.substring(1, index.length() - 1);
						String from = rest.replace(mat2.group(), "");
//						System.out.println("INDEX: " + index);
//						System.out.println("FROM: " + from);
						memory.get(from).get(index).values().clear();
//						System.out.println("MAPSIZE: " + memory.get(from).get(index).values().size());
					} else {
						throw new ChatException();
					}
				} else if ("add".equals(action)) {
					String rest = whole.substring(whole.indexOf(":") + 1);
					String[] param = rest.split(",");
					Pattern pat2 = Pattern.compile("\\(.*?\\)");
					Matcher mat2 = pat2.matcher(mat.group());
					if (mat2.find()) {
						String index = mat2.group();
						index = index.substring(1, index.length() - 1);
						String from = param[0].replace(mat2.group(), "");
						String to = param[1];
//						System.out.println("INDEX: " + index);
//						System.out.println("FROM: " + from);
//						System.out.println("TO: " + to);
						memory.get(from).get(index).add(new DO(to));
//						System.out.println("MZIE: " + memory.get(from).get(index).values().size());
					} else {
						throw new ChatException();
					}
				} else if ("size".equals(action)) {
					String rest = whole.substring(whole.indexOf(":") + 1);
					value = String.valueOf(memory.get(rest).values().size());
					replace = true;
				}
				if (replace) {
					retext = retext.replace(mat.group(), value);
				} else {
					retext = retext.replace(mat.group(), "");
				}
			} else {
				// this is a lookup
				Pattern pat2 = Pattern.compile("\\(.*?\\)");
				Matcher mat2 = pat2.matcher(mat.group());
				if (mat2.find()) {
					String index = mat2.group();
					index = index.substring(1, index.length() - 1);
					String group = mat.group().replace(mat2.group(), "");
					group = group.substring(1, group.length() - 1);
					String field = group.substring(group.indexOf("."));
					group = group.replace(field, "");
					field = field.substring(1);
//					System.out.println("INDEX: " + index);
//					System.out.println("GROUP: " + group);
//					System.out.println("FIELD: " + field);
					String torep = Pattern.quote(mat.group());
					String value = memory.get(group).get(index).get(field).value();
					retext = retext.replaceAll(torep, value);
				} else {
					throw new ChatException();
				}
			}
		}
		return retext;
	}
	
//	private static DO parseField(HashMap<String, DO> memory, String field) {
//		// NOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//		return memory.get(field);
//	}

	public boolean isRepeatable() {
		return repeatable;
	}
	
	public boolean wasSaid() {
		return said;
	}

}
