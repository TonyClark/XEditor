package diagrams.model;

public class Inherits extends Edge {

	public Inherits(PackageElement source, PackageElement target, String label) {
		super(source, target, label);
		setType(EdgeType.INHERITS);
	}

	public String toString() {
		return "-|> " + getSource().getName() + " -> " + getTarget().getName();
	}

}
