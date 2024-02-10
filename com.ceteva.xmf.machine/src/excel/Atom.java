package excel;

public class Atom extends Value {

	private String type;
	private Object value;

	public Atom(String type, Object value) {
		super();
		this.type  = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}

}
