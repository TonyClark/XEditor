package diagrams.model;

public class List extends Type {

	private Type elementType;

	public List(Type elementType) {
		super();
		this.elementType = elementType;
	}

	public Type getElementType() {
		return elementType;
	}

	@Override
	protected String getPlantUML() {
		return "[" + elementType.getPlantUML() + "]";
	}

	public String toString() {
		return "[" + elementType + "]";
	}

}
