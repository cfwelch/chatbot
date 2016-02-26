package edu.umich.cfwelch;

@SuppressWarnings("rawtypes")
public class Operation<T extends Comparable> {
	
	public enum OpType {
		GT, LT, EQ
	}
	
	public Operation(OpType type, T threshold) {
		this.operation = type;
		this.threshold = threshold;
	}
	
	private OpType operation;
	private T threshold;
	
	@SuppressWarnings("unchecked")
	public boolean evaluate(T op2) {
		if (operation == OpType.GT) {
			return op2.compareTo(threshold) > 0;
		} else if (operation == OpType.LT) {
			return op2.compareTo(threshold) <= 0;
		} else if (operation == OpType.EQ) {
			return op2.compareTo(threshold) == 0;
		} else {
			return false;
		}
	}

}
