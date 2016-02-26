package edu.umich.cfwelch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charlie Welch
 * Program main entry point for Xplore Engineering!
 * 
 */
public class MainModule {
	
//	private static final String PATH = "C:\\Users\\Ytaipsw\\Desktop\\";
	
	public static void main(String[] args) {
		// initalize variables
		String dempty = "I do not understand.";
		Topic topic = null;
		Response gambitResponse = null;
		Response rejoinder = null;
//		Student student = new Student();
//		ClassContext context = student.pushNewContext();
		HashMap<String, DO> memory = new HashMap<String, DO>();
		HashMap<String, DO> contextmap = new HashMap<String, DO>();
		memory.put("context", new DO("context", contextmap));
		ArrayList<Topic> topics = parseScript(memory, "Demo");
		topic = topics.get(0);
		// run chat
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("You: ");
			while (true) {
				String input = br.readLine();
				input = input.replaceAll("[^a-zA-Z! ]", "").toLowerCase();
				String output = null;
//				System.out.println("INPUT IS: " + input);
				if ("!build".equals(input.trim())) {
					topics = parseScript(memory, "Demo");
					output = "Bot has been rebuilt!";
				} else {
					// try to gambit response
					if (gambitResponse != null && gambitResponse.canExecute(memory)) {
						gambitResponse = gambitResponse.getNext(input);
						if (gambitResponse != null) {
							output = gambitResponse.message(memory);
						}
					}
					// try to rejoind
					if (rejoinder != null && rejoinder.canExecute(memory)) {
						rejoinder = rejoinder.getNext(input);
						if (rejoinder != null) {
							output = rejoinder.message(memory);
						}
					}
					// check current topic
					if (output == null && topic != null) {
						if (topic.hasMatch(input) && topic.respond(input).canExecute(memory)) {
							rejoinder = topic.respond(input);
							output = topic.respond(input).message(memory);
						}
					}
					// check all topics
					if (output == null) {
						for (Topic t : topics) {
							if (t.hasMatch(input) && t.respond(input).canExecute(memory)) {
								topic = t;
								rejoinder = t.respond(input);
								output = t.respond(input).message(memory);
								break;
							}
						}
					}
					// try to gambit
					if (output == null && topic != null && topic.hasGambit(memory)) {
						gambitResponse = topic.gambit(memory);
						output = gambitResponse.message(memory);
					}
					// default response
					if (output == null) {
						output = dempty;
					}
				}
				System.out.print("System: " + output + "\nYou: ");
//				printMemory(memory, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printMemory(HashMap<String, DO> memory, int depth) {
		for (String s : memory.keySet()) {
			for (int i = 0; i < depth; i += 1) {
				System.out.print("\t");
			}
			if (memory.get(s) != null) {
				System.out.println(s + ":" + memory.get(s).value());
				if (memory.get(s).values().size() > 0) {
					printMemory(memory.get(s).values(), depth + 1);
				}
			} else {
				System.out.println(s + ":null");
			}
		}
	}
	
	private static ArrayList<Topic> parseScript(HashMap<String, DO> memory, String folder) {
		ArrayList<Topic> retval = new ArrayList<Topic>();
		try {
			File fold = new File(folder);
			FileReader fr = new FileReader("test.top");
			BufferedReader br = new BufferedReader(fr);
			try {
				String temp = null;
				HashMap<String, Response> res = new HashMap<String, Response>();
				Stack<ActionType> parseStack = new Stack<ActionType>();
				int pdepth = 0;
				Gambit gambit = new Gambit();
				while ((temp = br.readLine()) != null) {
					// ignore whitespace lines
					if ("".equals(temp.trim())) {
						continue;
					}
					// get indentation
					int depth = 0;
					while (temp.charAt(0) == '\t') {
						temp = temp.substring(1);
						depth += 1;
//							System.out.println(temp);
					}
					String rest = temp.substring(temp.indexOf(":") + 1);
					char code = temp.charAt(0);
					boolean repeatable = true;//temp.charAt(1) == '*';
					// pop responses
					int ddepth = depth;
					while (ddepth < pdepth) {
						parseStack.pop();
						ddepth += 1;
					}
					// parse line
					if ('#' == code) {
						// commented line
						continue;
					} else if ('r' == code) {
						// parse basic response - duplicate code with g?
						Pattern pat = Pattern.compile("\\[.*?\\]");
						Matcher mat = pat.matcher(rest);
						Operation oA = null;
						String oB = null;
						if (mat.find()) {
							String operation = mat.group().substring(1);
							operation = operation.substring(0, operation.length() - 1);
							Operation.OpType type = null;
							String[] parts = operation.split(" ");
							String op = parts[1];
							if ("=".equals(op)) {
								type = Operation.OpType.EQ;
							} else if ("<".equals(op)) {
								type = Operation.OpType.LT;
							} else if (">".equals(op)) {
								type = Operation.OpType.GT;
							}
//								System.out.println(parts[0] + ":" + parts[1] + ":" + parts[2]);
							oA = new Operation(type, Double.parseDouble(parts[2]));
							oB = parts[0];
							rest = rest.replaceAll(Pattern.quote(mat.group()), "");
						}
						String ipat = rest.substring(1, rest.indexOf(")"));
						ipat = ipat.replaceAll(Pattern.quote("*"), ".*");
						ipat = ipat.replaceAll(
								"[^a-zA-Z! " + Pattern.quote(".*") + "]", "")
								.toLowerCase();
						String opat = rest.substring(rest.indexOf(")") + 1).trim();
						Response pres = null;
						if (oA == null) {
							pres = new Response(opat, repeatable);
						} else {
							pres = new Response(opat, oA, oB, repeatable);
						}
						if (depth == pdepth && parseStack.size() > 0) {
							parseStack.pop();
						}
						if (depth > 0) {
//								System.out.println("adding to : " + parseStack.peek().getResponse().message(memory) + ":" + ipat + ":" + opat);
							parseStack.peek().getResponse().push(ipat, pres);
						} else {
							res.put(ipat, pres);
						}
						parseStack.push(new ActionType("r", pres));
					} else if ('g' == code) {
						// parse gambit - duplicate code with r?
						Pattern pat = Pattern.compile("\\[.*?\\]");
						Matcher mat = pat.matcher(rest);
						Operation oA = null;
						String oB = null;
						if (mat.find()) {
							String operation = mat.group().substring(1);
							operation = operation.substring(0, operation.length() - 1);
							Operation.OpType type = null;
							String[] parts = operation.split(" ");
							String op = parts[1];
							if ("=".equals(op)) {
								type = Operation.OpType.EQ;
							} else if ("<".equals(op)) {
								type = Operation.OpType.LT;
							} else if (">".equals(op)) {
								type = Operation.OpType.GT;
							}
//								System.out.println(parts[0] + ":" + parts[1] + ":" + parts[2]);
							oA = new Operation(type, Double.parseDouble(parts[2]));
							oB = parts[0];
							rest = rest.replaceAll(Pattern.quote(mat.group()), "");
						}
						Response pres = null;
						if (oA == null) {
							pres = new Response(rest, repeatable);
						} else {
							pres = new Response(rest, oA, oB, repeatable);
						}
						if (depth == pdepth && parseStack.size() > 0) {
							parseStack.pop();
						}
						if (depth > 0) {
							System.out.println("NOT YET IMPLEMENTED");
						} else {
							gambit.add(pres);
						}
						parseStack.push(new ActionType("g", pres));
					} else if ('d' == code) {
						// parse data
						DO parsed = parseData(rest);
						memory.put(parsed.value(), parsed);
					} else {
						// parse failure
						throw new ChatException();
					}
					pdepth = depth;
				}
				retval.add(new Topic(res, gambit));
			} catch (ChatException f) {
				f.printStackTrace();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retval;
	}
	
	private static DO parseData(String data) {
		ArrayList<String> elements = new ArrayList<String>();
		String element = "";
		HashMap<String, DO> nest = null;
		if (data.indexOf("[") > -1) {
			String key = data.substring(0, data.indexOf("["));
			String values = data.substring(data.indexOf("[") + 1);
			values = values.substring(0, values.length() - 1);
//			System.out.println(key + ":" + values);
			int depth = 0;
			for (int i = 0; i < values.length(); i += 1) {
				if (values.charAt(i) != ',' || values.charAt(i) == ',' && depth > 0) {
					element += values.charAt(i);
				}
//				System.out.println(elements.size() + ":" + depth + ":  " + element);
				if (values.charAt(i) == '[') {
					depth += 1;
				} else if (values.charAt(i) == ']') {
					depth -= 1;
				} else if (values.charAt(i) == ',' && depth == 0) {
					elements.add(element);
					element = "";
				}
			}
			elements.add(element);
			element = "";
			nest = new HashMap<String, DO>();
			for (String s : elements) {
				String kkey = s.substring(0, s.indexOf("["));
				String vvalues = s.substring(s.indexOf("[") + 1);
				vvalues = vvalues.substring(0, vvalues.length() - 1);
				if (vvalues.contains("[")) {
					DO nestdo = parseData(s);
					nest.put(nestdo.value(), nestdo);
				} else {
					nest.put(kkey, new DO(vvalues));
				}
//				System.out.println("element: " + s);
			}
			return new DO(key, nest);
		} else {
			return new DO(data);
		}
	}

}
