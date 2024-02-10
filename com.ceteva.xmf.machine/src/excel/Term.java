package excel;

import java.util.Arrays;
import java.util.Vector;

public class Term extends Value{

	private String   name;
	private Value[] values;

	public Term(String name, Value... values) {
		super();
		this.name   = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public Value[] getValues() {
		return values;
	}
	
	public String toString() {
		return name + Arrays.toString(values);
	}

}
