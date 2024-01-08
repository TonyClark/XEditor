package diagrams.model;

import java.util.Vector;

public class FunType extends Type {

	private Vector<Type> domain;
	private Type         range;

	public FunType(Vector<Type> domain, Type range) {
		super();
		this.domain = domain;
		this.range  = range;
	}

	@Override
	protected String getPlantUML() {
		String s = "(";
		for(int i = 0; i < domain.size();i++) {
			s += domain.get(i).getPlantUML();
			if(i + 1 < domain.size()) {
				s += ",";
			}
		}
		return s + ") -> " + range.getPlantUML();
	}

}
