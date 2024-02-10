package diagrams.model;

public class NamedType extends Type {

	public static NamedType STRING  = new NamedType("String");
	public static NamedType INTEGER = new NamedType("Integer");
	public static NamedType BOOLEAN = new NamedType("Boolean");
	public static NamedType FLOAT   = new NamedType("Float");
	public static NamedType ELEMENT   = new NamedType("Element");

	private String name;

	public NamedType(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	protected String getPlantUML() {
		return "<color:#Blue>" + name;
	}
	
	public String toString() {
		return name;
	}

}
