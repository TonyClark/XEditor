package diagrams.model;

public class Arg {

	private String name;
	private Type   type;

	public Arg(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getPlantUML() {
		return "<color:#Black>" + name + ":<color:#Blue>" + type.getPlantUML();
	}

}
